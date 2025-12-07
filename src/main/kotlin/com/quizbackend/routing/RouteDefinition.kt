package com.quizbackend.routing

import io.ktor.http.HttpMethod
import com.quizbackend.contracts.generated.DTOResponse
import com.quizbackend.contracts.generated.DTOParams
import kotlin.reflect.KClass
import kotlin.reflect.KType
import kotlin.reflect.typeOf

data class RouteDefinition<Body : Any, Params : DTOParams, Response : Any>(
    val method: HttpMethod,
    val path: String,
    val bodyType: KClass<Body>,
    val paramsType: KClass<Params>,
    val responseType: KClass<Response>,
    val returnType: KType,
    val requiresAuth: Boolean = false,
    val handler: suspend (Body, Params) -> DTOResponse<Response>
)

// Helper to create definitions easily with inferred types
inline fun <reified Body : Any, reified Params : DTOParams, reified Response : Any> defineRoute(
    method: HttpMethod,
    path: String,
    requiresAuth: Boolean = false,
    noinline handler: suspend (Body, Params) -> DTOResponse<Response>
): RouteDefinition<Body, Params, Response> {
    return RouteDefinition(
        method,
        path,
        Body::class,
        Params::class,
        Response::class,
        typeOf<DTOResponse<Response>>(),
        requiresAuth,
        handler
    )
}
