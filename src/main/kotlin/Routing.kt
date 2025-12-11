package com.quizbackend

import com.quizbackend.contracts.generated.*
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
import com.quizbackend.services.notification.MockEmailSender
import com.quizbackend.utils.UserContext
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.serialization.JsonConvertException
import io.ktor.server.plugins.BadRequestException
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.serialization.MissingFieldException
import kotlinx.serialization.SerializationException
import kotlin.reflect.KClass
import kotlin.reflect.full.primaryConstructor

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
    val devicesContractService = NotificationsContractImpl() // Renamed to avoid confusion with domain service and match Generated interface arg name
    val profileService = ProfileContractImpl()

    configureGeneratedRoutes(
        authService = authService,
        questionsService = questionsService,
        collectionsService = collectionsService,
        communitiesService = communitiesService,
        marksService = marksService,
        devicesService = devicesContractService,
        profileService = profileService
    )
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
                    call.respond(HttpStatusCode.OK, response, io.ktor.util.reflect.TypeInfo(def.returnType.classifier as KClass<*>, def.returnType))
                }
            } catch (e: Exception) {
                val errorDetails = when {
                    e is BadRequestException && e.cause is MissingFieldException -> {
                        val cause = e.cause as MissingFieldException
                        ErrorDetailsDTO(ErrorType.MISSING_DATA, "Missing field(s): ${cause.missingFields.joinToString(", ")}")
                    }
                    e is BadRequestException && e.cause is SerializationException -> {
                        ErrorDetailsDTO(ErrorType.MISSING_DATA, "Invalid format or type: ${e.cause?.message}")
                    }
                    e is BadRequestException && e.cause is JsonConvertException -> {
                        ErrorDetailsDTO(ErrorType.MISSING_DATA, "Invalid format or type: ${e.cause?.message}")
                    }
                    e is IllegalArgumentException -> {
                        // This catches our custom exceptions from parseParams
                        ErrorDetailsDTO(ErrorType.MISSING_DATA, e.message ?: "Invalid data")
                    }
                    else -> {
                        e.message?.let { ErrorDetailsDTO(ErrorType.INTERNAL_SERVER_ERROR, it) }
                    }
                }
                call.respond(HttpStatusCode.OK, DTOResponse<Unit>(false, null, "Error: ${errorDetails?.message}", errorDetails))
            }
        }
    }

    if (def.requiresAuth) {
        authenticate("auth-bearer") {
            route(def.path, def.method) {
                // Wrap handler to set UserContext and ensure cleanup
                intercept(ApplicationCallPipeline.Call) {
                    try {
                        val principal = call.principal<UserIdPrincipal>()
                        if (principal != null) {
                            UserContext.setUserId(principal.name.toInt())
                            val token = call.request.parseAuthorizationHeader()?.render()?.removePrefix("Bearer ")
                            if (token != null) {
                                UserContext.setToken(token)
                            }
                        }
                        proceed()
                    } finally {
                        UserContext.clear()
                    }
                }

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
        try {
            convertValue(value, param.type.classifier as KClass<*>)
        } catch (e: NumberFormatException) {
            throw IllegalArgumentException("Parameter '${param.name}' must be a number", e)
        } catch (e: Exception) {
            throw IllegalArgumentException("Parameter '${param.name}' is invalid: ${e.message}", e)
        }
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
