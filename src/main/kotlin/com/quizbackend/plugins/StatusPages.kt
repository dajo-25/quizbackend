package com.quizbackend.plugins

import com.quizbackend.contracts.common.errors.ErrorDetailsDTO
import com.quizbackend.contracts.common.errors.ErrorType
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.plugins.*
import io.ktor.server.plugins.statuspages.*
import io.ktor.server.response.*

fun Application.configureStatusPages() {
    install(StatusPages) {
        status(HttpStatusCode.NotFound) { call, status ->
            call.respond(
                HttpStatusCode.NotFound,
                ErrorDetailsDTO(ErrorType.ROUTE_NOT_EXISTS_OR_BAD_IMPLEMENTED, "Route not found or bad implemented")
            )
        }
        status(HttpStatusCode.Unauthorized) { call, status ->
            call.respond(
                HttpStatusCode.Unauthorized,
                ErrorDetailsDTO(ErrorType.UNAUTHORIZED, "Unauthorized")
            )
        }
        status(HttpStatusCode.Forbidden) { call, status ->
            call.respond(
                HttpStatusCode.Forbidden,
                ErrorDetailsDTO(ErrorType.FORBIDDEN, "Forbidden")
            )
        }
        status(HttpStatusCode.MethodNotAllowed) { call, status ->
            call.respond(
                HttpStatusCode.MethodNotAllowed,
                ErrorDetailsDTO(ErrorType.METHOD_NOT_ALLOWED, "Method not allowed")
            )
        }
        status(HttpStatusCode.InternalServerError) { call, status ->
            call.respond(
                HttpStatusCode.InternalServerError,
                ErrorDetailsDTO(ErrorType.INTERNAL_SERVER_ERROR, "Internal server error")
            )
        }

        exception<BadRequestException> { call, cause ->
            call.respond(
                HttpStatusCode.BadRequest,
                ErrorDetailsDTO(ErrorType.BAD_REQUEST, cause.message ?: "Bad Request")
            )
        }

        exception<MissingRequestParameterException> { call, cause ->
            call.respond(
                HttpStatusCode.BadRequest,
                ErrorDetailsDTO(ErrorType.BAD_REQUEST, "Missing parameter: ${cause.parameterName}")
            )
        }

        exception<Throwable> { call, cause ->
            cause.printStackTrace() // Log the error
            call.respond(
                HttpStatusCode.InternalServerError,
                ErrorDetailsDTO(ErrorType.INTERNAL_SERVER_ERROR, "Internal server error")
            )
        }
    }
}
