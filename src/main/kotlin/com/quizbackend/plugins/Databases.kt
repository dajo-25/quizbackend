package com.quizbackend.plugins

import com.quizbackend.features.localizations.models.AnswersLocalizations
import com.quizbackend.features.localizations.models.QuestionsLocalizations
import com.quizbackend.features.devices.Devices
import com.quizbackend.features.quiz.answers.models.Answers
import com.quizbackend.features.quiz.collections.models.CollectionAccess
import com.quizbackend.features.quiz.collections.models.CollectionQuestions
import com.quizbackend.features.quiz.collections.models.Collections
import com.quizbackend.features.quiz.questions.models.Questions
import com.quizbackend.features.users.Users
import io.ktor.server.application.*
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.transaction
import java.io.File

fun Application.configureDatabases(jdbcUrl: String = "jdbc:sqlite:quiz.db") {
    Database.connect(
        url = jdbcUrl,
        driver = "org.sqlite.JDBC"
    )

    transaction {
        SchemaUtils.create(
            Questions,
            Answers,
            QuestionsLocalizations,
            AnswersLocalizations,
            Users,
            Devices,
            Collections,
            CollectionQuestions,
            CollectionAccess
        )
    }
}
