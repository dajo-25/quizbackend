package com.quizbackend.tests

import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.server.testing.*
import com.quizbackend.module
import com.quizbackend.plugins.configureDatabases
import com.quizbackend.plugins.configureSerialization
import com.quizbackend.plugins.configureSecurity
import com.quizbackend.features.quiz.questions.creation.questionsCreationRoutes
import com.quizbackend.features.quiz.questions.listing.questionsListingRoutes
import io.ktor.server.routing.routing
import kotlin.test.*
import java.io.File
import org.junit.After
import org.junit.Before

class ApplicationTest {

    private val dbFile = File("test_quiz.db")

    @Before
    fun setup() {
        if (dbFile.exists()) {
            dbFile.delete()
        }
    }

    @After
    fun teardown() {
        if (dbFile.exists()) {
            dbFile.delete()
        }
    }

    @Test
    fun testRoot() = testApplication {
        application {
             // Use a file-based database for testing to ensure persistence across connections
            configureSerialization()
            configureDatabases("jdbc:sqlite:${dbFile.absolutePath}")
            configureSecurity()
            routing {
                questionsCreationRoutes()
                questionsListingRoutes()
            }
        }

        // 1. Test POST /questions without Auth -> Should be 401
        val noAuthPost = client.post("/questions") {
             contentType(ContentType.Application.Json)
             setBody("{}")
        }
        assertEquals(HttpStatusCode.Unauthorized, noAuthPost.status)

        // 2. Test GET /questions without Auth -> Should be 401
        val noAuthGet = client.get("/questions")
        assertEquals(HttpStatusCode.Unauthorized, noAuthGet.status)

        // 3. Test POST /questions with Valid Auth
        val validToken = "super-secret-token"

        val createPayload = """
            {
                "questions": [
                    {
                        "localizations": [
                            {"locale": "en", "text": "What is 2+2?"},
                            {"locale": "es", "text": "¿Cuánto es 2+2?"}
                        ],
                        "answers": [
                            {
                                "localizations": [
                                    {"locale": "en", "text": "3"},
                                    {"locale": "es", "text": "3"}
                                ]
                            },
                            {
                                "localizations": [
                                    {"locale": "en", "text": "4"},
                                    {"locale": "es", "text": "4"}
                                ]
                            }
                        ],
                        "correctAnswersIndices": [1]
                    }
                ]
            }
        """.trimIndent()

        val postResponse = client.post("/questions") {
            header(HttpHeaders.Authorization, "Bearer $validToken")
            contentType(ContentType.Application.Json)
            setBody(createPayload)
        }
        assertEquals(HttpStatusCode.Created, postResponse.status)

        // 4. Test GET /questions with Valid Auth (English)
        val getResponseEn = client.get("/questions?locale=en") {
            header(HttpHeaders.Authorization, "Bearer $validToken")
        }
        assertEquals(HttpStatusCode.OK, getResponseEn.status)
        val responseBodyEn = getResponseEn.bodyAsText()
        assertTrue(responseBodyEn.contains("What is 2+2?"))
        assertTrue(responseBodyEn.contains("4"))

        // 5. Test GET /questions with Valid Auth (Spanish)
        val getResponseEs = client.get("/questions?locale=es") {
            header(HttpHeaders.Authorization, "Bearer $validToken")
        }
        assertEquals(HttpStatusCode.OK, getResponseEs.status)
        val responseBodyEs = getResponseEs.bodyAsText()
        assertTrue(responseBodyEs.contains("¿Cuánto es 2+2?"))

        // 6. Test Validation Failure (Inconsistent Locales)
        val invalidPayload = """
            {
                "questions": [
                    {
                        "localizations": [
                            {"locale": "en", "text": "Q1"}
                        ],
                        "answers": [
                            {
                                "localizations": [
                                    {"locale": "es", "text": "A1"}
                                ]
                            }
                        ],
                        "correctAnswersIndices": [0]
                    }
                ]
            }
        """.trimIndent()

        val invalidPostResponse = client.post("/questions") {
            header(HttpHeaders.Authorization, "Bearer $validToken")
            contentType(ContentType.Application.Json)
            setBody(invalidPayload)
        }
        assertEquals(HttpStatusCode.BadRequest, invalidPostResponse.status)
    }
}
