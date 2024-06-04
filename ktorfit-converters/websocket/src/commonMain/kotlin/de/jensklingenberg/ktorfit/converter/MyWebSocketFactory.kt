package de.jensklingenberg.ktorfit.converter

import de.jensklingenberg.ktorfit.Ktorfit
import de.jensklingenberg.ktorfit.WebSocket
import de.jensklingenberg.ktorfit.WebSocketImpl
import io.ktor.client.request.*

public class MyWebSocketFactory : Converter.Factory {
    override fun webSocketClass(typeData: TypeData, ktorfit: Ktorfit): Converter.WebSocketProvider {
        return object : Converter.WebSocketProvider {
            override fun createWebSocket(requestBuilder: HttpRequestBuilder.() -> Unit): WebSocket {
                return WebSocketImpl(ktorfit.httpClient, requestBuilder)
            }
        }
    }
}