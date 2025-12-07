package com.quizbackend.features.marks

import com.quizbackend.contracts.common.base.*
import com.quizbackend.contracts.features.marks.*

class MarksMockContractImpl : MarksService {

    override suspend fun GetMarks(body: EmptyRequestDTO): DTOResponse<MarkListResponse> {
        return DTOResponse(true, MarkListResponse(emptyList()), null)
    }
}
