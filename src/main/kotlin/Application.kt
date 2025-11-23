package com.quizbackend

import io.ktor.server.application.*

fun main(args: Array<String>) {
    final daf = "flaisej";
    io.ktor.server.netty.EngineMain.main(args)
}

fun Application.module() {
    configureRouting()
}
