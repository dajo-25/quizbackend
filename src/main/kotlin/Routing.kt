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
class EmptyParams : DTOParams()

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
        defineRoute(HttpMethod.Post, "/auth/login") { body: LoginRequestDTO, _: EmptyParams -> authService.Login(body) },
        defineRoute(HttpMethod.Post, "/auth/signup") { body: SignupRequestDTO, _: EmptyParams -> authService.Signup(body) },
        defineRoute(HttpMethod.Post, "/auth/logout", requiresAuth = true) { body: EmptyRequestDTO, _: EmptyParams -> authService.Logout(body) },
        defineRoute(HttpMethod.Delete, "/auth/account", requiresAuth = true) { body: EmptyRequestDTO, _: EmptyParams -> authService.DeleteAccount(body) },
        defineRoute(HttpMethod.Post, "/auth/recover") { body: RecoverPasswordRequestDTO, _: EmptyParams -> authService.RecoverPassword(body) },
        defineRoute(HttpMethod.Post, "/auth/verify") { body: VerifyEmailRequestDTO, _: EmptyParams -> authService.VerifyEmail(body) },
        defineRoute(HttpMethod.Get, "/auth/must-change-password", requiresAuth = true) { body: EmptyRequestDTO, _: EmptyParams -> authService.MustChangePassword(body) },
        defineRoute(HttpMethod.Post, "/auth/change-password", requiresAuth = true) { body: ChangePasswordRequestDTO, _: EmptyParams -> authService.ChangePassword(body) },
        defineRoute(HttpMethod.Get, "/auth/status", requiresAuth = true) { body: EmptyRequestDTO, _: EmptyParams -> authService.Status(body) },

        // QUESTIONS
        defineRoute(HttpMethod.Get, "/questions") { body: EmptyRequestDTO, params: SearchQuestionsParamsDTO -> questionsService.DiscoverQuestions(body, params) },
        defineRoute(HttpMethod.Post, "/questions", requiresAuth = true) { body: CreateQuestionsRequestDTO, _: EmptyParams -> questionsService.CreateQuestions(body) },
        defineRoute(HttpMethod.Get, "/questions/{id}") { body: EmptyRequestDTO, params: GetQuestionParamsDTO -> questionsService.GetQuestion(body, params) },
        defineRoute(HttpMethod.Put, "/questions/{id}", requiresAuth = true) { body: UpdateQuestionRequestDTO, params: UpdateQuestionParamsDTO -> questionsService.UpdateQuestion(body, params) },
        defineRoute(HttpMethod.Delete, "/questions/{id}", requiresAuth = true) { body: EmptyRequestDTO, params: DeleteQuestionParamsDTO -> questionsService.DeleteQuestion(body, params) },
        defineRoute(HttpMethod.Get, "/questions/batch") { body: EmptyRequestDTO, params: GetQuestionsBatchParamsDTO -> questionsService.GetQuestionsBatch(body, params) },

        // COLLECTIONS
        defineRoute(HttpMethod.Get, "/collections") { body: EmptyRequestDTO, _: EmptyParams -> collectionsService.GetCollectionsList(body) },
        defineRoute(HttpMethod.Post, "/collections", requiresAuth = true) { body: CreateCollectionRequestDTO, _: EmptyParams -> collectionsService.CreateCollection(body) },
        defineRoute(HttpMethod.Get, "/collections/{id}") { body: EmptyRequestDTO, params: UpdateCollectionParamsDTO -> collectionsService.GetCollection(body, params) },
        defineRoute(HttpMethod.Put, "/collections/{id}", requiresAuth = true) { body: UpdateCollectionRequestDTO, params: UpdateCollectionParamsDTO -> collectionsService.UpdateCollection(body, params) },
        defineRoute(HttpMethod.Delete, "/collections/{id}", requiresAuth = true) { body: EmptyRequestDTO, params: UpdateCollectionParamsDTO -> collectionsService.DeleteCollection(body, params) },

        // COMMUNITIES
        defineRoute(HttpMethod.Post, "/communities/friend-request", requiresAuth = true) { body: SendFriendRequestDTO, _: EmptyParams -> communitiesService.SendFriendRequest(body) },
        defineRoute(HttpMethod.Post, "/communities/friend-request/respond", requiresAuth = true) { body: EmptyRequestDTO, _: EmptyParams -> communitiesService.RespondFriendRequest(body) },
        defineRoute(HttpMethod.Get, "/communities/users", requiresAuth = true) { body: EmptyRequestDTO, _: EmptyParams -> communitiesService.SearchUsers(body) },

        // MARKS
        defineRoute(HttpMethod.Get, "/marks", requiresAuth = true) { body: EmptyRequestDTO, _: EmptyParams -> marksService.GetMarks(body) },

        // NOTIFICATIONS
        defineRoute(HttpMethod.Post, "/devices/push-token", requiresAuth = true) { body: RegisterPushTokenRequestDTO, _: EmptyParams -> notificationsService.RegisterPushToken(body) },
        defineRoute(HttpMethod.Delete, "/devices/push-token", requiresAuth = true) { body: EmptyRequestDTO, _: EmptyParams -> notificationsService.UnregisterPushToken(body) },

        // PROFILE
        defineRoute(HttpMethod.Get, "/profile", requiresAuth = true) { body: EmptyRequestDTO, _: EmptyParams -> profileService.SeeProfile(body) },
        defineRoute(HttpMethod.Put, "/profile", requiresAuth = true) { body: UpdateProfileRequestDTO, _: EmptyParams -> profileService.UpdateProfile(body) }
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
    if (type == EmptyParams::class) return EmptyParams() as Params

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
