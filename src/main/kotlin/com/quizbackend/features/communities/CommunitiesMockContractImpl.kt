package com.quizbackend.features.communities

import com.quizbackend.contracts.common.base.DTOResponse
import com.quizbackend.contracts.common.dtos.EmptyRequestDTO
import com.quizbackend.contracts.features.communities.*

class CommunitiesMockContractImpl : CommunitiesService {

    override suspend fun SendFriendRequest(body: SendFriendRequestDTO, userId: Int): DTOResponse<FriendRequestDataDTO> {
        return DTOResponse(
            true,
            FriendRequestDataDTO(1, userId, body.targetUserId, "PENDING", "2023-10-27T10:00:00Z"),
            null
        )
    }

    override suspend fun AcceptFriendRequest(id: Int, body: EmptyRequestDTO, params: AcceptFriendRequestParamsDTO, userId: Int): DTOResponse<FriendRequestDataDTO> {
        return DTOResponse(
            true,
            FriendRequestDataDTO(id, 2, userId, "ACCEPTED", "2023-10-27T10:00:00Z"),
            null
        )
    }

    override suspend fun SearchUsers(body: EmptyRequestDTO, params: SearchUsersParamsDTO, userId: Int): DTOResponse<List<PublicUserProfileDTO>> {
        return DTOResponse(
            true,
            listOf(PublicUserProfileDTO(2, params.username, "Mock", "User")),
            null
        )
    }
}
