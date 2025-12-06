package com.quizbackend

import com.quizbackend.contracts.features.auth.authRoutes
import com.quizbackend.contracts.features.collections.collectionsRoutes
import com.quizbackend.contracts.features.communities.communitiesRoutes
import com.quizbackend.contracts.features.marks.marksRoutes
import com.quizbackend.contracts.features.notifications.notificationsRoutes
import com.quizbackend.contracts.features.profile.profileRoutes
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
import com.quizbackend.services.notification.MockEmailSender
import io.ktor.server.application.*
import io.ktor.server.routing.*

fun Application.configureRouting() {
    // Services dependencies
    val usersService = UsersService()
    val devicesService = DevicesService()
    val emailSender = MockEmailSender()
    val authDomainService = AuthDomainService(usersService, devicesService, emailSender)

    // Contract Implementations
    val authService = AuthContractImpl(authDomainService)
    val questionsService = QuestionsContractImpl()
    val collectionsService = CollectionsContractImpl()
    val communitiesService = CommunitiesMockContractImpl()
    val marksService = MarksMockContractImpl()
    val notificationsService = NotificationsContractImpl()
    val profileService = ProfileContractImpl()

    routing {
        authRoutes(authService)
        questionsRoutes(questionsService)
        collectionsRoutes(collectionsService)
        communitiesRoutes(communitiesService)
        marksRoutes(marksService)
        notificationsRoutes(notificationsService)
        profileRoutes(profileService)
    }
}
