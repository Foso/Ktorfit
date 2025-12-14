package de.jensklingenberg.ktorfit.internal

import de.jensklingenberg.ktorfit.Ktorfit
import de.jensklingenberg.ktorfit.converter.KtorfitResult
import de.jensklingenberg.ktorfit.converter.TypeData
import io.ktor.client.HttpClient
import io.ktor.client.request.HttpRequestBuilder
import io.ktor.client.request.prepareRequest
import io.ktor.client.request.request
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.HttpStatement
import io.ktor.util.reflect.TypeInfo
import io.ktor.util.reflect.typeInfo
import kotlin.reflect.KClass
import kotlin.reflect.cast

/**
 * Cant make this internal because it is used by generated code
 */
@InternalKtorfitApi
public class KtorfitConverterHelper(
    private val ktorfit: Ktorfit
) {
    /**
     * This will handle all requests for functions without suspend modifier
     */

    public fun <ReturnType> request(
        httpClient: HttpClient,
        requestBuilder: HttpRequestBuilder.() -> Unit,
        typeInfo: TypeInfo,
        qualifier: String = "",
    ): ReturnType? {

        val httpResponseLambda: suspend () -> HttpResponse = {
            httpClient.request {
                requestBuilder(this)
            }
        }

        return request(httpResponseLambda,typeInfo,qualifier)
    }


    private fun <ReturnType> request(
        httpClient: suspend () -> HttpResponse,
        typeInfo: TypeInfo,
        qualifier: String = "",
    ): ReturnType? {
        val returnTypeData =
            TypeData.createTypeData(
                typeInfo = typeInfo,
                qualifiedTypename = qualifier,
            )
        ktorfit.nextResponseConverter(null, returnTypeData)?.let { responseConverter ->
            return responseConverter.convert {
                suspendRequest<HttpResponse>(
                    httpClient,
                    typeInfo<HttpResponse>(),
                    "io.ktor.client.statement.HttpResponse",
                )!!
            } as ReturnType?
        }

        throw IllegalStateException("Add a ResponseConverter for " + returnTypeData.typeInfo + " or make function suspend")
    }

    public suspend fun <ReturnType> suspendRequest(
        httpClient: HttpClient,
        requestBuilder: HttpRequestBuilder.() -> Unit,
        typeInfo: TypeInfo,
        qualifier: String = "",
    ): ReturnType? {
        val typeData =
            TypeData.createTypeData(
                typeInfo = typeInfo,
                qualifiedTypename = qualifier,
            )
        if (typeData.typeInfo.type == HttpStatement::class) {
            return httpClient.prepareRequest {
                requestBuilder(this)
            } as ReturnType
        }
        val httpResponseLambda: suspend () -> HttpResponse = {
            httpClient.request {
                requestBuilder(this)
            }
        }

        return suspendRequest(httpResponseLambda,typeInfo,qualifier)
    }

    /**
     * This will handle all requests for functions with suspend modifier
     * Used by generated Code
     */
    public suspend fun <ReturnType> suspendRequest(
        httpClient: suspend () -> HttpResponse,
        typeInfo: TypeInfo,
        qualifier: String = "",
    ): ReturnType? {
        val typeData =
            TypeData.createTypeData(
                typeInfo = typeInfo,
                qualifiedTypename = qualifier,
            )

        ktorfit.nextSuspendResponseConverter(null, typeData)?.let {
            val result: KtorfitResult =
                try {
                    KtorfitResult.Success(
                        httpClient(),
                    )
                } catch (throwable: Throwable) {
                    KtorfitResult.Failure(throwable)
                }
            return it.convert(result) as ReturnType?
        }

        throw IllegalStateException("No SuspendResponseConverter found to convert ${typeData.typeInfo}")
    }

    public fun <T : Any> convertParameterType(
        data: Any,
        parameterType: KClass<*>,
        requestType: KClass<T>,
    ): T {
        ktorfit.nextRequestParameterConverter(null, parameterType, requestType)?.let {
            return requestType.cast(it.convert(data))
        }

        throw IllegalStateException("No RequestConverter found to convert ${parameterType.simpleName} to ${requestType.simpleName}")
    }
}
