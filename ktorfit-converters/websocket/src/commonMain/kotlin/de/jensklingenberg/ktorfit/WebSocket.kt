package de.jensklingenberg.ktorfit

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
