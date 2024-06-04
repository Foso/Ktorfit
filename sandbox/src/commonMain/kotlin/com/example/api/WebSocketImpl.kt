package com.example.api

import io.ktor.client.*
import io.ktor.client.plugins.websocket.*
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.websocket.*
import kotlinx.coroutines.channels.ClosedReceiveChannelException
import kotlinx.coroutines.flow.MutableStateFlow

public class WebSocketImpl(private val client: HttpClient, val requestBuilder: HttpRequestBuilder.() -> Unit) : WebSocket {
    private val mutableEvents: MutableStateFlow<Event> = MutableStateFlow(Event.Created)
    override val events: MutableStateFlow<Event> = mutableEvents
    private var open = false
    private var channel: DefaultWebSocketSession? = null

    override suspend fun send(message: String) {
        channel?.send(Frame.Text(message))
    }

    override fun close() {
        open = false
        events.value = Event.Close("Closed")
    }

    override fun isOpen(): Boolean {
        return open
    }

    private fun onMessage(message: String) {
        mutableEvents.value = Event.Message(message)
    }

    override suspend fun open() {
        open = true
        try {

            client.webSocket(
                request = {
                    requestBuilder()
                }
            ) {
                events.value = Event.Opened
                while (isOpen()) {

                    channel = this
                    val frame = try {
                        incoming.receive()
                    } catch (closeR: ClosedReceiveChannelException) {
                        close()
                        mutableEvents.value = Event.Close(exception = closeR)
                        break
                    }
                    if (frame is Frame.Text) {
                        onMessage(frame.readText())
                    }
                    if (frame is Frame.Binary) {
                        onMessage(frame.readBytes())
                    }
                    if (frame is Frame.Close) {
                        mutableEvents.value = Event.Close("Closed")
                    }
                }
            }

        } catch (ex: Exception) {
            mutableEvents.value = Event.Error(ex)
        } catch (closeR: ClosedReceiveChannelException) {
            close()
            mutableEvents.value = Event.Error(closeR)
        }
    }

    private fun onMessage(bytes: ByteArray) {

    }
}