package de.jensklingenberg.ktorfit

import io.ktor.client.statement.*
import io.ktor.http.*

/** An HTTP response.  */
@Suppress("MemberVisibilityCanBePrivate")
public class Response<T> private constructor(
    rawResponse: HttpResponse,
    body: T?,
    errorBody: Any?,
) {
    private val rawResponse: HttpResponse
    private val body: T?
    private val errorBody: Any?

    init {
        this.rawResponse = rawResponse
        this.body = body
        this.errorBody = errorBody
    }

    /** The raw response from the HTTP client.  */
    public fun raw(): HttpResponse {
        return rawResponse
    }

    /** HTTP status.  */
    public val status: HttpStatusCode get() = rawResponse.status

    /** HTTP status code.  */
    public val code: Int
        get() = status.value

    /** HTTP status message or null if unknown.  */
    public val message: String
        get() = status.toString()

    /** HTTP headers.  */
    public val headers: Headers
        get() = rawResponse.headers

    /** Returns true if status code is in the range [200..300).  */
    public val isSuccessful: Boolean
        get() = status.isSuccess()

    /** The deserialized response body of a [successful][.isSuccessful] response.  */
    public fun body(): T? {
        return body
    }

    /** The raw response body of an [unsuccessful][.isSuccessful] response.  */
    public fun errorBody(): Any? {
        return errorBody
    }

    override fun toString(): String {
        return rawResponse.toString()
    }

    public companion object {

        /**
         * Create a successful response from `rawResponse` with `body` as the deserialized
         * body.
         */
        public fun <T> success(body: T?, rawResponse: HttpResponse): Response<T?> {
            require(rawResponse.status.isSuccess()) { "rawResponse must be successful response" }
            return Response(rawResponse, body, null)
        }

        /** Create an error response from `rawResponse` with `body` as the error body.  */
        public fun <T> error(body: Any, rawResponse: HttpResponse): Response<T?> {
            require(!rawResponse.status.isSuccess()) { "rawResponse should not be successful response" }
            return Response(rawResponse, null, body)
        }
    }
}