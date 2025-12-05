package com.quizbackend.features.auth

import com.quizbackend.features.devices.DevicesService
import com.quizbackend.features.users.Users
import com.quizbackend.features.users.UsersService
import com.quizbackend.services.notification.EmailSender
import com.quizbackend.utils.CaesarCipher
import java.security.MessageDigest
import java.time.LocalDate
import java.util.UUID
import kotlin.random.Random

class AuthDomainService(
    private val usersService: UsersService,
    private val devicesService: DevicesService,
    private val emailSender: EmailSender
) {

    fun signup(email: String, username: String, name: String, surname: String, passwordHash: String): Boolean {
        if (usersService.findByEmail(email) != null) return false

        usersService.create(email, username, name, surname, passwordHash)

        // Send verification email
        sendVerificationEmail(email)

        return true
    }

    fun login(email: String, passwordHash: String, uniqueId: String): String? {
        val user = usersService.findByEmail(email) ?: return null

        if (user[Users.passwordHash] != passwordHash) return null

        // Generate handmade token
        val accessToken = generateHandmadeToken(email)

        // Register/Update device
        devicesService.registerOrUpdate(user[Users.id].value, uniqueId, accessToken)

        return accessToken
    }

    fun logout(token: String) {
        devicesService.disableDevice(token)
    }

    fun recoverPassword(email: String) {
        val user = usersService.findByEmail(email) ?: return

        val tempPassword = generateRandomPassword()
        val tempHash = sha512(tempPassword)

        usersService.updatePassword(user[Users.id].value, tempHash, mustChange = true)

        emailSender.sendEmail(
            email,
            "Password Recovery",
            "Your temporary password is: $tempPassword\nPlease log in and change it immediately."
        )
    }

    fun changePassword(userId: Int, oldHash: String, newHash: String): Boolean {
        val user = usersService.findById(userId) ?: return false

        if (user[Users.passwordHash] != oldHash) return false

        usersService.updatePassword(userId, newHash, mustChange = false)
        return true
    }

    fun verifyEmail(code: String): Boolean {
        try {
            val email = CaesarCipher.decryptFromUrlSafe(code)
            if (usersService.findByEmail(email) == null) return false

            usersService.markVerified(email)
            return true
        } catch (e: Exception) {
            return false
        }
    }

    fun getUser(userId: Int) = usersService.findById(userId)

    private fun sendVerificationEmail(email: String) {
        val code = CaesarCipher.encryptToUrlSafe(email)
        // Assuming a frontend URL or just the code for the task context
        val verifyUrl = "http://localhost:8080/auth/verify-page?code=$code" // Placeholder URL

        emailSender.sendEmail(
            email,
            "Verify your email",
            "Please click the link to verify your email: $verifyUrl\nOr use this code: $code"
        )
    }

    private fun generateHandmadeToken(email: String): String {
        val md = MessageDigest.getInstance("SHA-512")
        val digest = md.digest("$email${LocalDate.now()}${UUID.randomUUID()}".toByteArray())
        val sb = StringBuilder()
        for (i in digest.indices) {
            sb.append(Integer.toString((digest[i].toInt() and 0xff) + 0x100, 16).substring(1))
        }
        return sb.toString()
    }

    private fun sha512(input: String): String {
        val md = MessageDigest.getInstance("SHA-512")
        val digest = md.digest(input.toByteArray())
        val sb = StringBuilder()
        for (i in digest.indices) {
            sb.append(Integer.toString((digest[i].toInt() and 0xff) + 0x100, 16).substring(1))
        }
        return sb.toString()
    }

    private fun generateRandomPassword(): String {
        val chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789"
        return (1..8).map { chars.random() }.joinToString("")
    }
}
