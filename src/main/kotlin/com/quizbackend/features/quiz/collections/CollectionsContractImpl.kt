package com.quizbackend.features.quiz.collections

import com.quizbackend.contracts.common.base.*
import com.quizbackend.contracts.features.collections.*

class CollectionsContractImpl : CollectionsService {

    override suspend fun GetCollectionsList(body: EmptyRequestDTO): DTOResponse<CollectionListResponse> {
        return DTOResponse(true, CollectionListResponse(emptyList()), null)
    }

    override suspend fun CreateCollection(body: CreateCollectionRequestDTO): DTOResponse<IdDataResponse> {
        return DTOResponse(true, IdDataResponse(1), null)
    }

    override suspend fun GetCollection(body: EmptyRequestDTO, params: UpdateCollectionParamsDTO): DTOResponse<CollectionDetailResponse> {
        return DTOResponse(true, CollectionDetailResponse(null), null)
    }

    override suspend fun UpdateCollection(body: UpdateCollectionRequestDTO, params: UpdateCollectionParamsDTO): DTOResponse<GenericResponse> {
        return DTOResponse(true, GenericResponse(true), null)
    }

    override suspend fun DeleteCollection(body: EmptyRequestDTO, params: UpdateCollectionParamsDTO): DTOResponse<GenericResponse> {
        return DTOResponse(true, GenericResponse(true), null)
    }
}
