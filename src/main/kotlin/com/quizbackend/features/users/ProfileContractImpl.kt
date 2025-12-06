package com.quizbackend.features.users

import com.quizbackend.contracts.common.base.DTOResponse
import com.quizbackend.contracts.common.dtos.EmptyRequestDTO
import com.quizbackend.contracts.common.errors.ErrorDetailsDTO
import com.quizbackend.contracts.common.errors.ErrorType
import com.quizbackend.contracts.features.profile.*
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction
import org.jetbrains.exposed.sql.update

class ProfileContractImpl(
    private val usersService: UsersService
) : ProfileService {

    override suspend fun SeeProfile(body: EmptyRequestDTO, userId: Int): DTOResponse<ProfileDataDTO> {
        if (userId == 0) return DTOResponse(false, null, ErrorDetailsDTO(ErrorType.UNAUTHORIZED, "Unauthorized"))

        val user = usersService.findById(userId) ?: return DTOResponse(false, null, ErrorDetailsDTO(ErrorType.USER_NOT_FOUND, "User not found"))

        return DTOResponse(true, ProfileDataDTO(
            id = user[Users.id].value,
            email = user[Users.email],
            username = user[Users.username],
            name = user[Users.name],
            surname = user[Users.surname]
        ), null)
    }

    override suspend fun UpdateProfile(body: UpdateProfileRequestDTO, userId: Int): DTOResponse<ProfileDataDTO> {
        if (userId == 0) return DTOResponse(false, null, ErrorDetailsDTO(ErrorType.UNAUTHORIZED, "Unauthorized"))

        val existing = usersService.findById(userId) ?: return DTOResponse(false, null, ErrorDetailsDTO(ErrorType.USER_NOT_FOUND, "User not found"))

        // TODO: Ideally move this update logic to UsersService, but for now implementing here or using Exposed directly via UsersService methods if available.
        // UsersService has create, findByEmail, updatePassword. Doesn't seem to have updateProfile.
        // I'll add it to UsersService or do it here inside transaction.
        // Best practice: add to UsersService.

        // Updating via UsersService (I will add this method to UsersService)
        usersService.updateProfile(userId, body.name, body.surname, body.username)

        val updated = usersService.findById(userId)!!
        return DTOResponse(true, ProfileDataDTO(
            id = updated[Users.id].value,
            email = updated[Users.email],
            username = updated[Users.username],
            name = updated[Users.name],
            surname = updated[Users.surname]
        ), null)
    }
}
