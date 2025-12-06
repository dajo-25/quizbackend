package com.quizbackend

import com.myapp.features.auth.authRoutes
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
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.plugins.cors.routing.*
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
    install(CORS) {
        // 1. Mètodes permesos (GET, POST i HEAD venen per defecte)
        allowMethod(HttpMethod.Options)
        allowMethod(HttpMethod.Put)
        allowMethod(HttpMethod.Delete)

        // 2. Capçaleres permeses (si envies JSON o Tokens)
        allowHeader(HttpHeaders.ContentType)
        allowHeader(HttpHeaders.Authorization)

        // 3. Permetre credencials (Cookies / Auth headers)
        allowCredentials = true

        // 4. Host específic (Sense "http://", només domini i port)
        // scheme: llista els protocols (http, https)
        allowHost("localhost:5500", schemes = listOf("http", "https"))
        allowHost("sks0s0kg8cgw8kwg4skoocwg.reservarum.com", schemes = listOf("https"))
    }

    routing {
        //TODO routes
    }
}
