package com.quizbackend.services.notification

import java.util.Properties
import javax.mail.Authenticator
import javax.mail.Message
import javax.mail.PasswordAuthentication
import javax.mail.Session
import javax.mail.Transport
import javax.mail.internet.InternetAddress
import javax.mail.internet.MimeMessage

class JavaxEmailSender(
    private val host: String,
    private val port: Int,
    private val user: String,
    private val pass: String
) : EmailSender {
    override fun sendEmail(to: String, subject: String, body: String) {
        val props = Properties()
        props["mail.smtp.auth"] = "true"
        props["mail.smtp.starttls.enable"] = "true"
        props["mail.smtp.host"] = host
        props["mail.smtp.port"] = port

        val session = Session.getInstance(props, object : Authenticator() {
            override fun getPasswordAuthentication(): PasswordAuthentication {
                return PasswordAuthentication(user, pass)
            }
        })

        val message = MimeMessage(session)
        message.setFrom(InternetAddress(user))
        message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(to))
        message.subject = subject
        message.setText(body)

        Transport.send(message)
    }
}
