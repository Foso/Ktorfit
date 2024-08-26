package de.jensklingenberg.ktorfit.converter

import io.ktor.client.statement.HttpResponse

/**
 * Represents the result from a Ktorfit request. */
public sealed interface KtorfitResult {
    /**
     * Represents a successful response.
     * @property response The HTTP response.
     */
    public class Success(
        public val response: HttpResponse
    ) : KtorfitResult

    /**
     * Represents a failed response.
     * @property throwable The throwable associated with the failure.
     */
    public class Failure(
        public val throwable: Throwable
    ) : KtorfitResult
}
