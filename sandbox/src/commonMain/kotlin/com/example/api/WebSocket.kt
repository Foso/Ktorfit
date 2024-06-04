package com.example.api

import de.jensklingenberg.ktorfit.Ktorfit
import de.jensklingenberg.ktorfit.converter.Converter
import de.jensklingenberg.ktorfit.converter.TypeData
import io.ktor.client.request.*
import kotlinx.coroutines.flow.Flow

public interface WebSocket {
    public val events: Flow<Event>
    public suspend fun send(message: String)
    public fun close()
    public fun isOpen(): Boolean
    public suspend fun open()
}


public sealed interface Event {
    public data class Message(val message: String) : Event
    public data class Close(val message: String = "", val exception: Exception? = null) : Event
    public data object Opened : Event
    public data object Created : Event
    public data class Error(val message: Exception) : Event
}

class MyWebSocketFactory : Converter.Factory {
    override fun webSocketClass(typeData: TypeData, ktorfit: Ktorfit): Converter.WebSocketProvider {
        return object : Converter.WebSocketProvider {
            override fun createWebSocket(requestBuilder: HttpRequestBuilder.() -> Unit): WebSocket {
                return WebSocketImpl(ktorfit.httpClient,requestBuilder)
            }
        }
    }
}