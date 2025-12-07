package com.quizbackend.features.devices

import com.quizbackend.contracts.common.base.*
import com.quizbackend.contracts.features.notifications.*

class NotificationsContractImpl : NotificationsService {

    override suspend fun RegisterPushToken(body: RegisterPushTokenRequestDTO): DTOResponse<GenericResponse> {
        return DTOResponse(true, GenericResponse(true), null)
    }

    override suspend fun UnregisterPushToken(body: EmptyRequestDTO): DTOResponse<GenericResponse> {
        return DTOResponse(true, GenericResponse(true), null)
    }
}
