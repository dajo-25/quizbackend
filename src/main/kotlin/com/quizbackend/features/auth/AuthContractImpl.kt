package com.quizbackend.features.auth

import com.quizbackend.contracts.generated.*

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

    override suspend fun PostSignup(body: SignupRequestDTO, params: EmptyParamsDTO): DTOResponse<GenericResponseDTO> {
        val success = authDomainService.signup(
            body.email?.uppercase() ?: "",
            body.username ?: "",
            body.name ?: "",
            body.surname ?: "",
            body.passwordHash ?: ""
        )
        return if (success) {
            DTOResponse(true, GenericResponseDTO(true), null)
        } else {
            DTOResponse(false, null, null, ErrorDetailsDTO(ErrorType.ACCOUNT_ALREADY_EXISTS, "User already exists"))
        }
    }

    override suspend fun PostLogout(body: EmptyRequestDTO, params: EmptyParamsDTO): DTOResponse<GenericResponseDTO> {
        return DTOResponse(true, GenericResponseDTO(true), null)
    }

    override suspend fun DeleteAccount(body: EmptyRequestDTO, params: EmptyParamsDTO): DTOResponse<MessageResponseDTO> {
        return DTOResponse(true, MessageResponseDTO("Account deleted (simulated)"), null)
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
        return DTOResponse(true, GenericResponseDTO(true), null)
    }

    override suspend fun GetStatus(body: EmptyRequestDTO, params: EmptyParamsDTO): DTOResponse<UserStatusResponseDTO> {
        return DTOResponse(true, UserStatusResponseDTO(UserStatusDataDTO(isVerified = true, mustChangePassword = false)), null)
    }
}
