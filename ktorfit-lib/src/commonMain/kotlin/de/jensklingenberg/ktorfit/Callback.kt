package de.jensklingenberg.ktorfit

import io.ktor.client.statement.*

interface Callback<T> {
    fun onResponse(call: T, response: HttpResponse)
    fun onError(exception: Exception)
}
