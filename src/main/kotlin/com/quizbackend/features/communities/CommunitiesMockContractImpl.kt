package com.quizbackend.features.communities

import com.quizbackend.contracts.common.base.*
import com.quizbackend.contracts.features.communities.*

class CommunitiesMockContractImpl : CommunitiesService {

    override suspend fun SendFriendRequest(body: SendFriendRequestDTO): DTOResponse<FriendRequestResponseDTO> {
        return DTOResponse(true, FriendRequestResponseDTO(null), null)
    }

    override suspend fun RespondFriendRequest(body: EmptyRequestDTO): DTOResponse<GenericResponseDTO> {
        return DTOResponse(true, GenericResponseDTO(true), null)
    }

    override suspend fun SearchUsers(body: EmptyRequestDTO): DTOResponse<UserListResponseDTO> {
        return DTOResponse(true, UserListResponseDTO(emptyList()), null)
    }
}
