package com.quizbackend.features.quiz.collections

import com.quizbackend.utils.CallContext
import io.ktor.server.auth.*
import kotlin.coroutines.coroutineContext

interface UserProvider {
    suspend fun getUserId(): Int?
}

class DefaultUserProvider : UserProvider {
    override suspend fun getUserId(): Int? {
        val call = coroutineContext[CallContext.Key]?.call
        return call?.principal<UserIdPrincipal>()?.name?.toIntOrNull()
    }
}
