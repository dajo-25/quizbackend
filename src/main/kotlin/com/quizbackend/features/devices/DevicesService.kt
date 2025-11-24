package com.quizbackend.features.devices

import com.quizbackend.features.devices.Devices
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction

class DevicesService {

    fun registerOrUpdate(userId: Int, uniqueId: String, accessToken: String, pushToken: String? = null) {
        transaction {
            val existing = Devices.selectAll().where { (Devices.uniqueId eq uniqueId) and (Devices.user eq userId) }.singleOrNull()

            if (existing != null) {
                Devices.update({ Devices.id eq existing[Devices.id] }) {
                    it[Devices.accessToken] = accessToken
                    it[Devices.enabled] = true
                    // Only update push token if provided, or maybe clear it?
                    // Requirement: "maybe there could be an endpoint that "checks" if the current device has a push token assigned"
                    // Requirement: "logging out only disables the current device"
                    // Requirement: "when logging off it can be erased to make sure that the next log in is refreshed"
                    if (pushToken != null) {
                        it[Devices.pushToken] = pushToken
                    }
                }
            } else {
                Devices.insert {
                    it[Devices.user] = userId
                    it[Devices.uniqueId] = uniqueId
                    it[Devices.accessToken] = accessToken
                    it[Devices.pushToken] = pushToken
                    it[Devices.enabled] = true
                }
            }
        }
    }

    fun disableDevice(accessToken: String) {
        transaction {
            Devices.update({ Devices.accessToken eq accessToken }) {
                it[enabled] = false
                it[pushToken] = null // "erased to make sure that the next log in is refreshed"
            }
        }
    }

    fun findByToken(token: String): ResultRow? {
        return transaction {
            Devices.selectAll().where { Devices.accessToken eq token }.singleOrNull()
        }
    }

    fun getPushTokenStatus(accessToken: String): Boolean {
        return transaction {
            val device = Devices.selectAll().where { Devices.accessToken eq accessToken }.singleOrNull()
            device?.get(Devices.pushToken) != null
        }
    }

    fun updatePushToken(accessToken: String, pushToken: String) {
        transaction {
            Devices.update({ Devices.accessToken eq accessToken }) {
                it[Devices.pushToken] = pushToken
            }
        }
    }
}
