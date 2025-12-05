package com.quizbackend.features.quiz.collections

import com.quizbackend.contracts.common.base.DTOResponse
import com.quizbackend.contracts.common.dtos.EmptyRequestDTO
import com.quizbackend.contracts.common.errors.ErrorDetailsDTO
import com.quizbackend.contracts.common.errors.ErrorType
import com.quizbackend.contracts.features.collections.*
import com.quizbackend.features.quiz.collections.models.CollectionQuestions
import com.quizbackend.features.quiz.collections.models.Collections
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction
import java.time.LocalDateTime

class CollectionsContractImpl : CollectionsService {

    override suspend fun GetCollectionsList(body: EmptyRequestDTO, params: ListCollectionsParamsDTO, userId: Int): DTOResponse<List<CollectionDataDTO>> {
        // Return collections: Public or Private owned by user.
        return transaction {
            val query = Collections.selectAll()
            // Very simplified search logic.
            // If name provided, filter.
            // Logic: Where (isPublic OR creatorId == userId)
            val rows = if (params.name.isNotBlank()) {
                query.where { (Collections.name like "%${params.name}%") and ((Collections.isPublic eq true) or (Collections.creatorId eq userId)) }
            } else {
                query.where { (Collections.isPublic eq true) or (Collections.creatorId eq userId) }
            }

            val list = rows.map {
                CollectionDataDTO(
                    id = it[Collections.id].value,
                    name = it[Collections.name],
                    description = it[Collections.description] ?: "",
                    isPublic = it[Collections.isPublic],
                    creatorId = it[Collections.creatorId].value,
                    createdAt = it[Collections.createdAt].toString()
                )
            }
            DTOResponse(true, list, null)
        }
    }

    override suspend fun GetCollection(id: Int, body: EmptyRequestDTO, params: GetCollectionParamsDTO, userId: Int): DTOResponse<CollectionDetailDataDTO> {
        return transaction {
            val col = Collections.selectAll().where { Collections.id eq id }.singleOrNull()
            if (col == null) return@transaction DTOResponse(false, null, ErrorDetailsDTO(ErrorType.COLLECTION_NOT_FOUND, "Not found"))

            // Access check
            if (!col[Collections.isPublic] && col[Collections.creatorId].value != userId) {
                // TODO: Check CollectionAccess for shared private collections
                return@transaction DTOResponse(false, null, ErrorDetailsDTO(ErrorType.ACCESS_DENIED_TO_COLLECTION, "Access denied"))
            }

            val questionIds = CollectionQuestions.select(CollectionQuestions.questionId)
                .where { CollectionQuestions.collectionId eq id }
                .map { it[CollectionQuestions.questionId].value }

            val detail = CollectionDetailDataDTO(
                id = col[Collections.id].value,
                name = col[Collections.name],
                description = col[Collections.description] ?: "",
                isPublic = col[Collections.isPublic],
                creatorId = col[Collections.creatorId].value,
                createdAt = col[Collections.createdAt].toString(),
                questionIds = questionIds
            )
            DTOResponse(true, detail, null)
        }
    }

    override suspend fun CreateCollection(body: CreateCollectionRequestDTO, userId: Int): DTOResponse<IdDataDTO> {
        if (userId == 0) return DTOResponse(false, null, ErrorDetailsDTO(ErrorType.UNAUTHORIZED, "Unauthorized"))

        val newId = transaction {
            Collections.insertAndGetId {
                it[name] = body.name
                it[description] = body.description
                it[isPublic] = body.isPublic
                it[creatorId] = userId
                it[createdAt] = LocalDateTime.now()
            }.value
        }
        return DTOResponse(true, IdDataDTO(newId), null)
    }

    override suspend fun UpdateCollection(id: Int, body: UpdateCollectionRequestDTO, params: UpdateCollectionParamsDTO, userId: Int): DTOResponse<Void> {
        if (userId == 0) return DTOResponse(false, null, ErrorDetailsDTO(ErrorType.UNAUTHORIZED, "Unauthorized"))

        return transaction {
            val col = Collections.selectAll().where { Collections.id eq id }.singleOrNull() ?: return@transaction DTOResponse(false, null, ErrorDetailsDTO(ErrorType.COLLECTION_NOT_FOUND, "Not found"))

            if (col[Collections.creatorId].value != userId) {
                return@transaction DTOResponse(false, null, ErrorDetailsDTO(ErrorType.ACCESS_DENIED_TO_COLLECTION, "Not owner"))
            }

            if (col[Collections.isPublic] && !body.isPublic) {
                 // Requirement: Public collections make all contained questions discoverable.
                 // If changing from Public to Private, we might need to handle contained questions discoverability or "CANNOT_MODIFY_PUBLIC_COLLECTION" error?
                 // Error enum has CANNOT_MODIFY_PUBLIC_COLLECTION.
                 // Let's assume we can modify, but if it's public we might restrict making it private?
                 // Or maybe we can't edit public collections at all?
                 // I'll proceed with update.
            }

            Collections.update({ Collections.id eq id }) {
                it[name] = body.name
                it[description] = body.description
                it[isPublic] = body.isPublic
            }
            DTOResponse(true, null, null)
        }
    }
}
