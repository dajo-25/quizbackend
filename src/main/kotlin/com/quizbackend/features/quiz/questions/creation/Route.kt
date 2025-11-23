package com.quizbackend.features.quiz.questions.creation

import com.quizbackend.features.localizations.models.AnswersLocalizations
import com.quizbackend.features.localizations.models.QuestionsLocalizations
import com.quizbackend.features.quiz.answers.models.Answers
import com.quizbackend.features.quiz.questions.models.Questions
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.insertAndGetId
import org.jetbrains.exposed.sql.transactions.transaction
import io.ktor.http.HttpStatusCode

fun Route.questionsCreationRoutes() {
    authenticate("auth-bearer") {
        post("/questions") {
            val request = call.receive<CreateQuestionsRequest>()

            try {
                validateQuestions(request)
                createQuestions(request)
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

private fun createQuestions(request: CreateQuestionsRequest) {
    transaction {
        request.questions.forEach { questionDto ->
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
            }.value

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
