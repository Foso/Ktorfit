package de.jensklingenberg.ktorfit.internal

import kotlin.reflect.KClass

public interface Client {

    /**
     * This will handle all requests for functions without suspend modifier
     */
    public fun <ReturnType, RequestType> request(requestData: RequestData): ReturnType?

    /**
     * This will handle all requests for functions with suspend modifier
     * Used by generated Code
     */
    public suspend fun <ReturnType, RequestType> suspendRequest(requestData: RequestData): ReturnType?

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