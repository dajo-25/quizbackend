package com.quizbackend.features.quiz.questions

import com.quizbackend.contracts.generated.*
import io.ktor.server.application.*

class QuestionsContractImpl : QuestionsService {

    private val domainService = QuestionsDomainService()

    override suspend fun GetQuestions(body: EmptyRequestDTO, params: SearchQuestionsParamsDTO): DTOResponse<QuestionListResponseDTO> {
        val questions = domainService.getQuestions(
            page = params.page,
            pageSize = 10,
            locale = params.locale ?: "en"
        )
        return DTOResponse(true, QuestionListResponseDTO(questions = questions), null)
    }

    override suspend fun GetQuestionsId(body: EmptyRequestDTO, params: GetQuestionParamsDTO): DTOResponse<QuestionDataResponseDTO> {
        val question = domainService.getQuestionById(params.id, params.locale ?: "en")

        return if (question != null) {
            DTOResponse(true, QuestionDataResponseDTO(question = question), null)
        } else {
            DTOResponse(false, null, null, ErrorDetailsDTO(ErrorType.QUESTION_NOT_FOUND, "Question not found"))
        }
    }

    override suspend fun GetBatch(body: EmptyRequestDTO, params: GetQuestionsBatchParamsDTO): DTOResponse<QuestionListResponseDTO> {
        val questions = domainService.getQuestionsBatch(params.ids, params.locale ?: "en")
        return DTOResponse(true, QuestionListResponseDTO(questions = questions), null)
    }

    override suspend fun PostQuestions(body: CreateQuestionsRequestDTO, params: EmptyParamsDTO): DTOResponse<GenericResponseDTO> {
        // TODO: Get userId from context
        val success = domainService.createQuestions(null, body.questions)
        return if (success) {
            DTOResponse(true, GenericResponseDTO(true), null)
        } else {
            DTOResponse(false, null, null, ErrorDetailsDTO(ErrorType.INTERNAL_SERVER_ERROR, "Failed to create questions"))
        }
    }

    override suspend fun PutQuestionsId(body: UpdateQuestionRequestDTO, params: UpdateQuestionParamsDTO): DTOResponse<QuestionDataResponseDTO> {
        // UpdateQuestionParamsDTO does not support locale yet, usually updates are content-agnostic or send full object.
        // Assuming update returns the object in 'en' or we should add locale to UpdateParams too?
        // The comment only mentioned retrieval DTOs.
        val success = domainService.updateQuestion(params.id, body)
        if (success) {
             val updatedQ = domainService.getQuestionById(params.id, "en")
             return if (updatedQ != null) {
                 DTOResponse(true, QuestionDataResponseDTO(question = updatedQ), null)
             } else {
                 DTOResponse(false, null, null, ErrorDetailsDTO(ErrorType.INTERNAL_SERVER_ERROR, "Failed to retrieve updated question"))
             }
        } else {
             return DTOResponse(false, null, null, ErrorDetailsDTO(ErrorType.QUESTION_NOT_FOUND, "Question not found or update failed"))
        }
    }

    override suspend fun DeleteQuestionsId(body: EmptyRequestDTO, params: DeleteQuestionParamsDTO): DTOResponse<GenericResponseDTO> {
        val success = domainService.deleteQuestion(params.id)
        return if (success) {
            DTOResponse(true, GenericResponseDTO(true), null)
        } else {
            DTOResponse(false, null, null, ErrorDetailsDTO(ErrorType.QUESTION_NOT_FOUND, "Question not found"))
        }
    }
}
