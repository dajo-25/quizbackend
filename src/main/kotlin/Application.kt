package com.quizbackend

import com.quizbackend.contracts.features.auth.protectedAuthRoutes
import com.quizbackend.contracts.features.auth.publicAuthRoutes
import com.quizbackend.contracts.features.profile.profileRoutes
import com.quizbackend.contracts.features.collections.collectionsRoutes
import com.quizbackend.contracts.features.communities.communitiesRoutes
import com.quizbackend.contracts.features.marks.marksRoutes
import com.quizbackend.contracts.features.notifications.notificationsRoutes
import com.quizbackend.contracts.features.questions.questionsRoutes
import com.quizbackend.features.auth.AuthContractImpl
import com.quizbackend.features.auth.AuthDomainService
import com.quizbackend.features.communities.CommunitiesMockContractImpl
import com.quizbackend.features.devices.DevicesService
import com.quizbackend.features.devices.NotificationsContractImpl
import com.quizbackend.features.marks.MarksMockContractImpl
import com.quizbackend.features.quiz.collections.CollectionsContractImpl
import com.quizbackend.features.quiz.questions.QuestionsContractImpl
import com.quizbackend.features.users.ProfileContractImpl
import com.quizbackend.features.users.UsersService
import com.quizbackend.plugins.*
import com.quizbackend.services.notification.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
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

    val emailSender = MockEmailSender()
    val pushSender = MockPushNotificationSender()

    val authDomainService = AuthDomainService(usersService, devicesService, emailSender)
    val authContractImpl = AuthContractImpl(authDomainService)
    val profileContractImpl = ProfileContractImpl(usersService)
    val notificationsContractImpl = NotificationsContractImpl(devicesService)
    val questionsContractImpl = QuestionsContractImpl()
    val collectionsContractImpl = CollectionsContractImpl()
    val communitiesMockContractImpl = CommunitiesMockContractImpl()
    val marksMockContractImpl = MarksMockContractImpl()

    configureDatabases(jdbcUrl)
    configureSecurity(devicesService)

    routing {
        publicAuthRoutes(authContractImpl)
        authenticate("auth-bearer") {
            protectedAuthRoutes(authContractImpl)
            profileRoutes(profileContractImpl)
            notificationsRoutes(notificationsContractImpl)
            questionsRoutes(questionsContractImpl)
            collectionsRoutes(collectionsContractImpl)
            communitiesRoutes(communitiesMockContractImpl)
            marksRoutes(marksMockContractImpl)
        }
    }
}
