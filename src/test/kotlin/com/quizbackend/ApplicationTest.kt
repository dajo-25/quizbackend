package com.quizbackend.tests

import io.ktor.client.call.*
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
import com.quizbackend.features.auth.LoginRequest
import com.quizbackend.features.auth.AuthResponse
import com.quizbackend.features.auth.SignupRequest
import com.quizbackend.features.users.UsersService
import com.quizbackend.features.devices.DevicesService
import com.quizbackend.services.notification.MockEmailSender
import com.quizbackend.features.auth.AuthService
import io.ktor.server.config.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.serialization.kotlinx.json.*
import java.io.File
import kotlin.test.*

class ApplicationTest {

    private val dbFile = File("test_quiz.db")

    @BeforeTest
    fun setup() {
        if (dbFile.exists()) {
            dbFile.delete()
        }
    }

    @AfterTest
    fun teardown() {
        if (dbFile.exists()) {
            dbFile.delete()
        }
    }

    @Test
    fun testRoot() = testApplication {
        environment {
            config = MapApplicationConfig("storage.jdbcUrl" to "jdbc:sqlite:${dbFile.absolutePath}")
        }
        application {
            module()
        }

        val client = createClient {
             install(ContentNegotiation) {
                json()
            }
        }

        // 1. Setup Auth (Create User & Login to get Token)
        val signupRes = client.post("/auth/signup") {
            contentType(ContentType.Application.Json)
            setBody(SignupRequest("quizuser@test.com", "quizzer", "Quiz", "User", "hashedpw"))
        }
        assertEquals(HttpStatusCode.Created, signupRes.status)

        val loginRes = client.post("/auth/login") {
            contentType(ContentType.Application.Json)
            setBody(LoginRequest("quizuser@test.com", "hashedpw", "device-test"))
        }
        assertEquals(HttpStatusCode.OK, loginRes.status)
        val validToken = loginRes.body<AuthResponse>().token
        assertNotNull(validToken)

        // 2. Test POST /questions without Auth -> Should be 401
        val noAuthPost = client.post("/questions") {
             contentType(ContentType.Application.Json)
             setBody("{}")
        }
        assertEquals(HttpStatusCode.Unauthorized, noAuthPost.status)

        // 3. Test GET /questions without Auth -> Should be 401
        val noAuthGet = client.get("/questions")
        assertEquals(HttpStatusCode.Unauthorized, noAuthGet.status)

        // 4. Test POST /questions with Valid Auth
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

        // 5. Test GET /questions with Valid Auth (English)
        val getResponseEn = client.get("/questions?locale=en") {
            header(HttpHeaders.Authorization, "Bearer $validToken")
        }
        assertEquals(HttpStatusCode.OK, getResponseEn.status)
        val responseBodyEn = getResponseEn.bodyAsText()
        assertTrue(responseBodyEn.contains("What is 2+2?"))
        assertTrue(responseBodyEn.contains("4"))
        assertTrue(responseBodyEn.contains("\"isCorrect\": true") || responseBodyEn.contains("\"isCorrect\":true"))

        // 6. Test GET /questions with Valid Auth (Spanish)
        val getResponseEs = client.get("/questions?locale=es") {
            header(HttpHeaders.Authorization, "Bearer $validToken")
        }
        assertEquals(HttpStatusCode.OK, getResponseEs.status)
        val responseBodyEs = getResponseEs.bodyAsText()
        assertTrue(responseBodyEs.contains("¿Cuánto es 2+2?"))
    }
}
