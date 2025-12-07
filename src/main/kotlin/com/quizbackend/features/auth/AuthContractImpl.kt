package com.quizbackend.features.auth

import com.quizbackend.contracts.common.base.*
import com.quizbackend.contracts.features.auth.*

class AuthContractImpl(
    private val authDomainService: AuthDomainService
) : AuthService {

    override suspend fun Login(body: LoginRequestDTO): DTOResponse<LoginResponseDTO> {
        val token = authDomainService.login(body.email?.uppercase() ?: "", body.passwordHash ?: "", body.uniqueId ?: "")
        return if (token != null) {
            DTOResponse(true, LoginResponseDTO(token = token), null)
        } else {
            DTOResponse(false, null, null, ErrorDetailsDTO(ErrorType.INVALID_CREDENTIALS, "Invalid credentials"))
        }
    }

    override suspend fun Signup(body: SignupRequestDTO): DTOResponse<GenericResponseDTO> {
        val success = authDomainService.signup(
            body.email?.uppercase() ?: "",
            body.username ?: "",
            body.name ?: "",
            body.surname ?: "",
            body.passwordHash ?: ""
        )
        // Signup implementation in DomainService returns boolean.
        // It doesn't auto-login in this mock, but the original code tried to.
        // I'll stick to basic signup for now or follow the logic.
        // Original logic: Signup -> Login -> Return Token.
        // But the Interface now says return GenericResponseDTO (success boolean).
        // If I want to return Token, I need to change Interface return type.
        // But let's stick to what I defined in Contracts (GenericResponseDTO).
        // If the user wants auto-login, the frontend usually handles it or I should change the contract.
        // The original contract returned GenericResponseDTO in the file I read!
        // Wait, let me check `AuthContracts.autogen.kt` original content.
        // It had `DTOResponse<GenericResponseDTO>` for Signup.
        // But `AuthContractImpl` was returning `SignupResponseDTO`?
        // Ah, `AuthContractImpl` previously returned `DTOResponse<SignupResponseDTO>`.
        // But the Interface said `GenericResponseDTO`.
        // This confirms `AuthContractImpl` was diverging.
        // I will return `GenericResponseDTO` as per current Interface.

        return if (success) {
            DTOResponse(true, GenericResponseDTO(true), null)
        } else {
            DTOResponse(false, null, null, ErrorDetailsDTO(ErrorType.ACCOUNT_ALREADY_EXISTS, "User already exists"))
        }
    }

    override suspend fun Logout(body: EmptyRequestDTO): DTOResponse<GenericResponseDTO> {
        return DTOResponse(true, GenericResponseDTO(true), null)
    }

    override suspend fun DeleteAccount(body: EmptyRequestDTO): DTOResponse<MessageResponseDTO> {
        return DTOResponse(true, MessageResponseDTO("Account deleted (simulated)"), null)
    }

    override suspend fun RecoverPassword(body: RecoverPasswordRequestDTO): DTOResponse<MessageResponseDTO> {
        authDomainService.recoverPassword(body.email?.uppercase() ?: "")
        return DTOResponse(true, MessageResponseDTO("Recovery email sent"), null)
    }

    override suspend fun VerifyEmail(body: VerifyEmailRequestDTO): DTOResponse<GenericResponseDTO> {
        val success = authDomainService.verifyEmail(body.code ?: "")
        return if (success) {
            DTOResponse(true, GenericResponseDTO(true), null)
        } else {
            DTOResponse(false, null, null, ErrorDetailsDTO(ErrorType.INVALID_VERIFICATION_CODE, "Invalid code"))
        }
    }

    override suspend fun MustChangePassword(body: EmptyRequestDTO): DTOResponse<MustChangePasswordResponseDTO> {
        return DTOResponse(true, MustChangePasswordResponseDTO(false), null)
    }

    override suspend fun ChangePassword(body: ChangePasswordRequestDTO): DTOResponse<GenericResponseDTO> {
        return DTOResponse(true, GenericResponseDTO(true), null)
    }

    override suspend fun Status(body: EmptyRequestDTO): DTOResponse<UserStatusResponseDTO> {
        return DTOResponse(true, UserStatusResponseDTO(UserStatusDataDTO(isVerified = true, mustChangePassword = false)), null)
    }
}
