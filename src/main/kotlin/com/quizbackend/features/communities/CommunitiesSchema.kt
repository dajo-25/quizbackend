package com.quizbackend.features.communities

import com.quizbackend.features.users.Users
import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.javatime.datetime
import java.time.LocalDateTime

object FriendRequests : IntIdTable("friend_requests") {
    val senderId = reference("sender_id", Users)
    val receiverId = reference("receiver_id", Users)
    val status = varchar("status", 20) // PENDING, ACCEPTED, REJECTED
    val createdAt = datetime("created_at").clientDefault { LocalDateTime.now() }
}
