package com.quizbackend.features.devices

import com.quizbackend.contracts.common.base.*
import com.quizbackend.contracts.features.notifications.*

class NotificationsContractImpl : NotificationsService {

    override suspend fun RegisterPushToken(body: RegisterPushTokenRequestDTO): DTOResponse<GenericResponseDTO> {
        return DTOResponse(true, GenericResponseDTO(true), null)
    }

    override suspend fun UnregisterPushToken(body: EmptyRequestDTO): DTOResponse<GenericResponseDTO> {
        return DTOResponse(true, GenericResponseDTO(true), null)
    }
}
