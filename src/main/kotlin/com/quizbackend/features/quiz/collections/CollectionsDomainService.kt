package com.quizbackend.features.quiz.collections

import com.quizbackend.contracts.generated.*
import com.quizbackend.features.quiz.collections.models.CollectionAccess
import com.quizbackend.features.quiz.collections.models.CollectionQuestions
import com.quizbackend.features.quiz.collections.models.Collections
import com.quizbackend.features.quiz.questions.models.Questions
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.transactions.transaction
import java.time.LocalDateTime

class CollectionsDomainService {

    fun getCollections(userId: Int): List<CollectionDataDTO> {
        return transaction {
            // Logic: Visible if (isPublic == true) OR (creatorId == userId) OR (userId in CollectionAccess)
            // Using Left Join on CollectionAccess to check access

            // Query collections where...
            val query = Collections
                .leftJoin(CollectionAccess, { Collections.id }, { CollectionAccess.collectionId })
                .selectAll()
                .where {
                    (Collections.isPublic eq true) or
                    (Collections.creatorId eq userId) or
                    (CollectionAccess.userId eq userId)
                }
                .withDistinct() // In case multiple access entries (though unique index exists)

            query.map { row ->
                CollectionDataDTO(
                    id = row[Collections.id].value,
                    name = row[Collections.name],
                    description = row[Collections.description] ?: "",
                    isPublic = row[Collections.isPublic],
                    creatorId = row[Collections.creatorId].value,
                    createdAt = row[Collections.createdAt].toString()
                )
            }
        }
    }

    fun createCollection(userId: Int, dto: CreateCollectionRequestDTO): Int {
        return transaction {
            // 1. Create Collection
            val collectionId = Collections.insertAndGetId {
                it[name] = dto.name
                it[description] = dto.description
                it[creatorId] = userId
                it[isPublic] = dto.isPublic
                it[createdAt] = LocalDateTime.now()
            }.value

            // 2. Add Questions (if any)
            if (dto.questionIds.isNotEmpty()) {
                // Verify questions exist?
                // For now, assuming provided IDs are valid. If we want strict check:
                val existingQuestions = Questions.selectAll().where { Questions.id inList dto.questionIds }
                    .map { it[Questions.id].value }

                // Only insert valid questions
                val validQuestionIds = dto.questionIds.filter { it in existingQuestions }

                // Bulk insert
                // CollectionQuestions.batchInsert(validQuestionIds) { qId -> ... }
                // Exposed batchInsert syntax:
                CollectionQuestions.batchInsert(validQuestionIds) { qId ->
                    this[CollectionQuestions.collectionId] = collectionId
                    this[CollectionQuestions.questionId] = qId
                }
            }

            collectionId
        }
    }

    fun getCollectionById(userId: Int, collectionId: Int): CollectionDetailDataDTO? {
        return transaction {
            // Fetch collection
            val collectionRow = Collections.selectAll().where { Collections.id eq collectionId }.singleOrNull()
                ?: return@transaction null

            val creatorId = collectionRow[Collections.creatorId].value
            val isPublic = collectionRow[Collections.isPublic]

            // Check Access
            var hasAccess = false
            if (isPublic || creatorId == userId) {
                hasAccess = true
            } else {
                // Check CollectionAccess
                val accessCount = CollectionAccess.selectAll().where {
                    (CollectionAccess.collectionId eq collectionId) and (CollectionAccess.userId eq userId)
                }.count()
                if (accessCount > 0L) hasAccess = true
            }

            if (!hasAccess) return@transaction null // Or throw AccessDenied? Returning null lets caller handle 404/403

            // Fetch Question IDs
            val questionIds = CollectionQuestions.selectAll().where { CollectionQuestions.collectionId eq collectionId }
                .map { it[CollectionQuestions.questionId].value }

            CollectionDetailDataDTO(
                id = collectionRow[Collections.id].value,
                name = collectionRow[Collections.name],
                description = collectionRow[Collections.description] ?: "",
                isPublic = isPublic,
                creatorId = creatorId,
                createdAt = collectionRow[Collections.createdAt].toString(),
                questionIds = questionIds
            )
        }
    }

    fun updateCollection(userId: Int, collectionId: Int, dto: UpdateCollectionRequestDTO): ErrorType? {
        return transaction {
            val collectionRow = Collections.selectAll().where { Collections.id eq collectionId }.singleOrNull()

            if (collectionRow == null) return@transaction ErrorType.COLLECTION_NOT_FOUND

            val creatorId = collectionRow[Collections.creatorId].value

            // Only creator can update?
            // "A user shouldn't be able to edit a collection they don't own"
            if (creatorId != userId) return@transaction ErrorType.ACCESS_DENIED_TO_COLLECTION

            // Update Metadata
            Collections.update({ Collections.id eq collectionId }) {
                it[name] = dto.name
                it[description] = dto.description
                it[isPublic] = dto.isPublic
            }

            // Update Questions
            // Delete existing links
            CollectionQuestions.deleteWhere { CollectionQuestions.collectionId eq collectionId }

            // Insert new links
            if (dto.questionIds.isNotEmpty()) {
                val existingQuestions = Questions.selectAll().where { Questions.id inList dto.questionIds }
                    .map { it[Questions.id].value }

                val validQuestionIds = dto.questionIds.filter { it in existingQuestions }

                CollectionQuestions.batchInsert(validQuestionIds) { qId ->
                    this[CollectionQuestions.collectionId] = collectionId
                    this[CollectionQuestions.questionId] = qId
                }
            }

            null // Success
        }
    }

    fun deleteCollection(userId: Int, collectionId: Int): ErrorType? {
        return transaction {
            val collectionRow = Collections.selectAll().where { Collections.id eq collectionId }.singleOrNull()

            if (collectionRow == null) return@transaction ErrorType.COLLECTION_NOT_FOUND

            val creatorId = collectionRow[Collections.creatorId].value
            if (creatorId != userId) return@transaction ErrorType.ACCESS_DENIED_TO_COLLECTION

            // Delete Access Links
            CollectionAccess.deleteWhere { CollectionAccess.collectionId eq collectionId }

            // Delete Question Links
            CollectionQuestions.deleteWhere { CollectionQuestions.collectionId eq collectionId }

            // Delete Collection
            Collections.deleteWhere { Collections.id eq collectionId }

            null
        }
    }
}
