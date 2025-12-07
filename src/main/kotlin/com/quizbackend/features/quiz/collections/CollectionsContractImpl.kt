package com.quizbackend.features.quiz.collections

import com.quizbackend.contracts.generated.*

class CollectionsContractImpl : CollectionsService {

    override suspend fun GetCollections(body: EmptyRequestDTO, params: EmptyParamsDTO): DTOResponse<CollectionListResponseDTO> {
        return DTOResponse(true, CollectionListResponseDTO(emptyList()), null)
    }

    override suspend fun PostCollections(body: CreateCollectionRequestDTO, params: EmptyParamsDTO): DTOResponse<IdDataResponseDTO> {
        return DTOResponse(true, IdDataResponseDTO(1), null)
    }

    override suspend fun GetCollectionsId(body: EmptyRequestDTO, params: UpdateCollectionParamsDTO): DTOResponse<CollectionDetailResponseDTO> {
        return DTOResponse(true, CollectionDetailResponseDTO(null), null)
    }

    override suspend fun PutCollectionsId(body: UpdateCollectionRequestDTO, params: UpdateCollectionParamsDTO): DTOResponse<GenericResponseDTO> {
        return DTOResponse(true, GenericResponseDTO(true), null)
    }

    override suspend fun DeleteCollectionsId(body: EmptyRequestDTO, params: UpdateCollectionParamsDTO): DTOResponse<GenericResponseDTO> {
        return DTOResponse(true, GenericResponseDTO(true), null)
    }
}
