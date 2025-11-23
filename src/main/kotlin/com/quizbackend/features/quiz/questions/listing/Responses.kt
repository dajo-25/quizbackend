package com.quizbackend.features.quiz.questions.listing

import kotlinx.serialization.Serializable

@Serializable
data class QuestionResponse(
    val id: Int,
    val text: String,
    val answers: List<AnswerResponse>
)

@Serializable
data class AnswerResponse(
    val id: Int,
    val text: String,
    val isCorrect: Boolean
)
