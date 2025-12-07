package com.quizbackend.features.quiz.collections

import com.quizbackend.contracts.common.base.*
import com.quizbackend.contracts.features.collections.*

class CollectionsContractImpl : CollectionsService {

    override suspend fun GetCollectionsList(body: EmptyRequestDTO): DTOResponse<CollectionListResponseDTO> {
        return DTOResponse(true, CollectionListResponseDTO(emptyList()), null)
    }

    override suspend fun CreateCollection(body: CreateCollectionRequestDTO): DTOResponse<IdDataResponseDTO> {
        return DTOResponse(true, IdDataResponseDTO(1), null)
    }

    override suspend fun GetCollection(body: EmptyRequestDTO, params: UpdateCollectionParamsDTO): DTOResponse<CollectionDetailResponseDTO> {
        return DTOResponse(true, CollectionDetailResponseDTO(null), null)
    }

    override suspend fun UpdateCollection(body: UpdateCollectionRequestDTO, params: UpdateCollectionParamsDTO): DTOResponse<GenericResponseDTO> {
        return DTOResponse(true, GenericResponseDTO(true), null)
    }

    override suspend fun DeleteCollection(body: EmptyRequestDTO, params: UpdateCollectionParamsDTO): DTOResponse<GenericResponseDTO> {
        return DTOResponse(true, GenericResponseDTO(true), null)
    }
}
