package com.quizbackend

import com.quizbackend.contracts.generated.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.server.testing.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.client.call.body
import kotlin.test.*
import kotlinx.serialization.json.Json
import kotlinx.serialization.encodeToString
import org.junit.Test
import java.io.File
import java.io.FileWriter

class ComprehensiveApiTest {

    private val logFile = File("test_results_log.txt")

    init {
        // Clear log file at start
        if (logFile.exists()) logFile.delete()
        logFile.createNewFile()
    }

    private fun logResult(testName: String, input: Any?, status: HttpStatusCode, output: String, success: Boolean, error: String? = null) {
        val entry = """
            --- TEST: $testName ---
            RESULT: ${if (success) "PASS" else "FAIL"}
            INPUT: $input
            STATUS: $status
            OUTPUT: $output
            ERROR: ${error ?: "None"}
            -----------------------
        """.trimIndent()
        FileWriter(logFile, true).use { it.append(entry + "\n\n") }
        println(entry)
    }

    private val json = Json { ignoreUnknownKeys = true; prettyPrint = true }

    @Test
    fun testAllEndpoints() = testApplication {
        application {
            module()
        }

        val client = createClient {
            install(ContentNegotiation) {
                json(Json { ignoreUnknownKeys = true })
            }
        }

        // --- AUTH ---
        var authToken: String? = null
        val userEmail = "test${System.currentTimeMillis()}@example.com"
        val userPassword = "Password123!"

        // Helper to run test block safely
        suspend fun runTest(name: String, block: suspend () -> Unit) {
            try {
                block()
            } catch (e: Exception) {
                logResult(name, "See specific test logic", HttpStatusCode.InternalServerError, "Exception: ${e.message}", false, e.toString())
            } catch (e: AssertionError) {
                logResult(name, "See specific test logic", HttpStatusCode.InternalServerError, "AssertionError: ${e.message}", false, e.toString())
            }
        }

        // 1. Signup - Good Data
        runTest("Signup - Good Data") {
            val input = SignupRequestDTO(
                email = userEmail,
                username = "user${System.currentTimeMillis()}",
                name = "Test",
                surname = "User",
                passwordHash = userPassword
            )
            val response = client.post("/auth/signup") {
                contentType(ContentType.Application.Json)
                setBody(input)
            }
            val body = response.bodyAsText()
            val success = response.status == HttpStatusCode.OK
            logResult("Signup - Good Data", input, response.status, body, success)
        }

        // 2. Signup - Bad Data (Invalid Email)
        runTest("Signup - Bad Email") {
            val input = SignupRequestDTO(
                email = "invalid-email",
                username = "user${System.currentTimeMillis()}",
                name = "Test",
                surname = "User",
                passwordHash = userPassword
            )
            val response = client.post("/auth/signup") {
                contentType(ContentType.Application.Json)
                setBody(input)
            }
            val body = response.bodyAsText()
            // Expecting 400 or OK with success=false depending on implementation
            // Currently implementation seems loose, so we'll log it as fail if it returns OK for now to highlight it
            val success = response.status == HttpStatusCode.BadRequest
            logResult("Signup - Bad Email", input, response.status, body, success, if (!success) "Expected 400 BadRequest" else null)
        }

        // 3. Signup - Missing Data
        runTest("Signup - Missing Data") {
            val response = client.post("/auth/signup") {
                contentType(ContentType.Application.Json)
                setBody("{\"username\": \"test\", \"passwordHash\": \"pw\"}")
            }
            val body = response.bodyAsText()
            val success = response.status == HttpStatusCode.BadRequest
            logResult("Signup - Missing Data", "Missing email", response.status, body, success)
        }

        // 4. Login - Good Data
        runTest("Login - Good Data") {
            val input = LoginRequestDTO(
                email = userEmail,
                passwordHash = userPassword,
                uniqueId = "device1"
            )
            val response = client.post("/auth/login") {
                contentType(ContentType.Application.Json)
                setBody(input)
            }
            val body = response.bodyAsText()
            val success = response.status == HttpStatusCode.OK
            if (success) {
                val parsed = json.decodeFromString<DTOResponse<LoginResponseDTO>>(body)
                authToken = parsed.data?.token
            }
            logResult("Login - Good Data", input, response.status, body, success)
        }

        // 5. Login - Bad Password
        runTest("Login - Bad Password") {
            val input = LoginRequestDTO(
                email = userEmail,
                passwordHash = "wrongpass",
                uniqueId = "device1"
            )
            val response = client.post("/auth/login") {
                contentType(ContentType.Application.Json)
                setBody(input)
            }
            val body = response.bodyAsText()
            val success = response.status == HttpStatusCode.BadRequest || (response.status == HttpStatusCode.OK && body.contains("false"))
            logResult("Login - Bad Password", input, response.status, body, success)
        }

        // 6. Auth Protected Route - No Token
        runTest("Auth Status - No Token") {
            val response = client.get("/auth/status")
            val body = response.bodyAsText()
            val success = response.status == HttpStatusCode.Unauthorized
            logResult("Auth Status - No Token", null, response.status, body, success)
        }

        // 7. Auth Protected Route - Bad Token
        runTest("Auth Status - Bad Token") {
            val response = client.get("/auth/status") {
                header("Authorization", "Bearer invalidtoken")
            }
            val body = response.bodyAsText()
            val success = response.status == HttpStatusCode.Unauthorized
            logResult("Auth Status - Bad Token", "Bearer invalidtoken", response.status, body, success)
        }

        // 8. Auth Protected Route - Correct Token
        runTest("Auth Status - Correct Token") {
            val response = client.get("/auth/status") {
                header("Authorization", "Bearer $authToken")
            }
            val body = response.bodyAsText()
            val success = response.status == HttpStatusCode.OK
            logResult("Auth Status - Correct Token", null, response.status, body, success)
        }

        // --- PROFILE ---

        // 9. Get Profile
        runTest("Get Profile") {
            val response = client.get("/profile") {
                header("Authorization", "Bearer $authToken")
            }
            val body = response.bodyAsText()
            val success = response.status == HttpStatusCode.OK
            logResult("Get Profile", null, response.status, body, success)
        }

        // 10. Update Profile - Good Data
        runTest("Update Profile - Good Data") {
            val input = UpdateProfileRequestDTO(
                name = "UpdatedName",
                surname = "UpdatedSurname",
                username = "updateduser"
            )
            val response = client.put("/profile") {
                header("Authorization", "Bearer $authToken")
                contentType(ContentType.Application.Json)
                setBody(input)
            }
            val body = response.bodyAsText()
            val success = response.status == HttpStatusCode.OK
            logResult("Update Profile - Good Data", input, response.status, body, success)
        }

        // --- COLLECTIONS ---

        var collectionId: Int? = null

        // 11. Create Collection - Good Data
        runTest("Create Collection - Good Data") {
            val input = CreateCollectionRequestDTO(
                name = "My Collection",
                description = "Desc",
                isPublic = true
            )
            val response = client.post("/collections") {
                header("Authorization", "Bearer $authToken")
                contentType(ContentType.Application.Json)
                setBody(input)
            }
            val body = response.bodyAsText()
            val success = response.status == HttpStatusCode.OK
            if (success) {
                val parsed = json.decodeFromString<DTOResponse<IdDataResponseDTO>>(body)
                collectionId = parsed.data?.id
            }
            logResult("Create Collection - Good Data", input, response.status, body, success)
        }

        // 12. Get Collections (Public)
        runTest("Get Collections") {
            val response = client.get("/collections")
            val body = response.bodyAsText()
            val success = response.status == HttpStatusCode.OK
            logResult("Get Collections", null, response.status, body, success)
        }

        // 13. Update Collection
        if (collectionId != null) {
            runTest("Update Collection") {
                val input = UpdateCollectionRequestDTO(
                    name = "Updated Collection",
                    description = "New Desc",
                    isPublic = false
                )
                val response = client.put("/collections/$collectionId") {
                    header("Authorization", "Bearer $authToken")
                    contentType(ContentType.Application.Json)
                    setBody(input)
                }
                val body = response.bodyAsText()
                val success = response.status == HttpStatusCode.OK
                logResult("Update Collection", input, response.status, body, success)
            }
        }

        // --- QUESTIONS ---

        var questionId: Int? = null

        // 14. Create Question - Good Data
        runTest("Create Question - Good Data") {
            val input = CreateQuestionsRequestDTO(
                questions = listOf(
                    CreateQuestionInputDTO(
                        localizations = listOf(LocalizationDTO("en", "Is this a test?")),
                        answers = listOf(
                            CreateAnswerInputDTO(listOf(LocalizationDTO("en", "Yes"))),
                            CreateAnswerInputDTO(listOf(LocalizationDTO("en", "No")))
                        ),
                        correctAnswersIndices = listOf(0),
                        isDiscoverable = true,
                        collectionIds = if (collectionId != null) listOf(collectionId!!) else listOf()
                    )
                )
            )
            val response = client.post("/questions") {
                header("Authorization", "Bearer $authToken")
                contentType(ContentType.Application.Json)
                setBody(input)
            }
            val body = response.bodyAsText()
            val success = response.status == HttpStatusCode.OK
            logResult("Create Question - Good Data", input, response.status, body, success)
        }

        // 15. Get Questions List
        runTest("Get Questions List") {
            val response = client.get("/questions?page=0")
            val body = response.bodyAsText()
            val success = response.status == HttpStatusCode.OK
            logResult("Get Questions List", "page=0", response.status, body, success)
            if (success) {
                // Parse to find an ID to test GET /{id}
                try {
                    val parsed = json.decodeFromString<DTOResponse<QuestionListResponseDTO>>(body)
                    val questions = parsed.data?.questions
                    if (!questions.isNullOrEmpty()) {
                        questionId = questions[0].id
                    }
                } catch(e: Exception) {
                     logResult("Get Questions List Parse", null, HttpStatusCode.InternalServerError, e.message ?: "", false)
                }
            }
        }

        // 16. Get Question Detail
        if (questionId != null) {
            runTest("Get Question Detail") {
                val response = client.get("/questions/$questionId")
                val body = response.bodyAsText()
                val success = response.status == HttpStatusCode.OK
                logResult("Get Question Detail", "id=$questionId", response.status, body, success)
            }
        }

        // 17. Get Question Detail - Not Found
        runTest("Get Question Detail - Not Found") {
            val response = client.get("/questions/999999")
            val body = response.bodyAsText()
            // Should be 400 with QUESTION_NOT_FOUND or similar
            val success = response.status == HttpStatusCode.BadRequest
            logResult("Get Question Detail - Not Found", "id=999999", response.status, body, success)
        }

        // 18. Update Question
        if (questionId != null) {
            runTest("Update Question") {
                val input = UpdateQuestionRequestDTO(
                    localizations = listOf(LocalizationDTO("en", "Updated Question Text")),
                    answers = listOf(), // Minimal update
                    correctAnswersIndices = listOf(0),
                    isDiscoverable = false,
                    collectionIds = listOf()
                )
                val response = client.put("/questions/$questionId") {
                    header("Authorization", "Bearer $authToken")
                    contentType(ContentType.Application.Json)
                    setBody(input)
                }
                val body = response.bodyAsText()
                val success = response.status == HttpStatusCode.OK
                logResult("Update Question", input, response.status, body, success)
            }
        }

        // 19. Get Questions Batch
        if (questionId != null) {
            runTest("Get Questions Batch") {
                 val response = client.get("/questions/batch?ids=$questionId")
                 val body = response.bodyAsText()
                 val success = response.status == HttpStatusCode.OK
                 logResult("Get Questions Batch", "ids=$questionId", response.status, body, success)
            }
        }

        // --- COMMUNITIES ---

        // 20. Users List
        runTest("Get Users List") {
            val response = client.get("/communities/users") {
                header("Authorization", "Bearer $authToken")
            }
            val body = response.bodyAsText()
            val success = response.status == HttpStatusCode.OK
            logResult("Get Users List", null, response.status, body, success)
        }

        // 21. Friend Request (Mocked)
        runTest("Send Friend Request") {
            val input = SendFriendRequestDTO(targetUserId = 123)
            val response = client.post("/communities/friend-request") {
                header("Authorization", "Bearer $authToken")
                contentType(ContentType.Application.Json)
                setBody(input)
            }
            val body = response.bodyAsText()
            val success = response.status == HttpStatusCode.OK
            logResult("Send Friend Request", input, response.status, body, success)
        }

        // --- MARKS ---

        // 22. Get Marks
        runTest("Get Marks") {
             val response = client.get("/marks") {
                header("Authorization", "Bearer $authToken")
            }
            val body = response.bodyAsText()
            val success = response.status == HttpStatusCode.OK
            logResult("Get Marks", null, response.status, body, success)
        }

        // --- DEVICES ---

        // 23. Register Push Token
        runTest("Register Push Token") {
            val input = RegisterPushTokenRequestDTO(pushToken = "token_123")
            val response = client.post("/devices/push-token") {
                header("Authorization", "Bearer $authToken")
                contentType(ContentType.Application.Json)
                setBody(input)
            }
            val body = response.bodyAsText()
            val success = response.status == HttpStatusCode.OK
            logResult("Register Push Token", input, response.status, body, success)
        }

        // 24. Delete Push Token
        runTest("Delete Push Token") {
            val response = client.delete("/devices/push-token") {
                header("Authorization", "Bearer $authToken")
            }
            val body = response.bodyAsText()
            val success = response.status == HttpStatusCode.OK
            logResult("Delete Push Token", null, response.status, body, success)
        }

        // --- CLEANUP (Delete Account / Collection / Question) ---

        // 25. Delete Question
        if (questionId != null) {
            runTest("Delete Question") {
                 val response = client.delete("/questions/$questionId") {
                    header("Authorization", "Bearer $authToken")
                }
                val body = response.bodyAsText()
                val success = response.status == HttpStatusCode.OK
                logResult("Delete Question", "id=$questionId", response.status, body, success)
            }
        }

        // 26. Delete Collection
        if (collectionId != null) {
            runTest("Delete Collection") {
                 val response = client.delete("/collections/$collectionId") {
                    header("Authorization", "Bearer $authToken")
                }
                val body = response.bodyAsText()
                val success = response.status == HttpStatusCode.OK
                logResult("Delete Collection", "id=$collectionId", response.status, body, success)
            }
        }

        // 27. Delete Account
        runTest("Delete Account") {
            val response = client.delete("/auth/account") {
                header("Authorization", "Bearer $authToken")
            }
            val body = response.bodyAsText()
            val success = response.status == HttpStatusCode.OK
            logResult("Delete Account", null, response.status, body, success)
        }

        // 28. Logout / Login After Delete
        runTest("Login After Delete") {
             val input = LoginRequestDTO(
                email = userEmail,
                passwordHash = userPassword,
                uniqueId = "device1"
            )
            val response = client.post("/auth/login") {
                contentType(ContentType.Application.Json)
                setBody(input)
            }
            val body = response.bodyAsText()
            // Should be 400 Bad Request (Invalid credentials or user not found)
            val success = response.status == HttpStatusCode.BadRequest || (response.status == HttpStatusCode.OK && body.contains("false"))
            logResult("Login After Delete", input, response.status, body, success)
        }
    }
}
