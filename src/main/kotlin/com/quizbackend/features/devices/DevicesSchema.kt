package com.quizbackend.features.devices

import com.quizbackend.features.users.Users
import org.jetbrains.exposed.dao.id.IntIdTable

object Devices : IntIdTable("devices") {
    val user = reference("user_id", Users)
    val uniqueId = varchar("unique_id", 255)
    val pushToken = varchar("push_token", 512).nullable()
    val accessToken = varchar("access_token", 512).nullable()
    val enabled = bool("enabled").default(true)
}
