package com.quizbackend.features.quiz

import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.server.testing.*
import com.quizbackend.module
import com.quizbackend.features.auth.LoginRequest
import com.quizbackend.features.auth.AuthResponse
import com.quizbackend.features.auth.SignupRequest
import com.quizbackend.features.quiz.collections.CreateCollectionRequest
import com.quizbackend.features.quiz.collections.UpdateCollectionRequest
import com.quizbackend.features.quiz.collections.ShareCollectionRequest
import com.quizbackend.features.quiz.collections.CollectionDetailResponse
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.config.*
import java.io.File
import kotlin.test.*
import kotlinx.serialization.json.*

class CollectionsTest {

    private val dbFile = File("test_collections.db")

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
    fun testCollectionsLogic() = testApplication {
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

        // 1. Setup Users (User A and User B)
        val userAToken = signupAndLogin(client, "usera@test.com", "usera", "device-a")
        val userBToken = signupAndLogin(client, "userb@test.com", "userb", "device-b")

        // 2. User A creates a Private Collection
        val createColRes = client.post("/collections") {
            header(HttpHeaders.Authorization, "Bearer $userAToken")
            contentType(ContentType.Application.Json)
            setBody(CreateCollectionRequest(name = "User A Private", isPublic = false))
        }
        assertEquals(HttpStatusCode.Created, createColRes.status)
        // Parse ID from map response
        val responseText = createColRes.bodyAsText()
        // Assuming mapOf("id" to id) serialized to {"id": 1}
        val colIdA = Json.parseToJsonElement(responseText).jsonObject["id"]?.toString()?.toInt() ?: fail("No ID returned")

        // 3. User A creates an Undiscoverable Question linked to Private Collection
        val questionPayload = """
            {
                "questions": [
                    {
                        "localizations": [{"locale": "en", "text": "Secret Question"}],
                        "answers": [{"localizations": [{"locale": "en", "text": "42"}]}],
                        "correctAnswersIndices": [0],
                        "isDiscoverable": false,
                        "collectionIds": [$colIdA]
                    }
                ]
            }
        """.trimIndent()

        val createQRes = client.post("/questions") {
            header(HttpHeaders.Authorization, "Bearer $userAToken")
            contentType(ContentType.Application.Json)
            setBody(questionPayload)
        }
        assertEquals(HttpStatusCode.Created, createQRes.status)

        // 4. Verify User B cannot see the question in /questions
        val listQRes = client.get("/questions") {
            header(HttpHeaders.Authorization, "Bearer $userBToken")
        }
        assertFalse(listQRes.bodyAsText().contains("Secret Question"))

        // 5. Verify User B cannot access the collection
        val getColRes = client.get("/collections/$colIdA") {
             header(HttpHeaders.Authorization, "Bearer $userBToken")
        }
        assertEquals(HttpStatusCode.Forbidden, getColRes.status)

        // 6. User A shares collection with User B
        val shareRes = client.post("/collections/$colIdA/share") {
            header(HttpHeaders.Authorization, "Bearer $userAToken")
            contentType(ContentType.Application.Json)
            setBody(ShareCollectionRequest(userEmail = "userb@test.com"))
        }
        assertEquals(HttpStatusCode.OK, shareRes.status)

        // 7. Verify User B can now access the collection
        val getColResShared = client.get("/collections/$colIdA") {
             header(HttpHeaders.Authorization, "Bearer $userBToken")
        }
        assertEquals(HttpStatusCode.OK, getColResShared.status)
        assertTrue(getColResShared.bodyAsText().contains("User A Private"))

        // 8. User A updates collection to Public
        val updateRes = client.put("/collections/$colIdA") {
            header(HttpHeaders.Authorization, "Bearer $userAToken")
            contentType(ContentType.Application.Json)
            setBody(UpdateCollectionRequest(isPublic = true))
        }
        assertEquals(HttpStatusCode.OK, updateRes.status)

        // 9. Verify Question becomes discoverable for User B (in /questions listing)
        val listQResPublic = client.get("/questions") {
            header(HttpHeaders.Authorization, "Bearer $userBToken")
        }
        assertTrue(listQResPublic.bodyAsText().contains("Secret Question"))

        // 10. Test validation: Cannot create undiscoverable question without collection
        val invalidQPayload = """
            {
                "questions": [
                    {
                        "localizations": [{"locale": "en", "text": "Invalid Secret"}],
                        "answers": [{"localizations": [{"locale": "en", "text": "X"}]}],
                        "correctAnswersIndices": [0],
                        "isDiscoverable": false,
                        "collectionIds": []
                    }
                ]
            }
        """.trimIndent()
        val invalidQRes = client.post("/questions") {
            header(HttpHeaders.Authorization, "Bearer $userAToken")
            contentType(ContentType.Application.Json)
            setBody(invalidQPayload)
        }
        assertEquals(HttpStatusCode.BadRequest, invalidQRes.status)
    }

    private suspend fun signupAndLogin(client: io.ktor.client.HttpClient, email: String, user: String, device: String): String {
        client.post("/auth/signup") {
            contentType(ContentType.Application.Json)
            setBody(SignupRequest(email, user, "Name", "Surname", "pw"))
        }
        val res = client.post("/auth/login") {
            contentType(ContentType.Application.Json)
            setBody(LoginRequest(email, "pw", device))
        }
        return res.body<AuthResponse>().token!!
    }
}
