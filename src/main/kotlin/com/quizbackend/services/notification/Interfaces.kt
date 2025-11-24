package com.quizbackend.services.notification

interface EmailSender {
    fun sendEmail(to: String, subject: String, body: String)
}

interface PushNotificationSender {
    fun sendPush(token: String, title: String, body: String)
}
