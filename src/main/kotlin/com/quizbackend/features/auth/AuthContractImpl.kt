package com.quizbackend.features.auth

import com.quizbackend.contracts.common.base.*
import com.quizbackend.contracts.features.auth.*

class AuthContractImpl(
    private val authDomainService: AuthDomainService
) : AuthService {

    override suspend fun Login(body: LoginRequestDTO): DTOResponse<LoginResponse> {
        val token = authDomainService.login(body.email?.uppercase() ?: "", body.passwordHash ?: "", body.uniqueId ?: "")
        return if (token != null) {
            DTOResponse(true, LoginResponse(token = token), null)
        } else {
            DTOResponse(false, null, null, ErrorDetailsDTO(ErrorType.INVALID_CREDENTIALS, "Invalid credentials"))
        }
    }

    override suspend fun Signup(body: SignupRequestDTO): DTOResponse<GenericResponse> {
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
        // But the Interface now says return GenericResponse (success boolean).
        // If I want to return Token, I need to change Interface return type.
        // But let's stick to what I defined in Contracts (GenericResponse).
        // If the user wants auto-login, the frontend usually handles it or I should change the contract.
        // The original contract returned GenericResponse in the file I read!
        // Wait, let me check `AuthContracts.autogen.kt` original content.
        // It had `DTOResponse<GenericResponse>` for Signup.
        // But `AuthContractImpl` was returning `SignupResponseDTO`?
        // Ah, `AuthContractImpl` previously returned `DTOResponse<SignupResponseDTO>`.
        // But the Interface said `GenericResponse`.
        // This confirms `AuthContractImpl` was diverging.
        // I will return `GenericResponse` as per current Interface.

        return if (success) {
            DTOResponse(true, GenericResponse(true), null)
        } else {
            DTOResponse(false, null, null, ErrorDetailsDTO(ErrorType.ACCOUNT_ALREADY_EXISTS, "User already exists"))
        }
    }

    override suspend fun Logout(body: EmptyRequestDTO): DTOResponse<GenericResponse> {
        return DTOResponse(true, GenericResponse(true), null)
    }

    override suspend fun DeleteAccount(body: EmptyRequestDTO): DTOResponse<MessageResponse> {
        return DTOResponse(true, MessageResponse("Account deleted (simulated)"), null)
    }

    override suspend fun RecoverPassword(body: RecoverPasswordRequestDTO): DTOResponse<MessageResponse> {
        authDomainService.recoverPassword(body.email?.uppercase() ?: "")
        return DTOResponse(true, MessageResponse("Recovery email sent"), null)
    }

    override suspend fun VerifyEmail(body: VerifyEmailRequestDTO): DTOResponse<GenericResponse> {
        val success = authDomainService.verifyEmail(body.code ?: "")
        return if (success) {
            DTOResponse(true, GenericResponse(true), null)
        } else {
            DTOResponse(false, null, null, ErrorDetailsDTO(ErrorType.INVALID_VERIFICATION_CODE, "Invalid code"))
        }
    }

    override suspend fun MustChangePassword(body: EmptyRequestDTO): DTOResponse<MustChangePasswordResponse> {
        return DTOResponse(true, MustChangePasswordResponse(false), null)
    }

    override suspend fun ChangePassword(body: ChangePasswordRequestDTO): DTOResponse<GenericResponse> {
        return DTOResponse(true, GenericResponse(true), null)
    }

    override suspend fun Status(body: EmptyRequestDTO): DTOResponse<UserStatusResponse> {
        return DTOResponse(true, UserStatusResponse(UserStatusDataDTO(isVerified = true, mustChangePassword = false)), null)
    }
}
