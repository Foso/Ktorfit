package de.jensklingenberg.ktorfit.internal

import de.jensklingenberg.ktorfit.Ktorfit
import io.ktor.client.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.util.reflect.*

@OptIn(InternalKtorfitApi::class)
internal fun <ReturnType, RequestType : Any?> KtorfitClient.handleDeprecatedResponseConverters(
    requestData: RequestData,
    ktorfit: Ktorfit
): ReturnType? {
    val returnTypeData = requestData.getTypeData()

    val requestTypeInfo = returnTypeData.typeArgs.firstOrNull()?.typeInfo ?: returnTypeData.typeInfo

    ktorfit.responseConverters.firstOrNull { converter ->
        converter.supportedType(
            returnTypeData, false
        )
    }?.let { requestConverter ->
        return requestConverter.wrapResponse<RequestType?>(
            typeData = returnTypeData,
            requestFunction = {
                try {
                    val data =
                        suspendRequest<HttpResponse, HttpResponse>(
                            RequestData(
                                ktorfitRequestBuilder = requestData.ktorfitRequestBuilder,
                                returnTypeName = "io.ktor.client.statement.HttpResponse",
                                returnTypeInfo = typeInfo<HttpResponse>()
                            )
                        )
                    Pair(requestTypeInfo, data)
                } catch (ex: Exception) {
                    throw ex
                }
            },
            ktorfit = ktorfit
        ) as ReturnType?
    }

    return null
}

@OptIn(InternalKtorfitApi::class)
internal suspend fun <ReturnType, RequestType : Any?> handleDeprecatedSuspendResponseConverters(
    requestData: RequestData,
    httpClient: HttpClient,
    ktorfit: Ktorfit
): ReturnType? {
    val returnTypeData = requestData.getTypeData()

    val requestTypeInfo = returnTypeData.typeArgs.firstOrNull()?.typeInfo ?: returnTypeData.typeInfo
    ktorfit.suspendResponseConverters.firstOrNull { converter ->
        converter.supportedType(
            returnTypeData, true
        )
    }?.let {
        return it.wrapSuspendResponse<RequestType>(
            typeData = returnTypeData,
            requestFunction = {
                Pair(requestTypeInfo, httpClient.request {
                    requestData.ktorfitRequestBuilder(this)
                })
            }, ktorfit
        ) as ReturnType?
    }
    return null
}