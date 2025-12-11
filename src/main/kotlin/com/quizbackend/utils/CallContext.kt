package com.quizbackend.utils

import io.ktor.server.application.*
import kotlin.coroutines.AbstractCoroutineContextElement
import kotlin.coroutines.CoroutineContext

class CallContext(val call: ApplicationCall) : AbstractCoroutineContextElement(Key) {
    companion object Key : CoroutineContext.Key<CallContext>
}
