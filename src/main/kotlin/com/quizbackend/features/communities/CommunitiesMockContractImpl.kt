package com.quizbackend.features.communities

import com.quizbackend.contracts.common.base.*
import com.quizbackend.contracts.features.communities.*

class CommunitiesMockContractImpl : CommunitiesService {

    override suspend fun SendFriendRequest(body: SendFriendRequestDTO): DTOResponse<FriendRequestResponse> {
        return DTOResponse(true, FriendRequestResponse(null), null)
    }

    override suspend fun RespondFriendRequest(body: EmptyRequestDTO): DTOResponse<GenericResponse> {
        return DTOResponse(true, GenericResponse(true), null)
    }

    override suspend fun SearchUsers(body: EmptyRequestDTO): DTOResponse<UserListResponse> {
        return DTOResponse(true, UserListResponse(emptyList()), null)
    }
}
