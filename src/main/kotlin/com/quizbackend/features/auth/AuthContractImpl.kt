package com.quizbackend.features.auth

import com.quizbackend.contracts.generated.*
import com.quizbackend.utils.UserContext
import com.quizbackend.features.users.Users

class AuthContractImpl(
    private val authDomainService: AuthDomainService
) : AuthService {

    override suspend fun PostLogin(body: LoginRequestDTO, params: EmptyParamsDTO): DTOResponse<LoginResponseDTO> {
        val token = authDomainService.login(body.email?.uppercase() ?: "", body.passwordHash ?: "", body.uniqueId ?: "")
        return if (token != null) {
            DTOResponse(true, LoginResponseDTO(token = token), null)
        } else {
            DTOResponse(false, null, null, ErrorDetailsDTO(ErrorType.INVALID_CREDENTIALS, "Invalid credentials"))
        }
    }

    override suspend fun PostSignup(body: SignupRequestDTO, params: EmptyParamsDTO): DTOResponse<LoginResponseDTO> {
        val token = authDomainService.signup(
            body.email?.uppercase() ?: "",
            body.username ?: "",
            body.name ?: "",
            body.surname ?: "",
            body.passwordHash ?: "",
            body.uniqueId ?: ""
        )
        return if (token != null) {
            DTOResponse(true, LoginResponseDTO(token), null)
        } else {
            // It could be invalid email or already exists
            // For simplicity, we return account already exists or bad request if validation failed.
            // Since we added validation inside signup, we might want to return different errors, but the current contract implies "Generic" error or specific ones.
            // I'll stick to a generic error or "ACCOUNT_ALREADY_EXISTS" which is the most common reason for failure here if validation passes on client side.
            // But if validation failed, it returns null too.
            DTOResponse(false, null, null, ErrorDetailsDTO(ErrorType.ACCOUNT_ALREADY_EXISTS, "User already exists or invalid data"))
        }
    }

    override suspend fun PostLogout(body: EmptyRequestDTO, params: EmptyParamsDTO): DTOResponse<GenericResponseDTO> {
        val token = UserContext.getToken()
        if (token != null) {
            authDomainService.logout(token)
            return DTOResponse(true, GenericResponseDTO(true), null)
        }
        return DTOResponse(false, null, null, ErrorDetailsDTO(ErrorType.UNAUTHORIZED, "Missing token"))
    }

    override suspend fun DeleteAccount(body: EmptyRequestDTO, params: EmptyParamsDTO): DTOResponse<MessageResponseDTO> {
        val userId = UserContext.getUserId()
        if (userId != null) {
            authDomainService.deleteAccount(userId)
            return DTOResponse(true, MessageResponseDTO("Account deleted"), null)
        }
        return DTOResponse(false, null, null, ErrorDetailsDTO(ErrorType.UNAUTHORIZED, "User not found"))
    }

    override suspend fun PostRecover(body: RecoverPasswordRequestDTO, params: EmptyParamsDTO): DTOResponse<MessageResponseDTO> {
        authDomainService.recoverPassword(body.email?.uppercase() ?: "")
        return DTOResponse(true, MessageResponseDTO("Recovery email sent"), null)
    }

    override suspend fun PostVerify(body: VerifyEmailRequestDTO, params: EmptyParamsDTO): DTOResponse<GenericResponseDTO> {
        val success = authDomainService.verifyEmail(body.code ?: "")
        return if (success) {
            DTOResponse(true, GenericResponseDTO(true), null)
        } else {
            DTOResponse(false, null, null, ErrorDetailsDTO(ErrorType.INVALID_VERIFICATION_CODE, "Invalid code"))
        }
    }

    override suspend fun GetMustChangePassword(body: EmptyRequestDTO, params: EmptyParamsDTO): DTOResponse<MustChangePasswordResponseDTO> {
        return DTOResponse(true, MustChangePasswordResponseDTO(false), null)
    }

    override suspend fun PostChangePassword(body: ChangePasswordRequestDTO, params: EmptyParamsDTO): DTOResponse<GenericResponseDTO> {
        val userId = UserContext.getUserId()
        if (userId != null) {
            val success = authDomainService.changePassword(userId, body.oldHash ?: "", body.newHash ?: "")
            if (success) {
                return DTOResponse(true, GenericResponseDTO(true), null)
            } else {
                return DTOResponse(false, null, null, ErrorDetailsDTO(ErrorType.INVALID_CREDENTIALS, "Invalid old password"))
            }
        }
        return DTOResponse(false, null, null, ErrorDetailsDTO(ErrorType.UNAUTHORIZED, "User not found"))
    }

    override suspend fun GetStatus(body: EmptyRequestDTO, params: EmptyParamsDTO): DTOResponse<UserStatusResponseDTO> {
        val userId = UserContext.getUserId()
        if (userId != null) {
            val user = authDomainService.getUser(userId)
            if (user != null) {
                return DTOResponse(true, UserStatusResponseDTO(UserStatusDataDTO(
                    id = user[Users.id].value,
                    email = user[Users.email],
                    username = user[Users.username],
                    isVerified = user[Users.isVerified],
                    mustChangePassword = user[Users.mustChangePassword]
                )), null)
            }
        }
        return DTOResponse(false, null, null, ErrorDetailsDTO(ErrorType.UNAUTHORIZED, "User not found"))
    }
}
