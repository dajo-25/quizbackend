package com.quizbackend.features.communities

import com.quizbackend.contracts.generated.*

class CommunitiesMockContractImpl : CommunitiesService {

    override suspend fun PostFriendRequest(body: SendFriendRequestDTO, params: EmptyParamsDTO): DTOResponse<FriendRequestResponseDTO> {
        return DTOResponse(true, FriendRequestResponseDTO(null), null)
    }

    override suspend fun PostRespond(body: EmptyRequestDTO, params: EmptyParamsDTO): DTOResponse<GenericResponseDTO> {
        return DTOResponse(true, GenericResponseDTO(true), null)
    }

    override suspend fun GetUsers(body: EmptyRequestDTO, params: EmptyParamsDTO): DTOResponse<UserListResponseDTO> {
        return DTOResponse(true, UserListResponseDTO(emptyList()), null)
    }
}
