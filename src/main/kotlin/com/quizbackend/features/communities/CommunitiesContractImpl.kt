package com.quizbackend.features.communities

import com.quizbackend.contracts.generated.*

class CommunitiesContractImpl(
    private val userProvider: UserProvider = DefaultUserProvider()
) : CommunitiesService {

    private val domainService = CommunitiesDomainService()

    private suspend fun getUserId(): Int {
        return userProvider.getUserId() ?: throw Exception("Unauthorized")
    }

    override suspend fun PostFriendRequest(body: SendFriendRequestDTO, params: EmptyParamsDTO): DTOResponse<FriendRequestResponseDTO> {
        return try {
            val userId = getUserId()
            when (val result = domainService.sendFriendRequest(userId, body.targetUserId)) {
                is SendRequestResult.Success -> {
                    DTOResponse(true, FriendRequestResponseDTO(result.data))
                }
                is SendRequestResult.Error -> {
                    DTOResponse(false, null, result.error.name, ErrorDetailsDTO(result.error))
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
            DTOResponse(false, null, "Internal Server Error", ErrorDetailsDTO(ErrorType.INTERNAL_SERVER_ERROR, e.message))
        }
    }

    override suspend fun PostRespond(body: RespondFriendRequestRequestDTO, params: EmptyParamsDTO): DTOResponse<GenericResponseDTO> {
        return try {
            val userId = getUserId()
            val error = domainService.respondToFriendRequest(userId, body.requestId, body.accept)
            if (error != null) {
                DTOResponse(false, null, error.name, ErrorDetailsDTO(error))
            } else {
                DTOResponse(true, GenericResponseDTO(true))
            }
        } catch (e: Exception) {
            e.printStackTrace()
            DTOResponse(false, null, "Internal Server Error", ErrorDetailsDTO(ErrorType.INTERNAL_SERVER_ERROR, e.message))
        }
    }

    override suspend fun GetFriendRequests(body: EmptyRequestDTO, params: EmptyParamsDTO): DTOResponse<FriendRequestListResponseDTO> {
        return try {
            val userId = getUserId()
            val requests = domainService.getFriendRequests(userId)
            DTOResponse(true, FriendRequestListResponseDTO(requests))
        } catch (e: Exception) {
            e.printStackTrace()
            DTOResponse(false, null, "Internal Server Error", ErrorDetailsDTO(ErrorType.INTERNAL_SERVER_ERROR, e.message))
        }
    }

    override suspend fun GetUsers(body: EmptyRequestDTO, params: EmptyParamsDTO): DTOResponse<UserListResponseDTO> {
        return try {
            val userId = getUserId()
            val users = domainService.getUsers()
            DTOResponse(true, UserListResponseDTO(users))
        } catch (e: Exception) {
            e.printStackTrace()
            DTOResponse(false, null, "Internal Server Error", ErrorDetailsDTO(ErrorType.INTERNAL_SERVER_ERROR, e.message))
        }
    }
}
