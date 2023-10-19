package de.jensklingenberg.ktorfit.internal

import io.ktor.client.request.*
import kotlin.reflect.KClass

@InternalKtorfitApi
public interface Client {

    /**
     * This will handle all requests for functions without suspend modifier
     */
    public fun <ReturnType, RequestType> request(
        returnTypeData: TypeData,
        requestBuilder: HttpRequestBuilder.() -> Unit
    ): ReturnType?

    /**
     * This will handle all requests for functions with suspend modifier
     * Used by generated Code
     */
    public suspend fun <ReturnType, RequestType> suspendRequest(
        typeData: TypeData,
        requestBuilder: HttpRequestBuilder.() -> Unit
    ): ReturnType?

    /**
     * Convert [data] of type [parameterType] to [requestType]
     * @return converted [data]
     */
    public fun <T : Any> convertParameterType(data: Any, parameterType: KClass<*>, requestType: KClass<T>): T

    /**
     * Set baseUrl of the client
     */
    public val baseUrl: String
}