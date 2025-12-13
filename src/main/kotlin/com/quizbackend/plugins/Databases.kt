package com.quizbackend.plugins

import com.quizbackend.features.localizations.models.AnswersLocalizations
import com.quizbackend.features.localizations.models.QuestionsLocalizations
import com.quizbackend.features.devices.Devices
import com.quizbackend.features.communities.FriendRequests
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
        val tables = arrayOf(
            Questions,
            Answers,
            QuestionsLocalizations,
            AnswersLocalizations,
            Users,
            Devices,
            Collections,
            CollectionQuestions,
            CollectionAccess,
            FriendRequests
        )

        SchemaUtils.create(tables = tables)

        // Manual migration to add missing columns if any
        // We use statementsRequiredToActualizeScheme because MigrationUtils is not easily available
        // and createMissingTablesAndColumns is deprecated.
        // This ensures the database is operative even if columns are missing.
        @Suppress("DEPRECATION")
        val statements = SchemaUtils.statementsRequiredToActualizeScheme(tables = tables)
        statements.forEach { statement ->
            try {
                exec(statement) { }
            } catch (e: Exception) {
                // Log or ignore if harmless. For now, we assume it's safe to try.
                // In a real scenario, we might want to log this.
                println("Migration warning: Failed to execute '$statement': ${e.message}")
            }
        }
    }
}
