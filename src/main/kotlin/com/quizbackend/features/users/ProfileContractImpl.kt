package com.quizbackend.features.users

import com.quizbackend.contracts.generated.*

class ProfileContractImpl : ProfileService {

    override suspend fun GetProfile(body: EmptyRequestDTO, params: EmptyParamsDTO): DTOResponse<ProfileDataResponseDTO> {
        return DTOResponse(true, ProfileDataResponseDTO(ProfileDataDTO(1, "mock@email.com", "mockUser", "Mock", "User")), null)
    }

    override suspend fun PutProfile(body: UpdateProfileRequestDTO, params: EmptyParamsDTO): DTOResponse<ProfileDataResponseDTO> {
        return DTOResponse(true, ProfileDataResponseDTO(ProfileDataDTO(1, "mock@email.com", body.username, body.name, body.surname)), null)
    }
}
