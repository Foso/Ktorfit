package de.jensklingenberg.ktorfit.internal

import de.jensklingenberg.ktorfit.Ktorfit
import io.ktor.client.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.util.reflect.*

@OptIn(InternalKtorfitApi::class)
internal fun <ReturnType, RequestType : Any?> KtorfitConverterHelper.handleDeprecatedResponseConverters(
    returnTypeData: TypeData,
    ktorfit: Ktorfit,
    requestBuilder: HttpRequestBuilder.() -> Unit
): ReturnType? {

    val requestTypeInfo = returnTypeData.typeArgs.firstOrNull()?.typeInfo ?: returnTypeData.typeInfo

    ktorfit.responseConverters.firstOrNull { converter ->
        converter.supportedType(
            returnTypeData, false
        )
    }?.let {
        return it.wrapResponse<RequestType?>(
            typeData = returnTypeData,
            requestFunction = {
                try {
                    val data =
                        suspendRequest<HttpResponse, HttpResponse>(
                            TypeData.createTypeData("io.ktor.client.statement.HttpResponse", typeInfo<HttpResponse>()),
                            requestBuilder
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

internal suspend fun <ReturnType, RequestType : Any?> handleDeprecatedSuspendResponseConverters(
    returnTypeData: TypeData,
    httpClient: HttpClient,
    ktorfit: Ktorfit,
    requestBuilder: HttpRequestBuilder.() -> Unit
): ReturnType? {

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
                    requestBuilder(this)
                })
            }, ktorfit
        ) as ReturnType?
    }


    return null
}