package com.quizbackend.features.auth

object AuthService {
    private const val VALID_TOKEN = "super-secret-token"

    fun validateToken(token: String): Boolean {
        return token == VALID_TOKEN
    }
}
