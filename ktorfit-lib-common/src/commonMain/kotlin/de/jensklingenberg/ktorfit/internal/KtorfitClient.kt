package de.jensklingenberg.ktorfit.internal

import de.jensklingenberg.ktorfit.Ktorfit
import de.jensklingenberg.ktorfit.converter.KtorfitResult
import de.jensklingenberg.ktorfit.converter.builtin.DefaultSuspendResponseConverterFactory
import io.ktor.client.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.util.reflect.*
import kotlin.reflect.KClass
import kotlin.reflect.cast

@OptIn(InternalKtorfitApi::class)
internal class KtorfitClient(private val ktorfit: Ktorfit) : Client {

    private val httpClient: HttpClient = ktorfit.httpClient
    override val baseUrl: String = ktorfit.baseUrl

    /**
     * This will handle all requests for functions without suspend modifier
     */
    override fun <ReturnType, RequestType : Any?> request(
        requestData: RequestData
    ): ReturnType? {
        val returnTypeData = requestData.getTypeData()

        ktorfit.nextResponseConverter(null, returnTypeData)?.let { responseConverter ->

            return responseConverter.convert {
                suspendRequest<HttpResponse, HttpResponse>(
                    RequestData(
                        ktorfitRequestBuilder = requestData.ktorfitRequestBuilder,
                        returnTypeName = "io.ktor.client.statement.HttpResponse",
                        returnTypeInfo = typeInfo<HttpResponse>()
                    )
                )!!
            } as ReturnType?
        }

        /**
         * Keeping this for compatibility
         */
        handleDeprecatedResponseConverters<ReturnType, RequestType>(requestData, ktorfit)?.let {
            return it
        }

        val typeIsNullable = returnTypeData.typeInfo.kotlinType?.isMarkedNullable ?: false
        return if (typeIsNullable) {
            null
        } else {
            throw IllegalArgumentException("Add a ResponseConverter for " + returnTypeData.qualifiedName + " or make function suspend")
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

        try {
            if (returnTypeData.typeInfo.type == HttpStatement::class) {
                return httpClient.prepareRequest {
                    requestBuilder(requestData)
                } as ReturnType
            }

            ktorfit.nextSuspendResponseConverter(null, returnTypeData)?.let {

                val result: KtorfitResult = try {
                    KtorfitResult.Success(httpClient.request {
                        requestBuilder(requestData)
                    })
                } catch (exception: Exception) {
                    KtorfitResult.Failed(exception)
                }
                return it.convert(result) as ReturnType?
            }

            /**
             * Keeping this for compatibility
             */
            handleDeprecatedSuspendResponseConverters<ReturnType, RequestType>(requestData, httpClient, ktorfit)?.let {
                return it
            } ?: DefaultSuspendResponseConverterFactory().suspendResponseConverter(returnTypeData, ktorfit).let {
                val response = httpClient.request {
                    requestBuilder(requestData)
                }
                return it.convert(response) as ReturnType?
            }

        } catch (exception: Exception) {
            val typeIsNullable = returnTypeData.typeInfo.kotlinType?.isMarkedNullable ?: false
            return if (typeIsNullable) {
                null
            } else {
                throw exception
            }
        }
    }

    override fun <T : Any> convertParameterType(data: Any, parameterType: KClass<*>, requestType: KClass<T>): T {
        ktorfit.nextRequestParameterConverter(null, parameterType, requestType)?.let {
            return requestType.cast(it.convert(data))
        }

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
