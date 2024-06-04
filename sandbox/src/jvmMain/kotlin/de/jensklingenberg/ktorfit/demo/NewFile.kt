package de.jensklingenberg.ktorfit.demo

import io.ktor.client.*
import io.ktor.client.plugins.websocket.*

val jvmClient2 = HttpClient {
    install(WebSockets)

}

