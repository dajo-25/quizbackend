package com.quizbackend.features.quiz.questions.models

import com.quizbackend.features.users.Users
import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.ReferenceOption

object Questions : IntIdTable("questions") {
    val creatorId = reference("creator_id", Users, onDelete = ReferenceOption.SET_NULL).nullable()
    val isDiscoverable = bool("is_discoverable").default(false)
}

object Answers : IntIdTable("answers") {
    val questionId = reference("question_id", Questions, onDelete = ReferenceOption.CASCADE)
    val isCorrect = bool("is_correct").default(false)
}

object Localizations : IntIdTable("localizations") {
    val entityId = integer("entity_id")
    val entityType = varchar("entity_type", 50) // "QUESTION", "ANSWER"
    val locale = varchar("locale", 10) // e.g., "en", "es"
    val text = text("text")

    init {
        index(isUnique = false, entityId, entityType)
    }
}
