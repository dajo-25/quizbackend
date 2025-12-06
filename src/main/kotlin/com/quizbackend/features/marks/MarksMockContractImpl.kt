package com.quizbackend.features.marks

import com.quizbackend.contracts.common.base.DTOResponse
import com.quizbackend.contracts.common.dtos.EmptyRequestDTO
import com.quizbackend.contracts.features.marks.*

class MarksMockContractImpl : MarksService {
    override suspend fun GetMarks(body: EmptyRequestDTO, params: GetMarksParamsDTO, userId: Int): DTOResponse<List<MarkDataDTO>> {
        return DTOResponse(
            true,
            listOf(MarkDataDTO(1, 101, true, "2023-10-27T10:00:00Z")),
            null
        )
    }
}
