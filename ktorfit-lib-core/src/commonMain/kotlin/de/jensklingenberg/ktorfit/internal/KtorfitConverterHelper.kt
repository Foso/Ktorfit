package de.jensklingenberg.ktorfit.internal

import de.jensklingenberg.ktorfit.Ktorfit
import de.jensklingenberg.ktorfit.converter.KtorfitResult
import de.jensklingenberg.ktorfit.converter.builtin.DefaultSuspendResponseConverterFactory
import io.ktor.client.HttpClient
import io.ktor.client.request.HttpRequestBuilder
import io.ktor.client.request.prepareRequest
import io.ktor.client.request.request
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.HttpStatement
import io.ktor.util.reflect.typeInfo
import kotlin.reflect.KClass
import kotlin.reflect.cast

/**
 * Cant make this internal because it is used by generated code
 */
@InternalKtorfitApi
public class KtorfitConverterHelper(private val ktorfit: Ktorfit) {

    private val httpClient: HttpClient = ktorfit.httpClient

    /**
     * This will handle all requests for functions without suspend modifier
     */
    public fun <ReturnType, RequestType : Any?> request(
        returnTypeData: TypeData,
        requestBuilder: HttpRequestBuilder.() -> Unit
    ): ReturnType? {

        ktorfit.nextResponseConverter(null, returnTypeData)?.let { responseConverter ->
            return responseConverter.convert {
                suspendRequest<HttpResponse, HttpResponse>(
                    TypeData.createTypeData(
                        "io.ktor.client.statement.HttpResponse",
                        typeInfo<HttpResponse>()
                    ),
                    requestBuilder
                )!!
            } as ReturnType?
        }

        /**
         * Keeping this for compatibility
         */
        handleDeprecatedResponseConverters<ReturnType, RequestType>(
            returnTypeData,
            ktorfit,
            requestBuilder
        )?.let {
            return it
        }

        val typeIsNullable = returnTypeData.typeInfo.kotlinType?.isMarkedNullable ?: false
        return if (typeIsNullable) {
            null
        } else {
            throw IllegalStateException("Add a ResponseConverter for " + returnTypeData.qualifiedName + " or make function suspend")
        }

    }

    /**
     * This will handle all requests for functions with suspend modifier
     * Used by generated Code
     */
    public suspend fun <ReturnType, RequestType : Any?> suspendRequest(
        typeData: TypeData,
        requestBuilder: HttpRequestBuilder.() -> Unit
    ): ReturnType? {

        try {
            if (typeData.typeInfo.type == HttpStatement::class) {
                return httpClient.prepareRequest {
                    requestBuilder(this)
                } as ReturnType
            }

            ktorfit.nextSuspendResponseConverter(null, typeData)?.let {
                val result: KtorfitResult = try {
                    KtorfitResult.Success(httpClient.request {
                        requestBuilder(this)
                    })
                } catch (exception: Exception) {
                    KtorfitResult.Failure(exception)
                }
                return it.convert(result) as ReturnType?
            }

            /**
             * Keeping this for compatibility
             */
            handleDeprecatedSuspendResponseConverters<ReturnType, RequestType>(
                typeData,
                httpClient,
                ktorfit,
                requestBuilder
            )?.let {
                return it
            } ?: DefaultSuspendResponseConverterFactory().suspendResponseConverter(
                typeData,
                ktorfit
            ).let {
                val result: KtorfitResult = try {
                    KtorfitResult.Success(httpClient.request {
                        requestBuilder(this)
                    })
                } catch (exception: Exception) {
                    KtorfitResult.Failure(exception)
                }
                return it.convert(result) as ReturnType?
            }

        } catch (exception: Exception) {
            val typeIsNullable = typeData.typeInfo.kotlinType?.isMarkedNullable ?: false
            return if (typeIsNullable) {
                null
            } else {
                throw exception
            }
        }
    }

    public fun <T : Any> convertParameterType(
        data: Any,
        parameterType: KClass<*>,
        requestType: KClass<T>
    ): T {
        ktorfit.nextRequestParameterConverter(null, parameterType, requestType)?.let {
            return requestType.cast(it.convert(data))
        }

        val requestConverter = ktorfit.requestConverters.firstOrNull {
            it.supportedType(parameterType, requestType)
        }
            ?: throw IllegalStateException("No RequestConverter found to convert ${parameterType.simpleName} to ${requestType.simpleName}")
        return requestType.cast(requestConverter.convert(data))
    }


}
