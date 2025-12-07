package com.quizbackend.features.quiz.questions

import com.quizbackend.contracts.common.base.*
import com.quizbackend.contracts.features.questions.*
import com.quizbackend.features.quiz.questions.models.Questions
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq

class QuestionsContractImpl : QuestionsService {

    override suspend fun DiscoverQuestions(body: EmptyRequestDTO, params: SearchQuestionsParamsDTO): DTOResponse<QuestionListResponse> {
        return transaction {
            val questionsList = Questions.selectAll().where { Questions.isDiscoverable eq true }
                .limit(10, offset = ((params.page - 1) * 10).toLong())
                .map {
                    QuestionDataDTO(it[Questions.id].value, "Question ${it[Questions.id].value}", emptyList())
                }
            DTOResponse(true, QuestionListResponse(questions = questionsList), null)
        }
    }

    override suspend fun GetQuestion(body: EmptyRequestDTO, params: GetQuestionParamsDTO): DTOResponse<QuestionDataResponse> {
        return transaction {
            val q = Questions.selectAll().where { Questions.id eq params.id }.singleOrNull()
            if (q != null) {
                DTOResponse(true, QuestionDataResponse(question = QuestionDataDTO(q[Questions.id].value, "Question Text", emptyList())), null)
            } else {
                DTOResponse(false, null, null, ErrorDetailsDTO(ErrorType.QUESTION_NOT_FOUND, "Question not found"))
            }
        }
    }

    override suspend fun GetQuestionsBatch(body: EmptyRequestDTO, params: GetQuestionsBatchParamsDTO): DTOResponse<QuestionListResponse> {
        val ids = params.ids
        return transaction {
            val questionsList = Questions.selectAll().where { Questions.id inList ids }
                .map { QuestionDataDTO(it[Questions.id].value, "Question ${it[Questions.id].value}", emptyList()) }
            DTOResponse(true, QuestionListResponse(questions = questionsList), null)
        }
    }

    override suspend fun CreateQuestions(body: CreateQuestionsRequestDTO): DTOResponse<GenericResponse> {
        return DTOResponse(true, GenericResponse(true), null)
    }

    override suspend fun UpdateQuestion(body: UpdateQuestionRequestDTO, params: UpdateQuestionParamsDTO): DTOResponse<QuestionDataResponse> {
        return DTOResponse(true, QuestionDataResponse(question = QuestionDataDTO(params.id, "Updated Question", emptyList())), null)
    }

    override suspend fun DeleteQuestion(body: EmptyRequestDTO, params: DeleteQuestionParamsDTO): DTOResponse<GenericResponse> {
        return DTOResponse(true, GenericResponse(true), null)
    }
}
