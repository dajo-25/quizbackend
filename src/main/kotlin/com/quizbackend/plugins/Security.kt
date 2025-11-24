package com.quizbackend.plugins

import com.quizbackend.features.devices.Devices
import com.quizbackend.features.devices.DevicesService
import com.quizbackend.features.users.Users
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.response.*

fun Application.configureSecurity(devicesService: DevicesService) {
    install(Authentication) {
        bearer("auth-bearer") {
            realm = "Quiz API"
            authenticate { tokenCredential ->
                val deviceRow = devicesService.findByToken(tokenCredential.token)
                if (deviceRow != null && deviceRow[Devices.enabled]) {
                    UserIdPrincipal(deviceRow[Devices.user].value.toString())
                } else {
                    null
                }
            }
        }
    }
}
