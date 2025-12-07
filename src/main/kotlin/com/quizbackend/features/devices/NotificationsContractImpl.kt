package com.quizbackend.features.devices

import com.quizbackend.contracts.generated.*
import com.quizbackend.contracts.generated.DevicesService as DevicesContractService

class NotificationsContractImpl : DevicesContractService {

    override suspend fun PostPushToken(body: RegisterPushTokenRequestDTO, params: EmptyParamsDTO): DTOResponse<GenericResponseDTO> {
        return DTOResponse(true, GenericResponseDTO(true), null)
    }

    override suspend fun DeletePushToken(body: EmptyRequestDTO, params: EmptyParamsDTO): DTOResponse<GenericResponseDTO> {
        return DTOResponse(true, GenericResponseDTO(true), null)
    }
}
