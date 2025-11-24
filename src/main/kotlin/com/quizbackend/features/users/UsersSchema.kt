package com.quizbackend.features.users

import org.jetbrains.exposed.dao.id.IntIdTable

object Users : IntIdTable("users") {
    val email = varchar("email", 255).uniqueIndex()
    val username = varchar("username", 255)
    val name = varchar("name", 255)
    val surname = varchar("surname", 255)
    val passwordHash = varchar("password_hash", 512)
    val mustChangePassword = bool("must_change_password").default(false)
    val isVerified = bool("is_verified").default(false)
}
