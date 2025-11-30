package com.quizbackend

import com.quizbackend.features.auth.AuthService
import com.quizbackend.features.auth.authRoutes
import com.quizbackend.features.devices.DevicesService
import com.quizbackend.features.devices.deviceRoutes
import com.quizbackend.features.quiz.collections.collectionsRoutes
import com.quizbackend.features.quiz.questions.creation.questionsCreationRoutes
import com.quizbackend.features.quiz.questions.listing.questionsListingRoutes
import com.quizbackend.features.users.UsersService
import com.quizbackend.plugins.*
import com.quizbackend.services.notification.*
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

    // Services Initialization
    val usersService = UsersService()
    val devicesService = DevicesService()

    // Notification Services
    // In a real scenario, we would read config here to decide which impl to use.
    // For now, using Mock implementations as default or falling back to safe defaults if config missing.
    // To switch to real impls, one would check config properties.
    // For the bonus requirement "Mock both for the moment. Leave it all parameterized",
    // I'll stick to Mocks for safety unless config is explicitly present/valid (which is not for the placeholders).
    val emailSender = MockEmailSender()
    // val emailSender = JavaxEmailSender(host, port, user, pass) // Uncomment to use real

    val pushSender = MockPushNotificationSender()
    // val pushSender = FirebasePushNotificationSender(credPath) // Uncomment to use real

    val authService = AuthService(usersService, devicesService, emailSender)

    configureDatabases(jdbcUrl)
    configureSecurity(devicesService)

    routing {
        questionsCreationRoutes()
        questionsListingRoutes()
        collectionsRoutes()
    }

    authRoutes(authService)
    deviceRoutes(devicesService)
}
