package com.quizbackend.plugins

import com.quizbackend.features.auth.AuthService
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.response.*

fun Application.configureSecurity() {
    install(Authentication) {
        bearer("auth-bearer") {
            realm = "Quiz API"
            authenticate { tokenCredential ->
                if (AuthService.validateToken(tokenCredential.token)) {
                    UserIdPrincipal("user")
                } else {
                    null
                }
            }
        }
        basic("swagger-auth") {
            realm = "Swagger"
            validate { credentials ->
                val user = System.getenv("SWAGGER_USER") ?: "admin"
                val password = System.getenv("SWAGGER_PASSWORD") ?: "admin"
                if (credentials.name == user && credentials.password == password) {
                    UserIdPrincipal(credentials.name)
                } else {
                    null
                }
            }
        }
    }
}
