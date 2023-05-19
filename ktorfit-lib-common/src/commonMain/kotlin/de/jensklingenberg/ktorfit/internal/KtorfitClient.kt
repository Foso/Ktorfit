package de.jensklingenberg.ktorfit.internal

import de.jensklingenberg.ktorfit.Ktorfit
import io.ktor.client.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.util.reflect.*
import kotlin.reflect.KClass
import kotlin.reflect.cast

@OptIn(InternalKtorfitApi::class)
internal class KtorfitClient(private val ktorfit: Ktorfit) : Client {

    private val httpClient: HttpClient = ktorfit.httpClient
    override var baseUrl: String = ktorfit.baseUrl

    /**
     * This will handle all requests for functions without suspend modifier
     */
    override fun <ReturnType, RequestType : Any?> request(
        requestData: RequestData
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

        ktorfit.nextResponseConverter(null, returnTypeData)?.let { requestConverter ->

            return requestConverter.convert {
                try {
                    val data =
                        suspendRequest<HttpResponse, HttpResponse>(
                            RequestData(
                                ktorfitRequestBuilder = requestData.ktorfitRequestBuilder,
                                returnTypeName = "io.ktor.client.statement.HttpResponse",
                                returnTypeInfo = typeInfo<HttpResponse>()
                            )
                        )
                    data!!
                } catch (ex: Exception) {
                    throw ex
                }
            } as ReturnType?
        }

        val typeIsNullable = returnTypeData.isNullable
        return if (typeIsNullable) {
            null
        } else {
            throw IllegalArgumentException("Add a RequestConverter for " + returnTypeData.qualifiedName + " or make function suspend")
        }

    }


    /**
     * This will handle all requests for functions with suspend modifier
     * Used by generated Code
     */
    override suspend fun <ReturnType, RequestType : Any?> suspendRequest(
        requestData: RequestData
    ): ReturnType? {
        val returnTypeData = requestData.getTypeData()
        val requestTypeInfo = returnTypeData.typeArgs.firstOrNull()?.typeInfo ?: returnTypeData.typeInfo

        try {
            if (returnTypeData.typeInfo.type == HttpStatement::class) {
                return httpClient.prepareRequest {
                    requestBuilder(requestData)
                } as ReturnType
            }

            if (returnTypeData.typeInfo.type == HttpResponse::class) {
                val response = httpClient.request {
                    requestBuilder(requestData)
                }
                return response as ReturnType
            }

            ktorfit.suspendResponseConverters.firstOrNull { converter ->
                converter.supportedType(
                    returnTypeData, true
                )
            }?.let {
                return it.wrapSuspendResponse<RequestType>(
                    typeData = returnTypeData,
                    requestFunction = {
                        Pair(requestTypeInfo, httpClient.request {
                            requestBuilder(requestData)
                        })
                    }, ktorfit
                ) as ReturnType
            }

            ktorfit.nextSuspendResponseConverter(null, returnTypeData)?.let {

                val response = httpClient.request {
                    requestBuilder(requestData)
                }
                return it.convert(response) as ReturnType?
            }
                ?: throw IllegalStateException("No SuspendResponseConverter found for " + returnTypeData.qualifiedName)

        } catch (exception: Exception) {
            val typeIsNullable = returnTypeData.isNullable
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
