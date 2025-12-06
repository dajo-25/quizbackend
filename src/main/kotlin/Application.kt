package com.quizbackend

import com.quizbackend.plugins.*
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.plugins.cors.routing.*

fun main(args: Array<String>) {
    io.ktor.server.netty.EngineMain.main(args)
}

fun Application.module() {
    configureSerialization()
    val jdbcUrl = System.getenv("STORAGE_JDBCURL")
        ?: environment.config.propertyOrNull("storage.jdbcUrl")?.getString()
        ?: "jdbc:sqlite:quiz.db"

    configureDatabases(jdbcUrl)
    // Security and Routing are now configured in separate functions/extensions
    // which are likely called from here or I should call them.
    // Wait, Routing.kt has configureRouting().
    // I should call configureRouting().
    // I need to check how to configure Security. `configureSecurity(devicesService)`.
    // I need to instantiate devicesService here to pass it to security?
    // Or I can move security config to Routing or similar?
    // The previous code had `configureSecurity(devicesService)`.

    // I will replicate the necessary setup but using the new Routing.kt logic if possible.
    // Actually, Routing.kt defined `configureRouting()`.
    // I should call it.

    // But `configureSecurity` depends on `devicesService`.
    // And `configureRouting` (my version) instantiates its own services.
    // This creates double instantiation.
    // I should probably move dependency injection to `module` and pass instances to `configureRouting`.

    // Let's modify `Routing.kt` to accept instances or `Application.configureRouting` should use what's available?
    // `Routing.kt` is an extension on `Application`.

    // Simplest: Instantiate common services here, pass to configureSecurity.
    // Then call configureRouting().
    // NOTE: `configureRouting` in my overwritten file creates its own instances. This is fine for now (stateless or DB-backed services).
    // `DevicesService` is DB backed, so separate instances are fine.

    // I just need to import DevicesService for configureSecurity.

    install(CORS) {
        allowMethod(HttpMethod.Options)
        allowMethod(HttpMethod.Put)
        allowMethod(HttpMethod.Delete)
        allowHeader(HttpHeaders.ContentType)
        allowHeader(HttpHeaders.Authorization)
        allowCredentials = true
        allowHost("localhost:5500", schemes = listOf("http", "https"))
        allowHost("sks0s0kg8cgw8kwg4skoocwg.reservarum.com", schemes = listOf("https"))
    }

    // Create service for Security
    val devicesService = com.quizbackend.features.devices.DevicesService()
    configureSecurity(devicesService)

    configureRouting()
}
