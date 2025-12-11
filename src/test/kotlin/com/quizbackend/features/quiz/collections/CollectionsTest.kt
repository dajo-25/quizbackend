package com.quizbackend.features.quiz.collections

import com.quizbackend.contracts.generated.*
import com.quizbackend.features.quiz.collections.models.CollectionAccess
import com.quizbackend.features.quiz.collections.models.CollectionQuestions
import com.quizbackend.features.quiz.collections.models.Collections
import com.quizbackend.features.quiz.questions.models.Questions
import com.quizbackend.features.users.Users
import kotlinx.coroutines.runBlocking
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.insertAndGetId
import org.jetbrains.exposed.sql.transactions.TransactionManager
import org.jetbrains.exposed.sql.transactions.transaction
import org.jetbrains.exposed.sql.Database
import java.sql.Connection
import kotlin.test.*
import java.io.File

class CollectionsTest {

    private val dbFile = File("test_collections.db")

    @BeforeTest
    fun setup() {
        if (dbFile.exists()) {
            dbFile.delete()
        }
        // Connect to file database
        Database.connect("jdbc:sqlite:${dbFile.absolutePath}", "org.sqlite.JDBC")
        TransactionManager.manager.defaultIsolationLevel = Connection.TRANSACTION_READ_UNCOMMITTED

        transaction {
            SchemaUtils.create(Users, Collections, Questions, CollectionQuestions, CollectionAccess)
        }
    }

    @AfterTest
    fun tearDown() {
        // Close connections? Exposed doesn't have explicit close on Database.
        // But we can try to delete the file.
        // SQLite lock might prevent deletion if connection is open.
        // For now, we just rely on overwrite in setup.
    }

    private fun createUser(email: String, username: String): Int {
        return transaction {
            Users.insertAndGetId {
                it[Users.email] = email
                it[Users.username] = username
                it[Users.name] = "Test"
                it[Users.surname] = "User"
                it[Users.passwordHash] = "hash"
            }.value
        }
    }

    private fun createQuestion(text: String): Int {
         return transaction {
            Questions.insertAndGetId {
                it[Questions.possibleAnswersIds] = ""
                it[Questions.correctAnswers] = ""
                it[Questions.creatorId] = 1
                it[Questions.isDiscoverable] = true
            }.value
        }
    }

    class MockUserProvider(val userId: Int) : UserProvider {
        override suspend fun getUserId(): Int? = userId
    }

    @Test
    fun testCreateAndGetCollection() = runBlocking {
        val userId = createUser("test@example.com", "testuser")

        val service = CollectionsContractImpl(MockUserProvider(userId))

        // Create
        val createDto = CreateCollectionRequestDTO(
            name = "My Collection",
            description = "Description",
            isPublic = false,
            questionIds = emptyList()
        )
        val createResponse = service.PostCollections(createDto, EmptyParamsDTO())
        assertTrue(createResponse.success)
        val collectionId = createResponse.data?.id ?: -1

        // Get List
        val listResponse = service.GetCollections(EmptyRequestDTO(), EmptyParamsDTO())
        assertTrue(listResponse.success)
        assertEquals(1, listResponse.data?.collections?.size)
        assertEquals("My Collection", listResponse.data?.collections?.first()?.name)

        // Get Detail
        val detailResponse = service.GetCollectionsId(EmptyRequestDTO(), UpdateCollectionParamsDTO(collectionId))
        assertTrue(detailResponse.success)
        assertEquals(collectionId, detailResponse.data?.collection?.id)
    }

    @Test
    fun testCollectionWithQuestions() = runBlocking {
        val userId = createUser("test@example.com", "testuser")
        val q1 = createQuestion("Q1")
        val q2 = createQuestion("Q2")

        val service = CollectionsContractImpl(MockUserProvider(userId))

        // Create with questions
        val createDto = CreateCollectionRequestDTO(
            name = "Quiz 1",
            description = "Desc",
            isPublic = true,
            questionIds = listOf(q1, q2)
        )
        val createResponse = service.PostCollections(createDto, EmptyParamsDTO())
        val collectionId = createResponse.data!!.id

        // Verify questions
        val detailResponse = service.GetCollectionsId(EmptyRequestDTO(), UpdateCollectionParamsDTO(collectionId))
        val questions = detailResponse.data?.collection?.questionIds
        assertEquals(2, questions?.size)
        assertTrue(questions!!.contains(q1))
        assertTrue(questions!!.contains(q2))

        // Update questions (remove one)
        val updateDto = UpdateCollectionRequestDTO(
            name = "Quiz 1 Updated",
            description = "Desc",
            isPublic = true,
            questionIds = listOf(q1)
        )
        val updateResponse = service.PutCollectionsId(updateDto, UpdateCollectionParamsDTO(collectionId))
        assertTrue(updateResponse.success)

        val detailResponse2 = service.GetCollectionsId(EmptyRequestDTO(), UpdateCollectionParamsDTO(collectionId))
        val questions2 = detailResponse2.data?.collection?.questionIds
        assertEquals(1, questions2?.size)
        assertTrue(questions2!!.contains(q1))
    }
}
