package com.quizbackend.features.devices

import com.quizbackend.contracts.common.base.DTOResponse
import com.quizbackend.contracts.common.dtos.EmptyRequestDTO
import com.quizbackend.contracts.common.errors.ErrorDetailsDTO
import com.quizbackend.contracts.common.errors.ErrorType
import com.quizbackend.contracts.features.notifications.*

class NotificationsContractImpl : NotificationsService {

    override suspend fun RegisterPushToken(body: RegisterPushTokenRequestDTO): DTOResponse<Unit> {
        // MOCK: Success
        return DTOResponse(true, null, null)
    }

    override suspend fun UnregisterPushToken(body: EmptyRequestDTO): DTOResponse<Unit> {
        // MOCK: Success
        return DTOResponse(true, null, null)
    }
}
