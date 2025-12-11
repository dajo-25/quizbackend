package com.quizbackend.features.users

import com.quizbackend.features.users.Users
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.SqlExpressionBuilder.inList
import org.jetbrains.exposed.sql.transactions.transaction

class UsersService {

    fun create(email: String, username: String, name: String, surname: String, passwordHash: String): Int? {
        return transaction {
            if (Users.selectAll().where { Users.email eq email }.count() > 0) {
                return@transaction null
            }
            Users.insertAndGetId {
                it[Users.email] = email
                it[Users.username] = username
                it[Users.name] = name
                it[Users.surname] = surname
                it[Users.passwordHash] = passwordHash
                it[Users.mustChangePassword] = false
                it[Users.isVerified] = false
            }.value
        }
    }

    fun findByEmail(email: String): ResultRow? {
        return transaction {
            Users.selectAll().where { Users.email eq email }.singleOrNull()
        }
    }

    fun findById(id: Int): ResultRow? {
        return transaction {
            Users.selectAll().where { Users.id eq id }.singleOrNull()
        }
    }

    fun updatePassword(id: Int, newHash: String, mustChange: Boolean) {
        transaction {
            Users.update({ Users.id eq id }) {
                it[passwordHash] = newHash
                it[mustChangePassword] = mustChange
            }
        }
    }

    fun markVerified(email: String) {
        transaction {
            Users.update({ Users.email eq email }) {
                it[isVerified] = true
            }
        }
    }

    fun updateProfile(id: Int, name: String, surname: String, username: String) {
        transaction {
            Users.update({ Users.id eq id }) {
                it[Users.name] = name
                it[Users.surname] = surname
                it[Users.username] = username
            }
        }
    }

    fun delete(id: Int) {
        transaction {
            // Manual cascading for production readiness
            // Delete devices
            com.quizbackend.features.devices.Devices.deleteWhere { user eq id }
            // Delete collection access
            com.quizbackend.features.quiz.collections.models.CollectionAccess.deleteWhere { userId eq id }
            // Delete collections owned by user (and their questions links)
            val userCollections = com.quizbackend.features.quiz.collections.models.Collections.selectAll().where { com.quizbackend.features.quiz.collections.models.Collections.creatorId eq id }.map { it[com.quizbackend.features.quiz.collections.models.Collections.id].value }
            if (userCollections.isNotEmpty()) {
                com.quizbackend.features.quiz.collections.models.CollectionQuestions.deleteWhere { collectionId inList userCollections }
                com.quizbackend.features.quiz.collections.models.CollectionAccess.deleteWhere { collectionId inList userCollections }
                com.quizbackend.features.quiz.collections.models.Collections.deleteWhere { creatorId eq id }
            }
            // Finally delete user
            Users.deleteWhere { Users.id eq id }
        }
    }
}
