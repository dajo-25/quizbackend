package com.quizbackend.features.auth

import com.quizbackend.features.users.Users
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.serialization.Serializable

@Serializable
data class SignupRequest(val email: String, val username: String, val name: String, val surname: String, val passwordHash: String)

@Serializable
data class LoginRequest(val email: String, val passwordHash: String, val uniqueId: String)

@Serializable
data class RecoverRequest(val email: String)

@Serializable
data class ChangePasswordRequest(val oldHash: String, val newHash: String)

@Serializable
data class VerifyRequest(val code: String)

@Serializable
data class AuthResponse(val token: String? = null, val message: String? = null)

@Serializable
data class UserStatusResponse(
    val id: Int,
    val email: String,
    val username: String,
    val isVerified: Boolean,
    val mustChangePassword: Boolean
)

fun Application.authRoutes(authService: AuthService) {
    routing {
        route("/auth") {
            post("/signup") {
                val req = call.receive<SignupRequest>()
                if (authService.signup(req.email.uppercase(), req.username, req.name, req.surname, req.passwordHash)) {
                    call.respond(HttpStatusCode.Created, AuthResponse(message = "User created. Check email for verification."))
                } else {
                    call.respond(HttpStatusCode.Conflict, AuthResponse(message = "User already exists"))
                }
            }

            post("/login") {
                val req = call.receive<LoginRequest>()
                val token = authService.login(req.email.uppercase(), req.passwordHash, req.uniqueId)
                if (token != null) {
                    call.respond(AuthResponse(token = token))
                } else {
                    call.respond(HttpStatusCode.Unauthorized, AuthResponse(message = "Invalid credentials"))
                }
            }

            post("/recover") {
                val req = call.receive<RecoverRequest>()
                authService.recoverPassword(req.email.uppercase())
                call.respond(AuthResponse(message = "If email exists, recovery email sent."))
            }

            post("/verify") {
                val req = call.receive<VerifyRequest>()
                if (authService.verifyEmail(req.code)) {
                    call.respond(AuthResponse(message = "Email verified successfully"))
                } else {
                    call.respond(HttpStatusCode.BadRequest, AuthResponse(message = "Invalid verification code"))
                }
            }

            authenticate("auth-bearer") {
                post("/logout") {
                    val token = call.request.headers["Authorization"]?.removePrefix("Bearer ")?.trim()
                    if (token != null) {
                        authService.logout(token)
                    }
                    call.respond(HttpStatusCode.OK)
                }

                post("/change-password") {
                    val userId = call.principal<UserIdPrincipal>()?.name?.toIntOrNull()
                    val req = call.receive<ChangePasswordRequest>()

                    if (userId != null && authService.changePassword(userId, req.oldHash, req.newHash)) {
                        call.respond(HttpStatusCode.OK)
                    } else {
                        call.respond(HttpStatusCode.Unauthorized, AuthResponse(message = "Invalid password or user"))
                    }
                }

                get("/must-change-password") {
                    val userId = call.principal<UserIdPrincipal>()?.name?.toIntOrNull()
                    val user = userId?.let { authService.getUser(it) }

                    if (user != null) {
                        val mustChange = user[Users.mustChangePassword]
                        call.respond(mapOf("must_change_password" to mustChange))
                    } else {
                        call.respond(HttpStatusCode.Unauthorized)
                    }
                }

                get("/status") {
                    val userId = call.principal<UserIdPrincipal>()?.name?.toIntOrNull()
                    val user = userId?.let { authService.getUser(it) }

                    if (user != null) {
                        call.respond(UserStatusResponse(
                            id = user[Users.id].value,
                            email = user[Users.email],
                            username = user[Users.username],
                            isVerified = user[Users.isVerified],
                            mustChangePassword = user[Users.mustChangePassword]
                        ))
                    } else {
                        call.respond(HttpStatusCode.Unauthorized)
                    }
                }
            }
        }
    }
}
