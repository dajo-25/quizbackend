package com.quizbackend.features.communities

import com.quizbackend.contracts.generated.*
import com.quizbackend.features.users.Users
import kotlinx.coroutines.runBlocking
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.insertAndGetId
import org.jetbrains.exposed.sql.transactions.TransactionManager
import org.jetbrains.exposed.sql.transactions.transaction
import org.jetbrains.exposed.sql.Database
import java.io.File
import kotlin.test.*
import java.sql.Connection

class CommunitiesTest {

    private val dbFile = File("test_communities.db")

    @BeforeTest
    fun setup() {
        if (dbFile.exists()) {
            dbFile.delete()
        }
        Database.connect("jdbc:sqlite:${dbFile.absolutePath}", "org.sqlite.JDBC")
        TransactionManager.manager.defaultIsolationLevel = Connection.TRANSACTION_READ_UNCOMMITTED

        transaction {
            SchemaUtils.create(Users, FriendRequests)
        }
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

    class MockUserProvider(val userId: Int) : UserProvider {
        override suspend fun getUserId(): Int? = userId
    }

    @Test
    fun testSendFriendRequest() = runBlocking {
        val user1 = createUser("u1@test.com", "u1")
        val user2 = createUser("u2@test.com", "u2")

        val service = CommunitiesContractImpl(MockUserProvider(user1))

        // Send request
        val response = service.PostFriendRequest(SendFriendRequestDTO(user2), EmptyParamsDTO())
        assertTrue(response.success, "Send request failed: ${response.error}")
        assertNotNull(response.data?.friendRequest)
        assertEquals(user1, response.data!!.friendRequest!!.sender.id)
        assertEquals(user2, response.data!!.friendRequest!!.receiver.id)
        assertEquals("PENDING", response.data!!.friendRequest!!.status)

        // Verify request exists via GetFriendRequests
        val listResponse = service.GetFriendRequests(EmptyRequestDTO(), EmptyParamsDTO())
        assertTrue(listResponse.success)
        assertEquals(1, listResponse.data?.requests?.size)
        assertEquals(user2, listResponse.data?.requests?.first()?.receiver?.id)
    }

    @Test
    fun testRespondFriendRequest() = runBlocking {
        val user1 = createUser("u1@test.com", "u1")
        val user2 = createUser("u2@test.com", "u2")

        // User1 sends to User2
        val service1 = CommunitiesContractImpl(MockUserProvider(user1))
        val sendResponse = service1.PostFriendRequest(SendFriendRequestDTO(user2), EmptyParamsDTO())
        assertTrue(sendResponse.success, "Setup send failed")
        val requestId = sendResponse.data!!.friendRequest!!.id

        // User2 accepts
        val service2 = CommunitiesContractImpl(MockUserProvider(user2))
        val respondResponse = service2.PostRespond(RespondFriendRequestRequestDTO(requestId, true), EmptyParamsDTO())
        assertTrue(respondResponse.success, "Respond failed: ${respondResponse.error}")

        // Check status (requires GetFriendRequests or checking DB/Response)
        // GetFriendRequests for User1. Should be empty because only PENDING are returned.
        val listResponse = service1.GetFriendRequests(EmptyRequestDTO(), EmptyParamsDTO())
        assertEquals(0, listResponse.data?.requests?.size, "Should find no pending requests")
    }
}
