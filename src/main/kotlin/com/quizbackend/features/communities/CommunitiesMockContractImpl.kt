package com.quizbackend.features.communities

import com.quizbackend.contracts.common.base.DTOResponse
import com.quizbackend.contracts.common.dtos.EmptyRequestDTO
import com.quizbackend.contracts.common.errors.ErrorDetailsDTO
import com.quizbackend.contracts.common.errors.ErrorType
import com.quizbackend.contracts.features.communities.*

class CommunitiesMockContractImpl : CommunitiesService {

    override suspend fun SendFriendRequest(body: SendFriendRequestDTO): DTOResponse<FriendRequestDataDTO> {
        // Missing userId due to contract limitation
        return DTOResponse(false, null, ErrorDetailsDTO(ErrorType.NOT_IMPLEMENTED, "Not implemented due to contract limitation (missing userId)"))
    }

    override suspend fun AcceptFriendRequest(id: Int, body: EmptyRequestDTO, params: AcceptFriendRequestParamsDTO): DTOResponse<FriendRequestDataDTO> {
        // Missing userId due to contract limitation
        return DTOResponse(false, null, ErrorDetailsDTO(ErrorType.NOT_IMPLEMENTED, "Not implemented due to contract limitation (missing userId)"))
    }

    override suspend fun SearchUsers(body: EmptyRequestDTO, params: SearchUsersParamsDTO): DTOResponse<List<PublicUserProfileDTO>> {
        // Missing userId due to contract limitation
        return DTOResponse(
            true,
            listOf(PublicUserProfileDTO(2, params.username, "Mock", "User")),
            null
        )
    }
}
