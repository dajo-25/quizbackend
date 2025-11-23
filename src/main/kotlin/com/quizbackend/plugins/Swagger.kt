package com.quizbackend.plugins

import io.github.smiley4.ktoropenapi.OpenApi
import io.github.smiley4.ktoropenapi.config.AuthScheme
import io.github.smiley4.ktoropenapi.config.AuthType
import io.github.smiley4.ktoropenapi.openApi
import io.github.smiley4.ktorswaggerui.swaggerUI
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.routing.*

fun Application.configureSwagger() {
    install(OpenApi) {
        info {
            title = "Quiz API"
            version = "latest"
            description = "Quiz API Documentation"
        }
        server {
            url = "http://localhost:8080"
            description = "Development Server"
        }
        security {
            securityScheme("auth-bearer") {
                type = AuthType.HTTP
                scheme = AuthScheme.BEARER
                bearerFormat = "jwt"
            }
            defaultSecuritySchemeNames("auth-bearer")
        }
    }

    routing {
        authenticate("swagger-auth") {
            route("swagger") {
                swaggerUI("/swagger/openapi.json")
            }
            route("swagger/openapi.json") {
                openApi()
            }
        }
    }
}
