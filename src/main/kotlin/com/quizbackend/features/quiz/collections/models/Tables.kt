package com.quizbackend.features.quiz.collections.models

import com.quizbackend.features.quiz.questions.models.Questions
import com.quizbackend.features.users.Users
import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.javatime.datetime
import java.time.LocalDateTime

object Collections : IntIdTable("collections") {
    val name = varchar("name", 255)
    val description = text("description").nullable()
    val creatorId = reference("creator_id", Users)
    val isPublic = bool("is_public").default(false)
    val createdAt = datetime("created_at").default(LocalDateTime.now())
}

object CollectionQuestions : IntIdTable("collection_questions") {
    val collectionId = reference("collection_id", Collections)
    val questionId = reference("question_id", Questions)

    init {
        uniqueIndex(collectionId, questionId)
    }
}

object CollectionAccess : IntIdTable("collection_access") {
    val collectionId = reference("collection_id", Collections)
    val userId = reference("user_id", Users)

    init {
        uniqueIndex(collectionId, userId)
    }
}
