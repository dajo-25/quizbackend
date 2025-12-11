package com.quizbackend.features.quiz.questions

import com.quizbackend.contracts.generated.*
import com.quizbackend.features.quiz.questions.models.Answers
import com.quizbackend.features.quiz.questions.models.Localizations
import com.quizbackend.features.quiz.questions.models.Questions
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.SqlExpressionBuilder.inList
import org.jetbrains.exposed.sql.transactions.transaction

class QuestionsDomainService {

    suspend fun createQuestions(creatorId: Int?, questionsData: List<CreateQuestionInputDTO>): Boolean {
        return transaction {
            questionsData.forEach { qDto ->
                // 1. Create Question
                val qId = Questions.insertAndGetId {
                    it[this.creatorId] = creatorId
                    it[this.isDiscoverable] = qDto.isDiscoverable
                }.value

                // 2. Add Localizations for Question
                qDto.localizations.forEach { loc ->
                    Localizations.insert {
                        it[this.entityId] = qId
                        it[this.entityType] = "QUESTION"
                        it[this.locale] = loc.locale
                        it[this.text] = loc.text
                    }
                }

                // 3. Add Answers
                qDto.answers.forEachIndexed { index, aDto ->
                    val aId = Answers.insertAndGetId {
                        it[this.questionId] = qId
                        it[this.isCorrect] = qDto.correctAnswersIndices.contains(index)
                    }.value

                    // 4. Add Localizations for Answer
                    aDto.localizations.forEach { loc ->
                        Localizations.insert {
                            it[this.entityId] = aId
                            it[this.entityType] = "ANSWER"
                            it[this.locale] = loc.locale
                            it[this.text] = loc.text
                        }
                    }
                }
            }
            true
        }
    }

    suspend fun getQuestions(
        page: Int,
        pageSize: Int,
        locale: String
    ): List<QuestionDataDTO> {
        return transaction {
            // 1. Fetch Questions (Pagination)
            val questions = Questions.selectAll()
                .where { Questions.isDiscoverable eq true }
                .limit(pageSize, offset = ((page - 1) * pageSize).toLong())
                .toList()

            if (questions.isEmpty()) return@transaction emptyList()

            enrichQuestions(questions, locale)
        }
    }

    suspend fun getQuestionById(id: Int, locale: String): QuestionDataDTO? {
        return transaction {
            val qRow = Questions.selectAll().where { Questions.id eq id }.singleOrNull() ?: return@transaction null
            enrichQuestions(listOf(qRow), locale).firstOrNull()
        }
    }

    suspend fun getQuestionsBatch(ids: List<Int>, locale: String): List<QuestionDataDTO> {
        return transaction {
            val questions = Questions.selectAll().where { Questions.id inList ids }.toList()
            if (questions.isEmpty()) return@transaction emptyList()
            enrichQuestions(questions, locale)
        }
    }

    suspend fun updateQuestion(id: Int, dto: UpdateQuestionRequestDTO): Boolean {
        return transaction {
             val qExists = Questions.selectAll().where { Questions.id eq id }.count() > 0
             if (!qExists) return@transaction false

             // Update Question fields
             Questions.update({ Questions.id eq id }) {
                 it[isDiscoverable] = dto.isDiscoverable
             }

             // Update Question Localizations: Delete all and re-insert (simplest strategy)
             Localizations.deleteWhere { (entityId eq id) and (entityType eq "QUESTION") }
             dto.localizations.forEach { loc ->
                 Localizations.insert {
                     it[entityId] = id
                     it[entityType] = "QUESTION"
                     it[locale] = loc.locale
                     it[text] = loc.text
                 }
             }

             // Update Answers: Delete all and re-insert
             // First delete localizations of answers we are about to delete
             val answerIds = Answers.select(Answers.id).where { Answers.questionId eq id }.map { it[Answers.id].value }

             if (answerIds.isNotEmpty()) {
                Localizations.deleteWhere { (entityId inList answerIds) and (entityType eq "ANSWER") }
                Answers.deleteWhere { questionId eq id }
             }

             dto.answers.forEachIndexed { index, aDto ->
                val aId = Answers.insertAndGetId {
                    it[this.questionId] = id
                    it[this.isCorrect] = dto.correctAnswersIndices.contains(index)
                }.value

                aDto.localizations.forEach { loc ->
                    Localizations.insert {
                        it[this.entityId] = aId
                        it[this.entityType] = "ANSWER"
                        it[this.locale] = loc.locale
                        it[this.text] = loc.text
                    }
                }
             }
             true
        }
    }

    suspend fun deleteQuestion(id: Int): Boolean {
        return transaction {
            // Delete Localizations for Question
            Localizations.deleteWhere { (entityId eq id) and (entityType eq "QUESTION") }

            // Delete Localizations for Answers
            val answerIds = Answers.select(Answers.id).where { Answers.questionId eq id }.map { it[Answers.id].value }
            if (answerIds.isNotEmpty()) {
                Localizations.deleteWhere { (entityId inList answerIds) and (entityType eq "ANSWER") }
            }

            // Delete Answers
            Answers.deleteWhere { questionId eq id }

            // Delete Question
            val count = Questions.deleteWhere { Questions.id eq id }
            count > 0
        }
    }

    // Helper to join data
    private fun enrichQuestions(questionRows: List<ResultRow>, locale: String): List<QuestionDataDTO> {
        val qIds = questionRows.map { it[Questions.id].value }

        // Fetch all answers for these questions
        val answersMap = Answers.selectAll().where { Answers.questionId inList qIds }
            .groupBy { it[Answers.questionId].value }

        val allAnswerIds = answersMap.values.flatten().map { it[Answers.id].value }

        // Fetch localizations for Questions
        val qLocs = Localizations.selectAll()
            .where { (Localizations.entityId inList qIds) and (Localizations.entityType eq "QUESTION") }
            .groupBy { it[Localizations.entityId] }

        // Fetch localizations for Answers
        val aLocs = if (allAnswerIds.isNotEmpty()) {
            Localizations.selectAll()
                .where { (Localizations.entityId inList allAnswerIds) and (Localizations.entityType eq "ANSWER") }
                .groupBy { it[Localizations.entityId] }
        } else {
            emptyMap()
        }

        return questionRows.map { row ->
            val qId = row[Questions.id].value

            // Resolve Question Text (Locale Fallback)
            val qText = resolveText(qLocs[qId], locale)

            // Resolve Answers
            val qAnswers = answersMap[qId]?.map { aRow ->
                val aId = aRow[Answers.id].value
                val aText = resolveText(aLocs[aId], locale)
                AnswerDataDTO(
                    id = aId,
                    text = aText,
                    isCorrect = aRow[Answers.isCorrect]
                )
            } ?: emptyList()

            QuestionDataDTO(
                id = qId,
                text = qText,
                answers = qAnswers
            )
        }
    }

    private fun resolveText(locRows: List<ResultRow>?, targetLocale: String): String {
        if (locRows.isNullOrEmpty()) return "???"

        // 1. Try exact match
        val exact = locRows.find { it[Localizations.locale] == targetLocale }
        if (exact != null) return exact[Localizations.text]

        // 2. Try default 'en'
        val en = locRows.find { it[Localizations.locale] == "en" }
        if (en != null) return en[Localizations.text]

        // 3. Return first available
        return locRows.first()[Localizations.text]
    }
}
