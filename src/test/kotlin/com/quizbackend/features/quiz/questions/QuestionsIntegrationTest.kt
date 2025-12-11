package com.quizbackend.features.quiz.questions

import com.quizbackend.contracts.generated.*
import com.quizbackend.features.quiz.questions.models.Answers
import com.quizbackend.features.quiz.questions.models.Localizations
import com.quizbackend.features.quiz.questions.models.Questions
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction
import java.io.File
import kotlin.test.*

class QuestionsIntegrationTest {

    private lateinit var dbFile: File

    @BeforeTest
    fun setup() {
        // Create a temporary file for the database
        dbFile = File.createTempFile("test_db_", ".sqlite")

        // Connect to the file-based DB
        Database.connect("jdbc:sqlite:${dbFile.absolutePath}", "org.sqlite.JDBC")

        transaction {
            SchemaUtils.create(Questions, Answers, Localizations)
        }
    }

    @AfterTest
    fun tearDown() {
        // Close connections (if possible/needed) and delete file
        // SQLite driver doesn't have a clean "shutdown" via Exposed easily,
        // but we can try to delete the file. Windows might lock it, but Linux (Docker) should be fine.
        transaction {
             // Drop to ensure clean state if file deletion fails
             SchemaUtils.drop(Questions, Answers, Localizations)
        }
        dbFile.delete()
    }

    @Test
    fun testCreateAndGetQuestion() {
        val domainService = QuestionsDomainService()
        val qDto = CreateQuestionInputDTO(
            localizations = listOf(LocalizationDTO("en", "What is 2+2?"), LocalizationDTO("es", "¿Cuanto es 2+2?")),
            answers = listOf(
                CreateAnswerInputDTO(listOf(LocalizationDTO("en", "4"), LocalizationDTO("es", "4"))),
                CreateAnswerInputDTO(listOf(LocalizationDTO("en", "5"), LocalizationDTO("es", "5")))
            ),
            correctAnswersIndices = listOf(0),
            isDiscoverable = true,
            collectionIds = listOf()
        )

        val created = kotlinx.coroutines.runBlocking {
            domainService.createQuestions(null, listOf(qDto))
        }
        assertTrue(created, "Question creation should return true")

        val questions = kotlinx.coroutines.runBlocking {
            domainService.getQuestions(1, 10, "en")
        }

        assertEquals(1, questions.size, "Should have 1 question")
        assertEquals("What is 2+2?", questions[0].text)
        assertEquals(2, questions[0].answers.size)
        assertEquals("4", questions[0].answers.find { it.isCorrect }?.text)

        // Test ES locale
        val questionsEs = kotlinx.coroutines.runBlocking {
            domainService.getQuestions(1, 10, "es")
        }
        assertEquals("¿Cuanto es 2+2?", questionsEs[0].text)
        assertEquals("4", questionsEs[0].answers.find { it.isCorrect }?.text)
    }

    @Test
    fun testUpdateQuestion() {
        val domainService = QuestionsDomainService()
        // Create first
        val qDto = CreateQuestionInputDTO(
            localizations = listOf(LocalizationDTO("en", "Original")),
            answers = listOf(),
            correctAnswersIndices = listOf(),
            isDiscoverable = true,
            collectionIds = listOf()
        )
        kotlinx.coroutines.runBlocking { domainService.createQuestions(null, listOf(qDto)) }
        val id = kotlinx.coroutines.runBlocking { domainService.getQuestions(1, 10, "en").first().id }

        // Update
        val updateDto = UpdateQuestionRequestDTO(
            localizations = listOf(LocalizationDTO("en", "Updated")),
            answers = listOf(
                UpdateAnswerInputDTO(0, listOf(LocalizationDTO("en", "New Answer")))
            ),
            correctAnswersIndices = listOf(0),
            isDiscoverable = false,
            collectionIds = listOf()
        )

        val updated = kotlinx.coroutines.runBlocking { domainService.updateQuestion(id, updateDto) }
        assertTrue(updated)

        val q = kotlinx.coroutines.runBlocking { domainService.getQuestionById(id, "en") }
        assertNotNull(q)
        assertEquals("Updated", q.text)
        assertEquals(1, q.answers.size)
        assertEquals("New Answer", q.answers[0].text)
    }

    @Test
    fun testDeleteQuestion() {
        val domainService = QuestionsDomainService()
        val qDto = CreateQuestionInputDTO(
            localizations = listOf(LocalizationDTO("en", "To Delete")),
            answers = listOf(CreateAnswerInputDTO(listOf(LocalizationDTO("en", "A")))),
            correctAnswersIndices = listOf(0),
            isDiscoverable = true,
            collectionIds = listOf()
        )
        kotlinx.coroutines.runBlocking { domainService.createQuestions(null, listOf(qDto)) }
        val id = kotlinx.coroutines.runBlocking { domainService.getQuestions(1, 10, "en").first().id }

        val deleted = kotlinx.coroutines.runBlocking { domainService.deleteQuestion(id) }
        assertTrue(deleted)

        val q = kotlinx.coroutines.runBlocking { domainService.getQuestionById(id, "en") }
        assertNull(q)

        // Verify cascade/cleanup
        transaction {
            assertEquals(0, Localizations.selectAll().count())
            assertEquals(0, Answers.selectAll().count())
        }
    }
}
