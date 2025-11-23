package com.quizbackend.features.quiz.questions.listing

import com.quizbackend.features.localizations.models.AnswersLocalizations
import com.quizbackend.features.localizations.models.QuestionsLocalizations
import com.quizbackend.features.quiz.questions.models.Questions
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction

fun Route.questionsListingRoutes() {
    authenticate("auth-bearer") {
        get("/questions") {
            val locale = call.request.queryParameters["locale"] ?: "en"
            val page = call.request.queryParameters["page"]?.toIntOrNull() ?: 1
            val pageSize = 10
            val offset = (page - 1) * pageSize

            val response = transaction {
                // Fetch questions with pagination
                val questionsQuery = Questions.selectAll()
                    .limit(pageSize, offset = offset.toLong())

                questionsQuery.map { questionRow ->
                    val questionId = questionRow[Questions.id].value

                    // Fetch Question Text for Locale
                    val questionText = QuestionsLocalizations
                        .selectAll().where { (QuestionsLocalizations.questionId eq questionId) and (QuestionsLocalizations.locale eq locale) }
                        .singleOrNull()
                        ?.get(QuestionsLocalizations.text) ?: "Missing Translation"

                    // Parse Answer IDs
                    val possibleAnswerIds = questionRow[Questions.possibleAnswersIds]
                        .split(",")
                        .filter { it.isNotBlank() }
                        .mapNotNull { it.toIntOrNull() }

                    // Parse Correct Answer IDs
                    val correctAnswerIds = questionRow[Questions.correctAnswers]
                        .split(",")
                        .filter { it.isNotBlank() }
                        .mapNotNull { it.toIntOrNull() }
                        .toSet()

                    // Fetch Answers Texts for Locale
                    val answers = if (possibleAnswerIds.isNotEmpty()) {
                        AnswersLocalizations
                            .selectAll().where { (AnswersLocalizations.answerId inList possibleAnswerIds) and (AnswersLocalizations.locale eq locale) }
                            .map { answerRow ->
                                val answerId = answerRow[AnswersLocalizations.answerId]
                                AnswerResponse(
                                    id = answerId,
                                    text = answerRow[AnswersLocalizations.text],
                                    isCorrect = answerId in correctAnswerIds
                                )
                            }
                    } else {
                        emptyList()
                    }

                    QuestionResponse(
                        id = questionId,
                        text = questionText,
                        answers = answers
                    )
                }
            }

            call.respond(response)
        }
    }
}
