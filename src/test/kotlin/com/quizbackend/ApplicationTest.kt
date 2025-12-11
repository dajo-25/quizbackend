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

class ApplicationTest {
    @Test
    fun testRoot() = testApplication {
        application {
            module()
        }

        val client = createClient {
            install(ContentNegotiation) {
                json()
            }
        }

        // Test Signup
        val signupResponse = client.post("/auth/signup") {
            contentType(ContentType.Application.Json)
            setBody(SignupRequestDTO(
                email = "unique_test_user_${System.currentTimeMillis()}@example.com",
                username = "testuser_${System.currentTimeMillis()}",
                name = "Test",
                surname = "User",
                passwordHash = "hash123"
            ))
        }
        println("Signup Status: ${signupResponse.status}")
        val signupText = signupResponse.bodyAsText()
        println("Signup Body: $signupText")
        assertEquals(HttpStatusCode.OK, signupResponse.status)
        // Signup now returns LoginResponseDTO, not GenericResponseDTO
        val signupBody = Json.decodeFromString<DTOResponse<LoginResponseDTO>>(signupText)
        assertTrue(signupBody.success, "Signup failed: ${signupBody.error}")
        assertNotNull(signupBody.data?.token, "Signup should return a token")

        // Test Login
        val loginResponse = client.post("/auth/login") {
            contentType(ContentType.Application.Json)
            setBody(LoginRequestDTO(
                email = "test@example.com",
                passwordHash = "hash123",
                uniqueId = "device1"
            ))
        }
        println("Login Status: ${loginResponse.status}")
        val loginText = loginResponse.bodyAsText()
        println("Login Body: $loginText")
        assertEquals(HttpStatusCode.OK, loginResponse.status)
        val loginBody = Json.decodeFromString<DTOResponse<LoginResponseDTO>>(loginText)
        assertTrue(loginBody.success, "Login failed: ${loginBody.error}")
        assertNotNull(loginBody.data?.token)
    }
}
