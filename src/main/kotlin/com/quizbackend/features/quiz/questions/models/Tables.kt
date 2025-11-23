package com.quizbackend.features.quiz.questions.models

import org.jetbrains.exposed.dao.id.IntIdTable

object Questions : IntIdTable("questions") {
    val possibleAnswersIds = text("possible_answers_ids") // Storing CSV of IDs
    val correctAnswers = text("correct_answers") // Storing CSV of IDs (or one ID)
}
