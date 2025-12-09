package com.quizbackend.plugins

import com.quizbackend.contracts.generated.DTOResponse
import com.quizbackend.contracts.generated.ErrorDetailsDTO
import com.quizbackend.contracts.generated.ErrorType
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.plugins.*
import io.ktor.server.plugins.statuspages.*
import io.ktor.server.response.*

fun Application.configureStatusPages() {
    install(StatusPages) {
        status(HttpStatusCode.NotFound) { call, status ->
            call.respond(
                HttpStatusCode.OK,
                DTOResponse<Unit>(
                    success = false,
                    message = "Route not found or bad implemented",
                    error = ErrorDetailsDTO(ErrorType.ROUTE_NOT_EXISTS_OR_BAD_IMPLEMENTED, "Route not found or bad implemented")
                )
            )
        }
        status(HttpStatusCode.Unauthorized) { call, status ->
            call.respond(
                HttpStatusCode.OK,
                DTOResponse<Unit>(
                    success = false,
                    message = "Unauthorized",
                    error = ErrorDetailsDTO(ErrorType.UNAUTHORIZED, "Unauthorized")
                )
            )
        }
        status(HttpStatusCode.Forbidden) { call, status ->
            call.respond(
                HttpStatusCode.OK,
                DTOResponse<Unit>(
                    success = false,
                    message = "Forbidden",
                    error = ErrorDetailsDTO(ErrorType.FORBIDDEN, "Forbidden")
                )
            )
        }
        status(HttpStatusCode.MethodNotAllowed) { call, status ->
            call.respond(
                HttpStatusCode.OK,
                DTOResponse<Unit>(
                    success = false,
                    message = "Method not allowed",
                    error = ErrorDetailsDTO(ErrorType.METHOD_NOT_ALLOWED, "Method not allowed")
                )
            )
        }
        status(HttpStatusCode.InternalServerError) { call, status ->
            call.respond(
                HttpStatusCode.OK,
                DTOResponse<Unit>(
                    success = false,
                    message = "Internal server error",
                    error = ErrorDetailsDTO(ErrorType.INTERNAL_SERVER_ERROR, "Internal server error")
                )
            )
        }

        exception<BadRequestException> { call, cause ->
            call.respond(
                HttpStatusCode.OK,
                DTOResponse<Unit>(
                    success = false,
                    message = cause.message ?: "Bad Request",
                    error = ErrorDetailsDTO(ErrorType.BAD_REQUEST, cause.message ?: "Bad Request")
                )
            )
        }

        exception<MissingRequestParameterException> { call, cause ->
            call.respond(
                HttpStatusCode.OK,
                DTOResponse<Unit>(
                    success = false,
                    message = "Missing parameter: ${cause.parameterName}",
                    error = ErrorDetailsDTO(ErrorType.BAD_REQUEST, "Missing parameter: ${cause.parameterName}")
                )
            )
        }

        exception<Throwable> { call, cause ->
            cause.printStackTrace() // Log the error
            call.respond(
                HttpStatusCode.OK,
                DTOResponse<Unit>(
                    success = false,
                    message = "Internal server error",
                    error = ErrorDetailsDTO(ErrorType.INTERNAL_SERVER_ERROR, "Internal server error")
                )
            )
        }
    }
}
