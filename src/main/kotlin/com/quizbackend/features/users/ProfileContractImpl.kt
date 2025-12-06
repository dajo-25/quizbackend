package com.quizbackend.features.users

import com.quizbackend.contracts.common.base.DTOResponse
import com.quizbackend.contracts.common.dtos.EmptyRequestDTO
import com.quizbackend.contracts.common.errors.ErrorDetailsDTO
import com.quizbackend.contracts.common.errors.ErrorType
import com.quizbackend.contracts.features.profile.*

class ProfileContractImpl : ProfileService {

    override suspend fun SeeProfile(body: EmptyRequestDTO): DTOResponse<ProfileDataDTO> {
        // MOCK: Return dummy profile
        return DTOResponse(true, ProfileDataDTO(1, "mock@email.com", "mockUser", "Mock", "User"), null)
    }

    override suspend fun UpdateProfile(body: UpdateProfileRequestDTO): DTOResponse<ProfileDataDTO> {
        // MOCK: Return updated dummy profile
        return DTOResponse(true, ProfileDataDTO(1, "mock@email.com", body.username, body.name, body.surname), null)
    }
}
