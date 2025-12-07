package com.quizbackend.features.quiz.questions

import com.quizbackend.contracts.generated.*
import com.quizbackend.features.quiz.questions.models.Questions
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq

class QuestionsContractImpl : QuestionsService {

    override suspend fun GetQuestions(body: EmptyRequestDTO, params: SearchQuestionsParamsDTO): DTOResponse<QuestionListResponseDTO> {
        return transaction {
            val questionsList = Questions.selectAll().where { Questions.isDiscoverable eq true }
                .limit(10, offset = ((params.page - 1) * 10).toLong())
                .map {
                    QuestionDataDTO(it[Questions.id].value, "Question ${it[Questions.id].value}", emptyList())
                }
            DTOResponse(true, QuestionListResponseDTO(questions = questionsList), null)
        }
    }

    override suspend fun GetQuestionsId(body: EmptyRequestDTO, params: GetQuestionParamsDTO): DTOResponse<QuestionDataResponseDTO> {
        return transaction {
            val q = Questions.selectAll().where { Questions.id eq params.id }.singleOrNull()
            if (q != null) {
                DTOResponse(true, QuestionDataResponseDTO(question = QuestionDataDTO(q[Questions.id].value, "Question Text", emptyList())), null)
            } else {
                DTOResponse(false, null, null, ErrorDetailsDTO(ErrorType.QUESTION_NOT_FOUND, "Question not found"))
            }
        }
    }

    override suspend fun GetBatch(body: EmptyRequestDTO, params: GetQuestionsBatchParamsDTO): DTOResponse<QuestionListResponseDTO> {
        val ids = params.ids
        return transaction {
            val questionsList = Questions.selectAll().where { Questions.id inList ids }
                .map { QuestionDataDTO(it[Questions.id].value, "Question ${it[Questions.id].value}", emptyList()) }
            DTOResponse(true, QuestionListResponseDTO(questions = questionsList), null)
        }
    }

    override suspend fun PostQuestions(body: CreateQuestionsRequestDTO, params: EmptyParamsDTO): DTOResponse<GenericResponseDTO> {
        return DTOResponse(true, GenericResponseDTO(true), null)
    }

    override suspend fun PutQuestionsId(body: UpdateQuestionRequestDTO, params: UpdateQuestionParamsDTO): DTOResponse<QuestionDataResponseDTO> {
        return DTOResponse(true, QuestionDataResponseDTO(question = QuestionDataDTO(params.id, "Updated Question", emptyList())), null)
    }

    override suspend fun DeleteQuestionsId(body: EmptyRequestDTO, params: DeleteQuestionParamsDTO): DTOResponse<GenericResponseDTO> {
        return DTOResponse(true, GenericResponseDTO(true), null)
    }
}
