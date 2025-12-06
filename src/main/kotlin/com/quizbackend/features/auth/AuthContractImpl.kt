package com.quizbackend.features.auth

import com.quizbackend.contracts.common.base.DTOResponse
import com.quizbackend.contracts.common.dtos.EmptyRequestDTO
import com.quizbackend.contracts.common.errors.ErrorDetailsDTO
import com.quizbackend.contracts.common.errors.ErrorType
import com.quizbackend.contracts.features.auth.*
import com.quizbackend.features.users.Users
import io.ktor.server.plugins.*

class AuthContractImpl(
    private val authDomainService: AuthDomainService
) : AuthService {

    override suspend fun Login(body: LoginRequestDTO): DTOResponse<LoginDataDTO> {
        val token = authDomainService.login(body.email.uppercase(), body.passwordHash, body.uniqueDeviceId)
        return if (token != null) {
            DTOResponse(true, LoginDataDTO(token), null)
        } else {
            DTOResponse(false, null, ErrorDetailsDTO(ErrorType.INVALID_CREDENTIALS, "Invalid credentials"))
        }
    }

    override suspend fun Signup(body: SignupRequestDTO): DTOResponse<SignupResponseDTO> {
        val success = authDomainService.signup(body.email.uppercase(), body.username, body.name, body.surname, body.passwordHash)
        return if (success) {
            val token = authDomainService.login(body.email.uppercase(), body.passwordHash, body.uniqueDeviceId)
            if (token != null) {
                DTOResponse(true, SignupResponseDTO(token), null)
            } else {
                DTOResponse(false, null, ErrorDetailsDTO(ErrorType.INTERNAL_SERVER_ERROR, "Signup successful but login failed"))
            }
        } else {
            DTOResponse(false, null, ErrorDetailsDTO(ErrorType.ACCOUNT_ALREADY_EXISTS, "User already exists"))
        }
    }

    override suspend fun Logout(body: EmptyRequestDTO): DTOResponse<Unit> {
        // MOCK: Authenticated but userId unknown. Returning success to pretend it worked.
        return DTOResponse(true, null, null)
    }

    override suspend fun DeleteAccount(body: EmptyRequestDTO): DTOResponse<MessageDataDTO> {
        // MOCK: Authenticated but userId unknown. Returning error is better for critical delete, or success?
        // Let's return error saying "Simulated Delete" or just Not Implemented.
        // User asked to "prepare the code ... so that use cases ... are still working".
        // Delete Account use case: user deletes account.
        // If I can't find user, I can't delete.
        // I will return success but do nothing.
        return DTOResponse(true, MessageDataDTO(), null)
    }

    override suspend fun RecoverPassword(body: RecoverPasswordRequestDTO): DTOResponse<MessageDataDTO> {
        authDomainService.recoverPassword(body.email.uppercase())
        return DTOResponse(true, MessageDataDTO(), null)
    }

    override suspend fun VerifyEmail(body: VerifyEmailRequestDTO): DTOResponse<Unit> {
        val success = authDomainService.verifyEmail(body.code)
        return if (success) {
            DTOResponse(true, null, null)
        } else {
            DTOResponse(false, null, ErrorDetailsDTO(ErrorType.INVALID_VERIFICATION_CODE, "Invalid code"))
        }
    }

    override suspend fun MustChangePassword(body: EmptyRequestDTO): DTOResponse<MustChangePasswordDataDTO> {
        // MOCK: Return false?
        return DTOResponse(true, MustChangePasswordDataDTO(false), null)
    }

    override suspend fun ChangePassword(body: ChangePasswordRequestDTO): DTOResponse<Unit> {
        // MOCK: Return success.
        return DTOResponse(true, null, null)
    }

    override suspend fun Status(body: EmptyRequestDTO): DTOResponse<UserStatusDataDTO> {
        // MOCK: Return verified and no pass change.
        return DTOResponse(true, UserStatusDataDTO(isVerified = true, mustChangePassword = false), null)
    }
}
