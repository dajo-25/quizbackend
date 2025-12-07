package com.quizbackend

import com.quizbackend.contracts.common.base.*
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
import com.quizbackend.routing.RouteDefinition
import com.quizbackend.routing.defineRoute
import com.quizbackend.services.notification.MockEmailSender
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.serialization.Serializable
import kotlin.reflect.KClass
import kotlin.reflect.full.primaryConstructor
import kotlin.reflect.KParameter

// Helper DTO for empty params
@Serializable
class EmptyParamsDTO : DTOParams()

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

    val routes = listOf(
        // AUTH
        defineRoute<LoginRequestDTO, EmptyParamsDTO, LoginResponseDTO>(HttpMethod.Post, "/auth/login") { body, _ -> authService.Login(body) },
        defineRoute<SignupRequestDTO, EmptyParamsDTO, GenericResponseDTO>(HttpMethod.Post, "/auth/signup") { body, _ -> authService.Signup(body) },
        defineRoute<EmptyRequestDTO, EmptyParamsDTO, GenericResponseDTO>(HttpMethod.Post, "/auth/logout", requiresAuth = true) { body, _ -> authService.Logout(body) },
        defineRoute<EmptyRequestDTO, EmptyParamsDTO, MessageResponseDTO>(HttpMethod.Delete, "/auth/account", requiresAuth = true) { body, _ -> authService.DeleteAccount(body) },
        defineRoute<RecoverPasswordRequestDTO, EmptyParamsDTO, MessageResponseDTO>(HttpMethod.Post, "/auth/recover") { body, _ -> authService.RecoverPassword(body) },
        defineRoute<VerifyEmailRequestDTO, EmptyParamsDTO, GenericResponseDTO>(HttpMethod.Post, "/auth/verify") { body, _ -> authService.VerifyEmail(body) },
        defineRoute<EmptyRequestDTO, EmptyParamsDTO, MustChangePasswordResponseDTO>(HttpMethod.Get, "/auth/must-change-password", requiresAuth = true) { body, _ -> authService.MustChangePassword(body) },
        defineRoute<ChangePasswordRequestDTO, EmptyParamsDTO, GenericResponseDTO>(HttpMethod.Post, "/auth/change-password", requiresAuth = true) { body, _ -> authService.ChangePassword(body) },
        defineRoute<EmptyRequestDTO, EmptyParamsDTO, UserStatusResponseDTO>(HttpMethod.Get, "/auth/status", requiresAuth = true) { body, _ -> authService.Status(body) },

        // QUESTIONS
        defineRoute<EmptyRequestDTO, SearchQuestionsParamsDTO, QuestionListResponseDTO>(HttpMethod.Get, "/questions") { body, params -> questionsService.DiscoverQuestions(body, params) },
        defineRoute<CreateQuestionsRequestDTO, EmptyParamsDTO, GenericResponseDTO>(HttpMethod.Post, "/questions", requiresAuth = true) { body, _ -> questionsService.CreateQuestions(body) },
        defineRoute<EmptyRequestDTO, GetQuestionParamsDTO, QuestionDataResponseDTO>(HttpMethod.Get, "/questions/{id}") { body, params -> questionsService.GetQuestion(body, params) },
        defineRoute<UpdateQuestionRequestDTO, UpdateQuestionParamsDTO, QuestionDataResponseDTO>(HttpMethod.Put, "/questions/{id}", requiresAuth = true) { body, params -> questionsService.UpdateQuestion(body, params) },
        defineRoute<EmptyRequestDTO, DeleteQuestionParamsDTO, GenericResponseDTO>(HttpMethod.Delete, "/questions/{id}", requiresAuth = true) { body, params -> questionsService.DeleteQuestion(body, params) },
        defineRoute<EmptyRequestDTO, GetQuestionsBatchParamsDTO, QuestionListResponseDTO>(HttpMethod.Get, "/questions/batch") { body, params -> questionsService.GetQuestionsBatch(body, params) },

        // COLLECTIONS
        defineRoute<EmptyRequestDTO, EmptyParamsDTO, CollectionListResponseDTO>(HttpMethod.Get, "/collections") { body, _ -> collectionsService.GetCollectionsList(body) },
        defineRoute<CreateCollectionRequestDTO, EmptyParamsDTO, IdDataResponseDTO>(HttpMethod.Post, "/collections", requiresAuth = true) { body, _ -> collectionsService.CreateCollection(body) },
        defineRoute<EmptyRequestDTO, UpdateCollectionParamsDTO, CollectionDetailResponseDTO>(HttpMethod.Get, "/collections/{id}") { body, params -> collectionsService.GetCollection(body, params) },
        defineRoute<UpdateCollectionRequestDTO, UpdateCollectionParamsDTO, GenericResponseDTO>(HttpMethod.Put, "/collections/{id}", requiresAuth = true) { body, params -> collectionsService.UpdateCollection(body, params) },
        defineRoute<EmptyRequestDTO, UpdateCollectionParamsDTO, GenericResponseDTO>(HttpMethod.Delete, "/collections/{id}", requiresAuth = true) { body, params -> collectionsService.DeleteCollection(body, params) },

        // COMMUNITIES
        defineRoute<SendFriendRequestDTO, EmptyParamsDTO, FriendRequestResponseDTO>(HttpMethod.Post, "/communities/friend-request", requiresAuth = true) { body, _ -> communitiesService.SendFriendRequest(body) },
        defineRoute<EmptyRequestDTO, EmptyParamsDTO, GenericResponseDTO>(HttpMethod.Post, "/communities/friend-request/respond", requiresAuth = true) { body, _ -> communitiesService.RespondFriendRequest(body) },
        defineRoute<EmptyRequestDTO, EmptyParamsDTO, UserListResponseDTO>(HttpMethod.Get, "/communities/users", requiresAuth = true) { body, _ -> communitiesService.SearchUsers(body) },

        // MARKS
        defineRoute<EmptyRequestDTO, EmptyParamsDTO, MarkListResponseDTO>(HttpMethod.Get, "/marks", requiresAuth = true) { body, _ -> marksService.GetMarks(body) },

        // NOTIFICATIONS
        defineRoute<RegisterPushTokenRequestDTO, EmptyParamsDTO, GenericResponseDTO>(HttpMethod.Post, "/devices/push-token", requiresAuth = true) { body, _ -> notificationsService.RegisterPushToken(body) },
        defineRoute<EmptyRequestDTO, EmptyParamsDTO, GenericResponseDTO>(HttpMethod.Delete, "/devices/push-token", requiresAuth = true) { body, _ -> notificationsService.UnregisterPushToken(body) },

        // PROFILE
        defineRoute<EmptyRequestDTO, EmptyParamsDTO, ProfileDataResponseDTO>(HttpMethod.Get, "/profile", requiresAuth = true) { body, _ -> profileService.SeeProfile(body) },
        defineRoute<UpdateProfileRequestDTO, EmptyParamsDTO, ProfileDataResponseDTO>(HttpMethod.Put, "/profile", requiresAuth = true) { body, _ -> profileService.UpdateProfile(body) }
    )

    routing {
        routes.forEach { def ->
            configureRoute(def)
        }
    }
}

