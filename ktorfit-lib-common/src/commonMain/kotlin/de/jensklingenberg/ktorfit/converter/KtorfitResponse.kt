package de.jensklingenberg.ktorfit.converter

import io.ktor.client.statement.*

public sealed interface KtorfitResponse {
    public class Success(public val response: HttpResponse) : KtorfitResponse
    public class Failed(public val throwable: Throwable) : KtorfitResponse
}