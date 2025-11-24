package com.quizbackend.features.devices

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.serialization.Serializable

@Serializable
data class PushTokenRequest(val pushToken: String)

fun Application.deviceRoutes(devicesService: DevicesService) {
    routing {
        authenticate("auth-bearer") {
            route("/devices") {
                get("/push-token-status") {
                    val token = call.request.headers["Authorization"]?.removePrefix("Bearer ")?.trim()
                    if (token != null) {
                        val hasToken = devicesService.getPushTokenStatus(token)
                        call.respond(mapOf("has_push_token" to hasToken))
                    } else {
                        call.respond(HttpStatusCode.Unauthorized)
                    }
                }

                post("/push-token") {
                    val token = call.request.headers["Authorization"]?.removePrefix("Bearer ")?.trim()
                    val req = call.receive<PushTokenRequest>()

                    if (token != null) {
                        devicesService.updatePushToken(token, req.pushToken)
                        call.respond(HttpStatusCode.OK)
                    } else {
                        call.respond(HttpStatusCode.Unauthorized)
                    }
                }
            }
        }
    }
}
