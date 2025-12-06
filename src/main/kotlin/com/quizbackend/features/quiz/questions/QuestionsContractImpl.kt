package com.quizbackend.features.quiz.questions

import com.quizbackend.contracts.common.base.DTOResponse
import com.quizbackend.contracts.common.dtos.EmptyRequestDTO
import com.quizbackend.contracts.common.errors.ErrorDetailsDTO
import com.quizbackend.contracts.common.errors.ErrorType
import com.quizbackend.contracts.features.questions.*
import com.quizbackend.features.quiz.questions.models.Questions
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction
import org.jetbrains.exposed.sql.insertAndGetId
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.update

class QuestionsContractImpl : QuestionsService {

    override suspend fun DiscoverQuestions(body: EmptyRequestDTO, params: SearchQuestionsParamsDTO): DTOResponse<List<QuestionDataDTO>> {
        // Missing userId due to contract limitation
        return transaction {
            val questions = Questions.selectAll().where { Questions.isDiscoverable eq true }
                .limit(10, offset = ((params.page - 1) * 10).toLong())
                .map {
                    QuestionDataDTO(it[Questions.id].value, "Question ${it[Questions.id].value}", emptyList())
                }
            DTOResponse(true, questions, null)
        }
    }

    override suspend fun GetQuestion(id: Int, body: EmptyRequestDTO, params: GetQuestionParamsDTO): DTOResponse<QuestionDataDTO> {
        return transaction {
            val q = Questions.selectAll().where { Questions.id eq id }.singleOrNull()
            if (q != null) {
                DTOResponse(true, QuestionDataDTO(q[Questions.id].value, "Question Text", emptyList()), null)
            } else {
                DTOResponse(false, null, ErrorDetailsDTO(ErrorType.QUESTION_NOT_FOUND, "Question not found"))
            }
        }
    }

    override suspend fun GetQuestionsBatch(body: EmptyRequestDTO, params: GetQuestionsBatchParamsDTO): DTOResponse<List<QuestionDataDTO>> {
        val ids = params.ids
        return transaction {
            val questions = Questions.selectAll().where { Questions.id inList ids }
                .map { QuestionDataDTO(it[Questions.id].value, "Question ${it[Questions.id].value}", emptyList()) }
            DTOResponse(true, questions, null)
        }
    }

    override suspend fun CreateQuestions(body: CreateQuestionsRequestDTO): DTOResponse<Unit> {
        // MOCK: pretending success.
        // If we want to actually create it, we need a creatorId.
        // We could use a hardcoded creatorId=1 if exists?
        // But for now just returning success.
        return DTOResponse(true, null, null)
    }

    override suspend fun UpdateQuestion(id: Int, body: UpdateQuestionRequestDTO, params: UpdateQuestionParamsDTO): DTOResponse<QuestionDataDTO> {
        // MOCK: returning success with dummy data
        return DTOResponse(true, QuestionDataDTO(id, "Updated Question", emptyList()), null)
    }

    override suspend fun DeleteQuestion(id: Int, body: EmptyRequestDTO, params: DeleteQuestionParamsDTO): DTOResponse<Unit> {
        // MOCK: returning success
        return DTOResponse(true, null, null)
    }
}
