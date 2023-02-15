package de.jensklingenberg.ktorfit.internal

import de.jensklingenberg.ktorfit.Ktorfit

public interface Client {
    public val ktorfit: Ktorfit

    /**
     * This will handle all requests for functions without suspend modifier
     */
    public fun <ReturnType, RequestType> request(requestData: RequestData): ReturnType?

    /**
     * This will handle all requests for functions with suspend modifier
     * Used by generated Code
     */
    public suspend fun <ReturnType, PRequest> suspendRequest(requestData: RequestData): ReturnType?
}