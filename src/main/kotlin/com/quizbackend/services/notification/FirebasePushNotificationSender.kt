package com.quizbackend.services.notification

import com.google.auth.oauth2.GoogleCredentials
import com.google.firebase.FirebaseApp
import com.google.firebase.FirebaseOptions
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.messaging.Message
import com.google.firebase.messaging.Notification
import java.io.FileInputStream

class FirebasePushNotificationSender(credentialsPath: String) : PushNotificationSender {
    init {
        // Only initialize if not already initialized
        if (FirebaseApp.getApps().isEmpty()) {
            val serviceAccount = FileInputStream(credentialsPath)
            val options = FirebaseOptions.builder()
                .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                .build()
            FirebaseApp.initializeApp(options)
        }
    }

    override fun sendPush(token: String, title: String, body: String) {
        val message = Message.builder()
            .setToken(token)
            .setNotification(
                Notification.builder()
                    .setTitle(title)
                    .setBody(body)
                    .build()
            )
            .build()

        FirebaseMessaging.getInstance().send(message)
    }
}
