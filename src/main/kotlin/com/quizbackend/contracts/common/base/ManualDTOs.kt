package com.quizbackend.contracts.common.base

import kotlinx.serialization.Serializable

@Serializable
abstract class DTOParams

@Serializable
data class SearchQuestionsParamsDTO(
    val page: Int = 1
) : DTOParams()

@Serializable
data class GetQuestionParamsDTO(
    val id: Int
) : DTOParams()

@Serializable
data class GetQuestionsBatchParamsDTO(
    val ids: List<Int>
) : DTOParams()

@Serializable
data class UpdateQuestionParamsDTO(
    val id: Int
) : DTOParams()

@Serializable
data class DeleteQuestionParamsDTO(
    val id: Int
) : DTOParams()

@Serializable
data class UpdateCollectionParamsDTO(
    val id: Int
) : DTOParams()
