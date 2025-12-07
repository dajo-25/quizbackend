package com.quizbackend.features.users

import com.quizbackend.contracts.common.base.*
import com.quizbackend.contracts.features.profile.*

class ProfileContractImpl : ProfileService {

    override suspend fun SeeProfile(body: EmptyRequestDTO): DTOResponse<ProfileDataResponse> {
        return DTOResponse(true, ProfileDataResponse(ProfileDataDTO(1, "mock@email.com", "mockUser", "Mock", "User")), null)
    }

    override suspend fun UpdateProfile(body: UpdateProfileRequestDTO): DTOResponse<ProfileDataResponse> {
        return DTOResponse(true, ProfileDataResponse(ProfileDataDTO(1, "mock@email.com", body.username, body.name, body.surname)), null)
    }
}
