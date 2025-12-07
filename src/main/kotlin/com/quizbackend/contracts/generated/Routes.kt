package com.quizbackend.contracts.generated

import io.ktor.server.application.*
import io.ktor.server.routing.*
import io.ktor.http.HttpMethod
import com.quizbackend.routing.defineRoute
import com.quizbackend.routing.RouteDefinition
import com.quizbackend.configureRoute

fun Application.configureGeneratedRoutes(authService: AuthService, questionsService: QuestionsService, collectionsService: CollectionsService, communitiesService: CommunitiesService, marksService: MarksService, devicesService: DevicesService, profileService: ProfileService) {
    val routes = listOf(
        defineRoute<LoginRequestDTO, EmptyParamsDTO, LoginResponseDTO>(HttpMethod.Post, "/auth/login", requiresAuth = false) { body, params -> authService.PostLogin(body, params) },
        defineRoute<SignupRequestDTO, EmptyParamsDTO, GenericResponseDTO>(HttpMethod.Post, "/auth/signup", requiresAuth = false) { body, params -> authService.PostSignup(body, params) },
        defineRoute<EmptyRequestDTO, EmptyParamsDTO, GenericResponseDTO>(HttpMethod.Post, "/auth/logout", requiresAuth = true) { body, params -> authService.PostLogout(body, params) },
        defineRoute<EmptyRequestDTO, EmptyParamsDTO, MessageResponseDTO>(HttpMethod.Delete, "/auth/account", requiresAuth = true) { body, params -> authService.DeleteAccount(body, params) },
        defineRoute<RecoverPasswordRequestDTO, EmptyParamsDTO, MessageResponseDTO>(HttpMethod.Post, "/auth/recover", requiresAuth = false) { body, params -> authService.PostRecover(body, params) },
        defineRoute<VerifyEmailRequestDTO, EmptyParamsDTO, GenericResponseDTO>(HttpMethod.Post, "/auth/verify", requiresAuth = false) { body, params -> authService.PostVerify(body, params) },
        defineRoute<EmptyRequestDTO, EmptyParamsDTO, MustChangePasswordResponseDTO>(HttpMethod.Get, "/auth/must-change-password", requiresAuth = true) { body, params -> authService.GetMustChangePassword(body, params) },
        defineRoute<ChangePasswordRequestDTO, EmptyParamsDTO, GenericResponseDTO>(HttpMethod.Post, "/auth/change-password", requiresAuth = true) { body, params -> authService.PostChangePassword(body, params) },
        defineRoute<EmptyRequestDTO, EmptyParamsDTO, UserStatusResponseDTO>(HttpMethod.Get, "/auth/status", requiresAuth = true) { body, params -> authService.GetStatus(body, params) },
        defineRoute<EmptyRequestDTO, SearchQuestionsParamsDTO, QuestionListResponseDTO>(HttpMethod.Get, "/questions", requiresAuth = false) { body, params -> questionsService.GetQuestions(body, params) },
        defineRoute<CreateQuestionsRequestDTO, EmptyParamsDTO, GenericResponseDTO>(HttpMethod.Post, "/questions", requiresAuth = true) { body, params -> questionsService.PostQuestions(body, params) },
        defineRoute<EmptyRequestDTO, GetQuestionParamsDTO, QuestionDataResponseDTO>(HttpMethod.Get, "/questions/{id}", requiresAuth = false) { body, params -> questionsService.GetQuestionsId(body, params) },
        defineRoute<UpdateQuestionRequestDTO, UpdateQuestionParamsDTO, QuestionDataResponseDTO>(HttpMethod.Put, "/questions/{id}", requiresAuth = true) { body, params -> questionsService.PutQuestionsId(body, params) },
        defineRoute<EmptyRequestDTO, DeleteQuestionParamsDTO, GenericResponseDTO>(HttpMethod.Delete, "/questions/{id}", requiresAuth = true) { body, params -> questionsService.DeleteQuestionsId(body, params) },
        defineRoute<EmptyRequestDTO, GetQuestionsBatchParamsDTO, QuestionListResponseDTO>(HttpMethod.Get, "/questions/batch", requiresAuth = false) { body, params -> questionsService.GetBatch(body, params) },
        defineRoute<EmptyRequestDTO, EmptyParamsDTO, CollectionListResponseDTO>(HttpMethod.Get, "/collections", requiresAuth = false) { body, params -> collectionsService.GetCollections(body, params) },
        defineRoute<CreateCollectionRequestDTO, EmptyParamsDTO, IdDataResponseDTO>(HttpMethod.Post, "/collections", requiresAuth = true) { body, params -> collectionsService.PostCollections(body, params) },
        defineRoute<EmptyRequestDTO, UpdateCollectionParamsDTO, CollectionDetailResponseDTO>(HttpMethod.Get, "/collections/{id}", requiresAuth = false) { body, params -> collectionsService.GetCollectionsId(body, params) },
        defineRoute<UpdateCollectionRequestDTO, UpdateCollectionParamsDTO, GenericResponseDTO>(HttpMethod.Put, "/collections/{id}", requiresAuth = true) { body, params -> collectionsService.PutCollectionsId(body, params) },
        defineRoute<EmptyRequestDTO, UpdateCollectionParamsDTO, GenericResponseDTO>(HttpMethod.Delete, "/collections/{id}", requiresAuth = true) { body, params -> collectionsService.DeleteCollectionsId(body, params) },
        defineRoute<SendFriendRequestDTO, EmptyParamsDTO, FriendRequestResponseDTO>(HttpMethod.Post, "/communities/friend-request", requiresAuth = true) { body, params -> communitiesService.PostFriendRequest(body, params) },
        defineRoute<EmptyRequestDTO, EmptyParamsDTO, GenericResponseDTO>(HttpMethod.Post, "/communities/friend-request/respond", requiresAuth = true) { body, params -> communitiesService.PostRespond(body, params) },
        defineRoute<EmptyRequestDTO, EmptyParamsDTO, UserListResponseDTO>(HttpMethod.Get, "/communities/users", requiresAuth = true) { body, params -> communitiesService.GetUsers(body, params) },
        defineRoute<EmptyRequestDTO, EmptyParamsDTO, MarkListResponseDTO>(HttpMethod.Get, "/marks", requiresAuth = true) { body, params -> marksService.GetMarks(body, params) },
        defineRoute<RegisterPushTokenRequestDTO, EmptyParamsDTO, GenericResponseDTO>(HttpMethod.Post, "/devices/push-token", requiresAuth = true) { body, params -> devicesService.PostPushToken(body, params) },
        defineRoute<EmptyRequestDTO, EmptyParamsDTO, GenericResponseDTO>(HttpMethod.Delete, "/devices/push-token", requiresAuth = true) { body, params -> devicesService.DeletePushToken(body, params) },
        defineRoute<EmptyRequestDTO, EmptyParamsDTO, ProfileDataResponseDTO>(HttpMethod.Get, "/profile", requiresAuth = true) { body, params -> profileService.GetProfile(body, params) },
        defineRoute<UpdateProfileRequestDTO, EmptyParamsDTO, ProfileDataResponseDTO>(HttpMethod.Put, "/profile", requiresAuth = true) { body, params -> profileService.PutProfile(body, params) },
    )

    routing {
        routes.forEach { def ->
            configureRoute(def)
        }
    }
}
