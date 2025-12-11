package com.quizbackend.features.quiz.collections

import com.quizbackend.contracts.generated.*
import io.ktor.server.application.*

class CollectionsContractImpl(
    private val userProvider: UserProvider = DefaultUserProvider()
) : CollectionsService {

    private val domainService = CollectionsDomainService()

    private suspend fun getUserId(): Int {
        return userProvider.getUserId() ?: throw Exception("Unauthorized")
    }

    private suspend fun getUserIdOrGuest(): Int {
        return userProvider.getUserId() ?: -1
    }

    override suspend fun GetCollections(body: EmptyRequestDTO, params: EmptyParamsDTO): DTOResponse<CollectionListResponseDTO> {
        return try {
            val userId = getUserIdOrGuest()
            val collections = domainService.getCollections(userId)
            DTOResponse(true, CollectionListResponseDTO(collections))
        } catch (e: Exception) {
            e.printStackTrace()
            DTOResponse(false, null, "Internal Server Error", ErrorDetailsDTO(ErrorType.INTERNAL_SERVER_ERROR, "An internal error occurred"))
        }
    }

    override suspend fun PostCollections(body: CreateCollectionRequestDTO, params: EmptyParamsDTO): DTOResponse<IdDataResponseDTO> {
        return try {
            val userId = getUserId()
            val id = domainService.createCollection(userId, body)
            DTOResponse(true, IdDataResponseDTO(id))
        } catch (e: Exception) {
            e.printStackTrace()
             DTOResponse(false, null, "Internal Server Error", ErrorDetailsDTO(ErrorType.INTERNAL_SERVER_ERROR, "An internal error occurred"))
        }
    }

    override suspend fun GetCollectionsId(body: EmptyRequestDTO, params: UpdateCollectionParamsDTO): DTOResponse<CollectionDetailResponseDTO> {
        return try {
            val userId = getUserIdOrGuest()

            val collection = domainService.getCollectionById(userId, params.id)

            if (collection == null) {
                DTOResponse(false, null, "Collection not found or access denied", ErrorDetailsDTO(ErrorType.COLLECTION_NOT_FOUND))
            } else {
                DTOResponse(true, CollectionDetailResponseDTO(collection))
            }
        } catch (e: Exception) {
            e.printStackTrace()
            DTOResponse(false, null, "Internal Server Error", ErrorDetailsDTO(ErrorType.INTERNAL_SERVER_ERROR, "An internal error occurred"))
        }
    }

    override suspend fun PutCollectionsId(body: UpdateCollectionRequestDTO, params: UpdateCollectionParamsDTO): DTOResponse<GenericResponseDTO> {
        return try {
            val userId = getUserId()
            val error = domainService.updateCollection(userId, params.id, body)

            if (error != null) {
                DTOResponse(false, null, error.name, ErrorDetailsDTO(error))
            } else {
                DTOResponse(true, GenericResponseDTO(true))
            }
        } catch (e: Exception) {
             e.printStackTrace()
             DTOResponse(false, null, "Internal Server Error", ErrorDetailsDTO(ErrorType.INTERNAL_SERVER_ERROR, "An internal error occurred"))
        }
    }

    override suspend fun DeleteCollectionsId(body: EmptyRequestDTO, params: UpdateCollectionParamsDTO): DTOResponse<GenericResponseDTO> {
        return try {
            val userId = getUserId()
            val error = domainService.deleteCollection(userId, params.id)

            if (error != null) {
                DTOResponse(false, null, error.name, ErrorDetailsDTO(error))
            } else {
                DTOResponse(true, GenericResponseDTO(true))
            }
        } catch (e: Exception) {
             e.printStackTrace()
             DTOResponse(false, null, "Internal Server Error", ErrorDetailsDTO(ErrorType.INTERNAL_SERVER_ERROR, "An internal error occurred"))
        }
    }
}
