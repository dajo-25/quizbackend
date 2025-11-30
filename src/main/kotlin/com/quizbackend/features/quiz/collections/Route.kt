package com.quizbackend.features.quiz.collections

import com.quizbackend.features.quiz.collections.models.CollectionAccess
import com.quizbackend.features.quiz.collections.models.CollectionQuestions
import com.quizbackend.features.quiz.collections.models.Collections
import com.quizbackend.features.quiz.questions.models.Questions
import com.quizbackend.features.users.Users
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.transactions.transaction
import java.time.LocalDateTime

fun Route.collectionsRoutes() {
    authenticate("auth-bearer") {
        route("/collections") {
            // Create Collection
            post {
                val userId = call.principal<UserIdPrincipal>()?.name?.toIntOrNull()
                if (userId == null) {
                    call.respond(HttpStatusCode.Unauthorized)
                    return@post
                }
                val request = call.receive<CreateCollectionRequest>()

                val newCollectionId = transaction {
                    Collections.insertAndGetId {
                        it[name] = request.name
                        it[description] = request.description
                        it[isPublic] = request.isPublic
                        it[creatorId] = userId
                        it[createdAt] = LocalDateTime.now()
                    }.value
                }
                call.respond(HttpStatusCode.Created, mapOf("id" to newCollectionId))
            }

            // List Collections
            get {
                val userId = call.principal<UserIdPrincipal>()?.name?.toIntOrNull()
                if (userId == null) {
                    call.respond(HttpStatusCode.Unauthorized)
                    return@get
                }
                val nameFilter = call.request.queryParameters["name"]

                val collections = transaction {
                    val query = Collections.selectAll()

                    val sharedCollectionIds = CollectionAccess
                        .selectAll().where { CollectionAccess.userId eq userId }
                        .map { it[CollectionAccess.collectionId].value }

                    query.andWhere {
                        (Collections.isPublic eq true) or
                        (Collections.creatorId eq userId) or
                        (Collections.id inList sharedCollectionIds)
                    }

                    if (!nameFilter.isNullOrBlank()) {
                        query.andWhere { Collections.name like "%$nameFilter%" }
                    }

                    query.map {
                        CollectionResponse(
                            id = it[Collections.id].value,
                            name = it[Collections.name],
                            description = it[Collections.description],
                            isPublic = it[Collections.isPublic],
                            creatorId = it[Collections.creatorId].value,
                            createdAt = it[Collections.createdAt].toString()
                        )
                    }
                }
                call.respond(collections)
            }

            // Get Collection Details
            get("/{id}") {
                val userId = call.principal<UserIdPrincipal>()?.name?.toIntOrNull()
                val collectionId = call.parameters["id"]?.toIntOrNull()

                if (userId == null || collectionId == null) {
                    call.respond(HttpStatusCode.BadRequest)
                    return@get
                }

                // Use a sealed class or simple object to return result from transaction
                val result = transaction {
                    val collection = Collections.selectAll().where { Collections.id eq collectionId }.singleOrNull()

                    if (collection == null) {
                        return@transaction HttpStatusCode.NotFound to null
                    }

                    val isCreator = collection[Collections.creatorId].value == userId
                    val isPublic = collection[Collections.isPublic]
                    val isShared = CollectionAccess.selectAll().where {
                        (CollectionAccess.collectionId eq collectionId) and (CollectionAccess.userId eq userId)
                    }.count() > 0

                    if (!isPublic && !isCreator && !isShared) {
                        return@transaction HttpStatusCode.Forbidden to null
                    }

                    val questionIds = CollectionQuestions
                        .selectAll().where { CollectionQuestions.collectionId eq collectionId }
                        .map { it[CollectionQuestions.questionId].value }

                    HttpStatusCode.OK to CollectionDetailResponse(
                        id = collection[Collections.id].value,
                        name = collection[Collections.name],
                        description = collection[Collections.description],
                        isPublic = isPublic,
                        creatorId = collection[Collections.creatorId].value,
                        createdAt = collection[Collections.createdAt].toString(),
                        questionIds = questionIds
                    )
                }

                if (result.second != null) {
                    call.respond(result.first, result.second!!)
                } else {
                    call.respond(result.first)
                }
            }

            // Update Collection
            put("/{id}") {
                val userId = call.principal<UserIdPrincipal>()?.name?.toIntOrNull()
                val collectionId = call.parameters["id"]?.toIntOrNull()

                if (userId == null || collectionId == null) {
                    call.respond(HttpStatusCode.BadRequest)
                    return@put
                }
                val request = call.receive<UpdateCollectionRequest>()

                val status = transaction {
                    val collection = Collections.selectAll().where { Collections.id eq collectionId }.singleOrNull()
                    if (collection == null) {
                        return@transaction HttpStatusCode.NotFound
                    }

                    if (collection[Collections.creatorId].value != userId) {
                        return@transaction HttpStatusCode.Forbidden
                    }

                    // Update fields
                    Collections.update({ Collections.id eq collectionId }) {
                        if (request.name != null) it[name] = request.name
                        if (request.description != null) it[description] = request.description
                        if (request.isPublic != null) it[isPublic] = request.isPublic
                    }

                    // If becoming public, update all questions to be discoverable
                    if (request.isPublic == true && collection[Collections.isPublic] == false) {
                        val questionIds = CollectionQuestions
                            .selectAll().where { CollectionQuestions.collectionId eq collectionId }
                            .map { it[CollectionQuestions.questionId].value }

                        if (questionIds.isNotEmpty()) {
                            Questions.update({ Questions.id inList questionIds }) {
                                it[isDiscoverable] = true
                            }
                        }
                    }
                    HttpStatusCode.OK
                }

                if (status == HttpStatusCode.Forbidden) {
                     call.respond(status, "Only creator can edit collection")
                } else {
                     call.respond(status)
                }
            }

            // Share Collection
            post("/{id}/share") {
                val userId = call.principal<UserIdPrincipal>()?.name?.toIntOrNull()
                val collectionId = call.parameters["id"]?.toIntOrNull()

                if (userId == null || collectionId == null) {
                    call.respond(HttpStatusCode.BadRequest)
                    return@post
                }
                val request = call.receive<ShareCollectionRequest>()

                val result = transaction {
                    val collection = Collections.selectAll().where { Collections.id eq collectionId }.singleOrNull()
                    if (collection == null) {
                        return@transaction HttpStatusCode.NotFound to "Collection not found"
                    }

                    if (collection[Collections.creatorId].value != userId) {
                        return@transaction HttpStatusCode.Forbidden to "Only creator can share collection"
                    }

                    if (collection[Collections.isPublic]) {
                        return@transaction HttpStatusCode.BadRequest to "Cannot share a public collection"
                    }

                    val targetUser = Users.selectAll().where { Users.email eq request.userEmail.uppercase() }.singleOrNull()
                    if (targetUser == null) {
                        return@transaction HttpStatusCode.NotFound to "User with email ${request.userEmail} not found"
                    }
                    val targetUserId = targetUser[Users.id].value

                    val existingShare = CollectionAccess.selectAll().where {
                        (CollectionAccess.collectionId eq collectionId) and (CollectionAccess.userId eq targetUserId)
                    }.count()

                    if (existingShare == 0L) {
                        CollectionAccess.insert {
                            it[this.collectionId] = collectionId
                            it[this.userId] = targetUserId
                        }
                    }

                    HttpStatusCode.OK to null
                }

                if (result.second != null) {
                    call.respond(result.first, result.second!!)
                } else {
                    call.respond(result.first)
                }
            }
        }
    }
}
