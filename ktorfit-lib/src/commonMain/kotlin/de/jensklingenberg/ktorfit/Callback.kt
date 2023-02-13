package de.jensklingenberg.ktorfit

import io.ktor.client.statement.*

public interface Callback<T> {
    public fun onResponse(call: T, response: HttpResponse)
    public fun onError(exception: Throwable)
}
