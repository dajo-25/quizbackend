package com.quizbackend.features.quiz.questions.creation

import kotlinx.serialization.Serializable

@Serializable
data class CreateQuestionsRequest(
    val questions: List<CreateQuestionDto>
)

@Serializable
data class CreateQuestionDto(
    val localizations: List<LocalizationDto>,
    val answers: List<CreateAnswerDto>,
    val correctAnswersIndices: List<Int>, // Indices in the 'answers' list that are correct
    val isDiscoverable: Boolean? = null, // Defaults to true if null and no private collection
    val collectionIds: List<Int> = emptyList()
)

@Serializable
data class CreateAnswerDto(
    val localizations: List<LocalizationDto>
)

@Serializable
data class LocalizationDto(
    val locale: String,
    val text: String
)
