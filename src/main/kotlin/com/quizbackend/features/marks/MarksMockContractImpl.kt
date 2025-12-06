package com.quizbackend.features.marks

import com.quizbackend.contracts.common.base.DTOResponse
import com.quizbackend.contracts.common.dtos.EmptyRequestDTO
import com.quizbackend.contracts.common.errors.ErrorDetailsDTO
import com.quizbackend.contracts.common.errors.ErrorType
import com.quizbackend.contracts.features.marks.*

class MarksMockContractImpl : MarksService {
    override suspend fun GetMarks(body: EmptyRequestDTO, params: GetMarksParamsDTO): DTOResponse<List<MarkDataDTO>> {
        // Missing userId due to contract limitation
        return DTOResponse(
            true,
            listOf(MarkDataDTO(1, 101, true, "2023-10-27T10:00:00Z")),
            null
        )
    }
}
