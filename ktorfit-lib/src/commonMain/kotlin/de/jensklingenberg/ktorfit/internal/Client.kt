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

    public fun <T> convertRequestType(data: Any, parameterType: KClass<*>, requestType: KClass<*>): T
}