package com.quizbackend.contracts.features.questions

import kotlinx.serialization.Serializable

@Serializable
data class AnswerData(
    val id: Int,
    val text: String
)

@Serializable
data class CreateQuestionInput(
    val text: String,
    val answers: List<String>,
    val correctAnswers: List<Int>,
    val isDiscoverable: Boolean
)

@Serializable
data class Localization(
    val locale: String,
    val text: String
)

@Serializable
data class UpdateAnswerInput(
    val id: Int,
    val text: String
)
