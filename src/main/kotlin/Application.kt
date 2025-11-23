package com.quizbackend

import com.quizbackend.features.quiz.questions.creation.questionsCreationRoutes
import com.quizbackend.features.quiz.questions.listing.questionsListingRoutes
import com.quizbackend.plugins.*
import io.ktor.server.application.*
import io.ktor.server.routing.*

fun main(args: Array<String>) {
    io.ktor.server.netty.EngineMain.main(args)
}

fun Application.module() {
    configureSerialization()
    val jdbcUrl = System.getenv("STORAGE_JDBCURL")
        ?: environment.config.propertyOrNull("storage.jdbcUrl")?.getString()
        ?: "jdbc:sqlite:quiz.db"
    configureDatabases(jdbcUrl)
    configureSecurity()

    routing {
        questionsCreationRoutes()
        questionsListingRoutes()
    }
}
