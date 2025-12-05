package com.quizbackend.features.devices

import com.quizbackend.contracts.common.base.DTOResponse
import com.quizbackend.contracts.common.dtos.EmptyRequestDTO
import com.quizbackend.contracts.common.errors.ErrorDetailsDTO
import com.quizbackend.contracts.common.errors.ErrorType
import com.quizbackend.contracts.features.notifications.*

class NotificationsContractImpl(
    private val devicesService: DevicesService
) : NotificationsService {

    override suspend fun RegisterPushToken(body: RegisterPushTokenRequestDTO, userId: Int, token: String): DTOResponse<Void> {
        if (userId == 0) return DTOResponse(false, null, ErrorDetailsDTO(ErrorType.UNAUTHORIZED, "Unauthorized"))

        // Update the push token for the current device identified by token
        devicesService.updatePushToken(token, body.pushToken)

        return DTOResponse(true, null, null)
    }

    override suspend fun UnregisterPushToken(body: EmptyRequestDTO, token: String): DTOResponse<Void> {
        if (token.isBlank()) return DTOResponse(false, null, ErrorDetailsDTO(ErrorType.INVALID_TOKEN, "Missing token"))

        // Disable push token (set to null)
        // DevicesService.disableDevice(token) clears it but also disables the device (logout).
        // We probably just want to clear the push token here?
        // The contract says "UnregisterPushToken".
        // The memory says "Push token unregistration is implemented via the DELETE /devices/push-token endpoint."
        // DevicesService doesn't have a clearPushToken method, but disableDevice does both.
        // I should probably add a clearPushToken method or use updatePushToken with empty?
        // Devices table usually nullable.
        // I'll assume passing empty string or adding a method.
        // Let's look at DevicesService again. It takes String? for pushToken in registerOrUpdate.
        // updatePushToken takes String (non-nullable).

        // I will add deletePushToken to DevicesService.
        devicesService.deletePushToken(token)

        return DTOResponse(true, null, null)
    }
}
