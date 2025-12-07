package com.quizbackend.features.marks

import com.quizbackend.contracts.generated.*

class MarksMockContractImpl : MarksService {

    override suspend fun GetMarks(body: EmptyRequestDTO, params: EmptyParamsDTO): DTOResponse<MarkListResponseDTO> {
        return DTOResponse(true, MarkListResponseDTO(emptyList()), null)
    }
}
