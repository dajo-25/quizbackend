package com.quizbackend.utils

object UserContext {
    private val threadLocalToken = ThreadLocal<String>()
    private val threadLocalUserId = ThreadLocal<Int>()

    fun setToken(token: String) {
        threadLocalToken.set(token)
    }

    fun getToken(): String? {
        return threadLocalToken.get()
    }

    fun setUserId(userId: Int) {
        threadLocalUserId.set(userId)
    }

    fun getUserId(): Int? {
        return threadLocalUserId.get()
    }

    fun clear() {
        threadLocalToken.remove()
        threadLocalUserId.remove()
    }
}
