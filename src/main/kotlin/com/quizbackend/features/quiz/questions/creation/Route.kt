package com.quizbackend.features.quiz.questions.creation

import com.quizbackend.features.localizations.models.AnswersLocalizations
import com.quizbackend.features.localizations.models.QuestionsLocalizations
import com.quizbackend.features.quiz.answers.models.Answers
import com.quizbackend.features.quiz.collections.models.CollectionQuestions
import com.quizbackend.features.quiz.collections.models.Collections
import com.quizbackend.features.quiz.questions.models.Questions
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction
import io.ktor.http.HttpStatusCode

fun Route.questionsCreationRoutes() {
    authenticate("auth-bearer") {
        post("/questions") {
            val userId = call.principal<UserIdPrincipal>()?.name?.toIntOrNull()
            if (userId == null) {
                call.respond(HttpStatusCode.Unauthorized)
                return@post
            }
            val request = call.receive<CreateQuestionsRequest>()

            try {
                validateQuestions(request)
                createQuestions(request, userId)
                call.respond(HttpStatusCode.Created)
            } catch (e: IllegalArgumentException) {
                call.respond(HttpStatusCode.BadRequest, e.message ?: "Invalid request")
            } catch (e: Exception) {
                call.respond(HttpStatusCode.InternalServerError, e.message ?: "Internal Server Error")
            }
        }
    }
}

private fun validateQuestions(request: CreateQuestionsRequest) {
    request.questions.forEach { question ->
        val questionLocales = question.localizations.map { it.locale }.toSet()

        if (questionLocales.isEmpty()) {
            throw IllegalArgumentException("Question must have at least one localization")
        }

        question.answers.forEach { answer ->
            val answerLocales = answer.localizations.map { it.locale }.toSet()
            if (questionLocales != answerLocales) {
                 throw IllegalArgumentException("Inconsistent locales between question and answer. Question locales: $questionLocales, Answer locales: $answerLocales")
            }
        }
    }
}

private fun createQuestions(request: CreateQuestionsRequest, userId: Int) {
    transaction {
        request.questions.forEach { questionDto ->
            // Validate Collections Logic
            var finalIsDiscoverable = questionDto.isDiscoverable ?: true // Default to true

            if (questionDto.collectionIds.isNotEmpty()) {
                // Check if user owns these collections and if they are public
                val collections = Collections.selectAll().where { Collections.id inList questionDto.collectionIds }.toList()

                if (collections.size != questionDto.collectionIds.size) {
                    throw IllegalArgumentException("One or more collection IDs not found")
                }

                val hasPublicCollection = collections.any { it[Collections.isPublic] }
                val allCollectionsOwnedByUser = collections.all { it[Collections.creatorId].value == userId }

                if (!allCollectionsOwnedByUser) {
                    // For now, enforce that you can only add questions to your own collections during creation
                    throw IllegalArgumentException("You can only add questions to your own collections")
                }

                if (hasPublicCollection) {
                    finalIsDiscoverable = true
                }
            }

            // "Undiscoverable questions can only exist inside of a collection created by the same person"
            // "If they are not linked to a collection, that question is discoverable."
            if (!finalIsDiscoverable) {
                if (questionDto.collectionIds.isEmpty()) {
                     throw IllegalArgumentException("Undiscoverable questions must be linked to a private collection")
                }
                // We already checked that collections are owned by user above
            }

            // Create Answers first to get IDs
            val answerIds = questionDto.answers.map { answerDto ->
                val answerId = Answers.insertAndGetId { }.value

                answerDto.localizations.forEach { loc ->
                    AnswersLocalizations.insert {
                        it[this.answerId] = answerId
                        it[locale] = loc.locale
                        it[text] = loc.text
                    }
                }
                answerId
            }

            // Determine correct answer IDs
            val correctAnswerIds = questionDto.correctAnswersIndices.map { index ->
                if (index < 0 || index >= answerIds.size) {
                    throw IllegalArgumentException("Invalid correct answer index: $index")
                }
                answerIds[index]
            }

            // Create Question
            val questionId = Questions.insertAndGetId {
                it[possibleAnswersIds] = answerIds.joinToString(",")
                it[correctAnswers] = correctAnswerIds.joinToString(",")
                it[creatorId] = userId
                it[isDiscoverable] = finalIsDiscoverable
            }.value

            // Link to Collections
            questionDto.collectionIds.forEach { colId ->
                CollectionQuestions.insert {
                    it[collectionId] = colId
                    it[this.questionId] = questionId
                }
            }

            // Create Question Localizations
            questionDto.localizations.forEach { loc ->
                QuestionsLocalizations.insert {
                    it[this.questionId] = questionId
                    it[locale] = loc.locale
                    it[text] = loc.text
                }
            }
        }
    }
}