fun <Body : Any, Params : DTOParams, Response : Any> Route.configureRoute(
    def: RouteDefinition<Body, Params, Response>
) {
    val routeBlock: Route.() -> Unit = {
        handle {
            try {
                // Parse Body
                val body: Body = if (def.bodyType != EmptyRequestDTO::class) {
                     // If bodyType is EmptyRequestDTO, instantiate it directly to avoid parsing body if not needed or empty
                    if (call.request.httpMethod == HttpMethod.Get || call.request.httpMethod == HttpMethod.Delete) {
                         // GET/DELETE usually don't have body, but if defined as EmptyRequestDTO, provide instance
                         EmptyRequestDTO() as Body
                    } else {
                         call.receive(def.bodyType)
                    }
                } else {
                    EmptyRequestDTO() as Body
                }

                // Parse Params
                val params: Params = parseParams(call, def.paramsType)

                // Execute
                val response = def.handler(body, params)

                // Respond
                if (response.success) {
                    call.respond(HttpStatusCode.OK, response, io.ktor.util.reflect.TypeInfo(def.returnType.classifier as KClass<*>, def.returnType))
                } else {
                    call.respond(HttpStatusCode.BadRequest, response, io.ktor.util.reflect.TypeInfo(def.returnType.classifier as KClass<*>, def.returnType))
                }
            } catch (e: Exception) {
                call.respond(HttpStatusCode.BadRequest, DTOResponse<Unit>(false, null, "Error: ${e.localizedMessage}", ErrorDetailsDTO(ErrorType.INTERNAL_SERVER_ERROR, e.message)))
            }
        }
    }

    if (def.requiresAuth) {
        authenticate("auth-bearer") {
            route(def.path, def.method) {
                routeBlock()
            }
        }
    } else {
        route(def.path, def.method) {
            routeBlock()
        }
    }
}

fun <Params : DTOParams> parseParams(call: ApplicationCall, type: KClass<Params>): Params {
    if (type == EmptyParamsDTO::class) return EmptyParamsDTO() as Params

    // Simple reflection mapper
    val constructor = type.primaryConstructor ?: throw IllegalArgumentException("No primary constructor for ${type.simpleName}")
    val args = constructor.parameters.associateWith { param ->
        val value = call.parameters[param.name!!]
        // Convert value to type
        convertValue(value, param.type.classifier as KClass<*>)
    }
    return constructor.callBy(args)
}

fun convertValue(value: String?, type: KClass<*>): Any? {
    if (value == null) return null
    return when (type) {
        Int::class -> value.toInt()
        String::class -> value
        Boolean::class -> value.toBoolean()
        Long::class -> value.toLong()
        // Add support for Lists (comma separated)
        List::class -> {
             // We assume List<Int> for now as used in GetQuestionsBatchParamsDTO
             // Ideally we check type arguments
             value.split(",").map { it.toInt() }
        }
        else -> value
    }
}
