package com.quizbackend.features.auth

import com.quizbackend.module
import com.quizbackend.utils.CaesarCipher
import io.ktor.client.call.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.config.*
import io.ktor.server.testing.*
import java.io.File
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class AuthIntegrationTest {

    private val dbFile = File("test_auth.db")

    @BeforeTest
    fun setup() {
        if (dbFile.exists()) {
            dbFile.delete()
        }
    }

    @AfterTest
    fun tearDown() {
        if (dbFile.exists()) {
            dbFile.delete()
        }
    }

    @Test
    fun testAuthFlow() = testApplication {
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

        // 1. Signup
        val signupRes = client.post("/auth/signup") {
            contentType(ContentType.Application.Json)
            setBody(SignupRequest("test@test.com", "tester", "Test", "User", "hashedpassword"))
        }
        assertEquals(HttpStatusCode.Created, signupRes.status)

        // 2. Verify Email (Simulate clicking link)
        val encryptedEmail = CaesarCipher.encryptToUrlSafe("test@test.com")
        val verifyRes = client.post("/auth/verify") {
             contentType(ContentType.Application.Json)
             setBody(VerifyRequest(encryptedEmail))
        }
        assertEquals(HttpStatusCode.OK, verifyRes.status)

        // 3. Login
        val loginRes = client.post("/auth/login") {
            contentType(ContentType.Application.Json)
            setBody(LoginRequest("test@test.com", "hashedpassword", "device-123"))
        }
        assertEquals(HttpStatusCode.OK, loginRes.status)
        val token = loginRes.body<AuthResponse>().token
        assertNotNull(token)

        // 4. Check Status (Authenticated)
        val statusRes = client.get("/auth/status") {
            header("Authorization", "Bearer $token")
        }
        assertEquals(HttpStatusCode.OK, statusRes.status)
        val status = statusRes.body<UserStatusResponse>()
        assertEquals("test@test.com", status.email)
        assertTrue(status.isVerified)

        // 5. Check Device Push Token Status (Initial state)
        val deviceRes = client.get("/devices/push-token-status") {
            header("Authorization", "Bearer $token")
        }
        assertEquals(HttpStatusCode.OK, deviceRes.status)

        // 5.5 Update Push Token
        val pushRes = client.post("/devices/push-token") {
            header("Authorization", "Bearer $token")
            contentType(ContentType.Application.Json)
            setBody(mapOf("pushToken" to "new-push-token"))
        }
        assertEquals(HttpStatusCode.OK, pushRes.status)

        val deviceResAfter = client.get("/devices/push-token-status") {
            header("Authorization", "Bearer $token")
        }
        val hasToken = deviceResAfter.body<Map<String, Boolean>>()["has_push_token"]
        assertTrue(hasToken == true)

        // 6. Logout
        val logoutRes = client.post("/auth/logout") {
            header("Authorization", "Bearer $token")
        }
        assertEquals(HttpStatusCode.OK, logoutRes.status)

        // 7. Check Status (Should be Unauthorized now as device is disabled)
        val statusResAfterLogout = client.get("/auth/status") {
            header("Authorization", "Bearer $token")
        }
        assertEquals(HttpStatusCode.Unauthorized, statusResAfterLogout.status)
    }

    @Test
    fun testPasswordRecovery() = testApplication {
        val recDbFile = File("test_rec.db")
        if (recDbFile.exists()) recDbFile.delete()

        environment {
             config = MapApplicationConfig("storage.jdbcUrl" to "jdbc:sqlite:${recDbFile.absolutePath}")
        }
        application {
            module()
        }

        val client = createClient {
            install(ContentNegotiation) {
                json()
            }
        }

        // Setup user
        client.post("/auth/signup") {
            contentType(ContentType.Application.Json)
            setBody(SignupRequest("recover@test.com", "rec", "Rec", "Over", "oldhash"))
        }

        // Recover
        val recRes = client.post("/auth/recover") {
             contentType(ContentType.Application.Json)
             setBody(RecoverRequest("recover@test.com"))
        }
        assertEquals(HttpStatusCode.OK, recRes.status)

        val loginOldRes = client.post("/auth/login") {
             contentType(ContentType.Application.Json)
             setBody(LoginRequest("recover@test.com", "oldhash", "device-123"))
        }
        assertEquals(HttpStatusCode.Unauthorized, loginOldRes.status)

        if (recDbFile.exists()) recDbFile.delete()
    }

    @Test
    fun testCaesarCipher() {
        val original = "test@example.com"
        val encrypted = CaesarCipher.encryptToUrlSafe(original)
        val decrypted = CaesarCipher.decryptFromUrlSafe(encrypted)

        assertEquals(original, decrypted)
    }
}
