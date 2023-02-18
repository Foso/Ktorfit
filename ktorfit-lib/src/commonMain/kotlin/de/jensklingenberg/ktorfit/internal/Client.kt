package de.jensklingenberg.ktorfit.internal

import de.jensklingenberg.ktorfit.converter.request.RequestConverter

public interface Client {
    public val requestConverters: Set<RequestConverter>

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