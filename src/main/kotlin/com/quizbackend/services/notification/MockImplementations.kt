package com.quizbackend.services.notification

class MockEmailSender : EmailSender {
    override fun sendEmail(to: String, subject: String, body: String) {
        println("MOCK EMAIL SENT TO: $to")
        println("SUBJECT: $subject")
        println("BODY: $body")
    }
}

class MockPushNotificationSender : PushNotificationSender {
    override fun sendPush(token: String, title: String, body: String) {
        println("MOCK PUSH SENT TO: $token")
        println("TITLE: $title")
        println("BODY: $body")
    }
}
