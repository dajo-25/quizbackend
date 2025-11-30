package com.quizbackend.features.quiz.questions.models

import com.quizbackend.features.users.Users
import org.jetbrains.exposed.dao.id.IntIdTable

object Questions : IntIdTable("questions") {
    val possibleAnswersIds = text("possible_answers_ids") // Storing CSV of IDs
    val correctAnswers = text("correct_answers") // Storing CSV of IDs (or one ID)
    val creatorId = reference("creator_id", Users).nullable() // Nullable for migration compatibility, though logic will enforce it
    val isDiscoverable = bool("is_discoverable").default(false)
}
