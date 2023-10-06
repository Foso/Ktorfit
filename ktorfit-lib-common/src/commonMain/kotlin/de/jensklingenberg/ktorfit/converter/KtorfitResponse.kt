package de.jensklingenberg.ktorfit.converter

import io.ktor.client.statement.*

/**
 * Represents the response from a Ktorfit request. */
public sealed interface KtorfitResponse {
    /**
     * Represents a successful response.
     * @property response The HTTP response.
     */
    public class Success(public val response: HttpResponse) : KtorfitResponse

    /**
     * Represents a failed response.
     * @property throwable The throwable associated with the failure.
     */
    public class Failed(public val throwable: Throwable) : KtorfitResponse
}