package com.quizbackend.features.localizations.models

import org.jetbrains.exposed.dao.id.IntIdTable

object QuestionsLocalizations : IntIdTable("questions_localizations") {
    val questionId = integer("question_id")
    val locale = varchar("locale", 10)
    val text = text("text")
}

object AnswersLocalizations : IntIdTable("answers_localizations") {
    val answerId = integer("answer_id")
    val locale = varchar("locale", 10)
    val text = text("text")
}
