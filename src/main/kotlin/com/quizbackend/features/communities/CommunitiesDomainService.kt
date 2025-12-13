package com.quizbackend.features.communities

import com.quizbackend.contracts.generated.*
import com.quizbackend.features.users.Users
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.transactions.transaction

sealed class SendRequestResult {
    data class Success(val data: FriendRequestDataDTO) : SendRequestResult()
    data class Error(val error: ErrorType) : SendRequestResult()
}

class CommunitiesDomainService {

    companion object {
        const val STATUS_PENDING = "PENDING"
        const val STATUS_ACCEPTED = "ACCEPTED"
        const val STATUS_REJECTED = "REJECTED"
    }

    fun sendFriendRequest(senderId: Int, targetUserId: Int): SendRequestResult {
        return transaction {
            if (senderId == targetUserId) {
                return@transaction SendRequestResult.Error(ErrorType.CANNOT_FRIEND_SELF)
            }

            // Check if target exists
            val targetExists = Users.selectAll().where { Users.id eq targetUserId }.count() > 0
            if (!targetExists) {
                return@transaction SendRequestResult.Error(ErrorType.USER_NOT_FOUND)
            }

            // Check if request exists (Pending or Accepted)
            // (sender, receiver) OR (receiver, sender)
            val existing = FriendRequests.selectAll().where {
                ((FriendRequests.senderId eq senderId) and (FriendRequests.receiverId eq targetUserId)) or
                ((FriendRequests.senderId eq targetUserId) and (FriendRequests.receiverId eq senderId))
            }.singleOrNull()

            if (existing != null) {
                // If already existing, we return Error.
                return@transaction SendRequestResult.Error(ErrorType.FRIEND_REQUEST_ALREADY_SENT)
            }

            // Create
            val id = FriendRequests.insertAndGetId {
                it[this.senderId] = senderId
                it[this.receiverId] = targetUserId
                it[this.status] = STATUS_PENDING
            }.value

            // Fetch DTO
            val sender = Users.selectAll().where { Users.id eq senderId }.single()
            val receiver = Users.selectAll().where { Users.id eq targetUserId }.single()

            val senderDTO = PublicUserProfileDTO(
                id = sender[Users.id].value,
                username = sender[Users.username],
                name = sender[Users.name],
                surname = sender[Users.surname]
            )
            val receiverDTO = PublicUserProfileDTO(
                id = receiver[Users.id].value,
                username = receiver[Users.username],
                name = receiver[Users.name],
                surname = receiver[Users.surname]
            )

            val dto = FriendRequestDataDTO(
                id = id,
                senderId = senderId,
                receiverId = targetUserId,
                status = STATUS_PENDING,
                createdAt = java.time.LocalDateTime.now().toString(),
                sender = senderDTO,
                receiver = receiverDTO
            )

            SendRequestResult.Success(dto)
        }
    }

    fun respondToFriendRequest(userId: Int, requestId: Int, accept: Boolean): ErrorType? {
        return transaction {
            val request = FriendRequests.selectAll().where { FriendRequests.id eq requestId }.singleOrNull()
                ?: return@transaction ErrorType.FRIEND_REQUEST_NOT_FOUND

            val receiverId = request[FriendRequests.receiverId].value
            if (receiverId != userId) {
                // Not the receiver
                return@transaction ErrorType.FRIEND_REQUEST_NOT_FOUND
            }

            val currentStatus = request[FriendRequests.status]
            if (currentStatus != STATUS_PENDING) {
                // Already responded
                return@transaction ErrorType.BAD_REQUEST
            }

            val newStatus = if (accept) STATUS_ACCEPTED else STATUS_REJECTED

            FriendRequests.update({ FriendRequests.id eq requestId }) {
                it[status] = newStatus
            }

            null
        }
    }

    fun getFriendRequests(userId: Int): List<FriendRequestDataDTO> {
        return transaction {
            // Get requests where I am sender or receiver, and status is PENDING
            val query = FriendRequests.selectAll().where {
                ((FriendRequests.senderId eq userId) or (FriendRequests.receiverId eq userId)) and
                (FriendRequests.status eq STATUS_PENDING)
            }

            val requests = query.toList()
            if (requests.isEmpty()) return@transaction emptyList()

            // Bulk fetch users
            val userIds = requests.flatMap { listOf(it[FriendRequests.senderId].value, it[FriendRequests.receiverId].value) }.distinct()
            val usersMap = Users.selectAll().where { Users.id inList userIds }.associateBy { it[Users.id].value }

            requests.mapNotNull { row ->
                val sId = row[FriendRequests.senderId].value
                val rId = row[FriendRequests.receiverId].value
                val senderRow = usersMap[sId]
                val receiverRow = usersMap[rId]

                if (senderRow == null || receiverRow == null) return@mapNotNull null

                val senderDTO = PublicUserProfileDTO(
                    id = sId,
                    username = senderRow[Users.username],
                    name = senderRow[Users.name],
                    surname = senderRow[Users.surname]
                )
                val receiverDTO = PublicUserProfileDTO(
                    id = rId,
                    username = receiverRow[Users.username],
                    name = receiverRow[Users.name],
                    surname = receiverRow[Users.surname]
                )

                FriendRequestDataDTO(
                    id = row[FriendRequests.id].value,
                    senderId = sId,
                    receiverId = rId,
                    status = row[FriendRequests.status],
                    createdAt = row[FriendRequests.createdAt].toString(),
                    sender = senderDTO,
                    receiver = receiverDTO
                )
            }
        }
    }

    fun getUsers(limit: Int = 20): List<PublicUserProfileDTO> {
        return transaction {
            Users.selectAll().limit(limit).map { row ->
                PublicUserProfileDTO(
                    id = row[Users.id].value,
                    username = row[Users.username],
                    name = row[Users.name],
                    surname = row[Users.surname]
                )
            }
        }
    }
}
