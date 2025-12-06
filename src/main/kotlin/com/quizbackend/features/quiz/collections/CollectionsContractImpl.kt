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

    override suspend fun GetCollectionsList(body: EmptyRequestDTO, params: ListCollectionsParamsDTO): DTOResponse<List<CollectionDataDTO>> {
        // Only public
        return transaction {
            val query = Collections.selectAll()
            val rows = if (params.name.isNotBlank()) {
                query.where { (Collections.name like "%${params.name}%") and (Collections.isPublic eq true) }
            } else {
                query.where { Collections.isPublic eq true }
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

    override suspend fun GetCollection(id: Int, body: EmptyRequestDTO, params: GetCollectionParamsDTO): DTOResponse<CollectionDetailDataDTO> {
        // Only public
        return transaction {
            val col = Collections.selectAll().where { Collections.id eq id }.singleOrNull()
            if (col == null) return@transaction DTOResponse(false, null, ErrorDetailsDTO(ErrorType.COLLECTION_NOT_FOUND, "Not found"))

            if (!col[Collections.isPublic]) {
                return@transaction DTOResponse(false, null, ErrorDetailsDTO(ErrorType.ACCESS_DENIED_TO_COLLECTION, "Access denied (private collection)"))
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

    override suspend fun CreateCollection(body: CreateCollectionRequestDTO): DTOResponse<IdDataDTO> {
        // MOCK: Success with dummy ID
        return DTOResponse(true, IdDataDTO(999), null)
    }

    override suspend fun UpdateCollection(id: Int, body: UpdateCollectionRequestDTO, params: UpdateCollectionParamsDTO): DTOResponse<Unit> {
        // MOCK: Success
        return DTOResponse(true, null, null)
    }
}
