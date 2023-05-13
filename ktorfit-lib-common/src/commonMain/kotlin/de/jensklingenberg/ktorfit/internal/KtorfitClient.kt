package de.jensklingenberg.ktorfit.internal

import de.jensklingenberg.ktorfit.Ktorfit
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.client.request.forms.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.http.content.*
import io.ktor.util.reflect.*
import kotlin.reflect.KClass
import kotlin.reflect.cast


internal class KtorfitClient(private val ktorfit: Ktorfit) : Client {

    private val httpClient: HttpClient = ktorfit.httpClient
    override var baseUrl: String = ktorfit.baseUrl

    /**
     * This will handle all requests for functions without suspend modifier
     */
    override fun <ReturnType, RequestType : Any?> request(
        requestData: RequestData
    ): ReturnType? {

        ktorfit.responseConverters.firstOrNull { converter ->
            converter.supportedType(
                requestData.returnTypeData, false
            )
        }?.let { requestConverter ->
            return requestConverter.wrapResponse<RequestType?>(
                typeData = requestData.returnTypeData,
                requestFunction = {
                    try {
                        val data =
                            suspendRequest<HttpResponse, HttpResponse>(requestData.copy(returnTypeInfo = typeInfo<HttpResponse>()))
                        Pair(requestData.requestTypeInfo, data)
                    } catch (ex: Exception) {
                        throw ex
                    }
                },
                ktorfit = ktorfit
            ) as ReturnType?
        }

        val typeIsNullable = requestData.returnTypeData.isNullable
        return if (typeIsNullable) {
            null
        } else {
            throw IllegalArgumentException("Add a RequestConverter for " + requestData.returnTypeData.qualifiedName + " or make function suspend")
        }

    }

    /**
     * This will handle all requests for functions with suspend modifier
     * Used by generated Code
     */
    override suspend fun <ReturnType, RequestType : Any?> suspendRequest(
        requestData: RequestData
    ): ReturnType? {
        try {

            if (requestData.returnTypeInfo.type == HttpStatement::class) {
                return httpClient.prepareRequest {
                    requestBuilder(requestData)
                } as ReturnType
            }

            if (requestData.returnTypeInfo.type == HttpResponse::class) {
                val response = httpClient.request {
                    requestBuilder(requestData)
                }
                return response as ReturnType
            }

            ktorfit.suspendResponseConverters.firstOrNull { converter ->
                converter.supportedType(
                    requestData.returnTypeData, true
                )
            }?.let {
                return it.wrapSuspendResponse<RequestType>(
                    typeData = requestData.returnTypeData,
                    requestFunction = {
                        Pair(requestData.requestTypeInfo, httpClient.request {
                            requestBuilder(requestData)
                        })
                    }, ktorfit
                ) as ReturnType
            }

            val response = httpClient.request {
                requestBuilder(requestData)
            }
            return response.body(requestData.returnTypeInfo)

        } catch (exception: Exception) {
            val typeIsNullable = requestData.returnTypeData.isNullable
            return if (typeIsNullable) {
                null
            } else {
                throw exception
            }
        }
    }

    override fun <T : Any> convertParameterType(data: Any, parameterType: KClass<*>, requestType: KClass<T>): T {
        val requestConverter = ktorfit.requestConverters.firstOrNull {
            it.supportedType(parameterType, requestType)
        }
            ?: throw IllegalArgumentException("No RequestConverter found to convert ${parameterType.simpleName} to ${requestType.simpleName}")
        return requestType.cast(requestConverter.convert(data))
    }

    private fun HttpRequestBuilder.requestBuilder(
        requestData: RequestData
    ) {
        requestData.ktorfitRequestBuilder(this)
    }

}
