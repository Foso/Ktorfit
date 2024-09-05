package de.jensklingenberg.ktorfit.internal

import de.jensklingenberg.ktorfit.Ktorfit
import de.jensklingenberg.ktorfit.converter.KtorfitResult
import de.jensklingenberg.ktorfit.converter.TypeData
import de.jensklingenberg.ktorfit.converter.TypeData2
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
public class KtorfitConverterHelper(
    private val ktorfit: Ktorfit
) {
    private val httpClient: HttpClient = ktorfit.httpClient

    /**
     * This will handle all requests for functions without suspend modifier
     */
    public fun <ReturnType> request(
        returnTypeData: TypeData2,
        requestBuilder: HttpRequestBuilder.() -> Unit,
    ): ReturnType? {
        val typo = TypeData.createTypeData(returnTypeData.qualifiedName, returnTypeData.typeInfo)
        ktorfit.nextResponseConverter(null, typo)?.let { responseConverter ->
            return responseConverter.convert {
                suspendRequest<HttpResponse>(
                    TypeData2.createTypeData(
                        "io.ktor.client.statement.HttpResponse",
                        typeInfo<HttpResponse>(),
                    ),
                    requestBuilder,
                )!!
            } as ReturnType?
        }

        throw IllegalStateException("Add a ResponseConverter for " + returnTypeData.typeInfo + " or make function suspend")
    }

    /**
     * This will handle all requests for functions with suspend modifier
     * Used by generated Code
     */
    public suspend fun <ReturnType> suspendRequest(
        typeData: TypeData2,
        requestBuilder: HttpRequestBuilder.() -> Unit,
    ): ReturnType? {
        if (typeData.typeInfo?.type == HttpStatement::class) {
            return httpClient.prepareRequest {
                requestBuilder(this)
            } as ReturnType
        }

        val typo = TypeData.createTypeData(typeData.qualifiedName, typeData.typeInfo)

        ktorfit.nextSuspendResponseConverter(null, typo)?.let {
            val result: KtorfitResult =
                try {
                    KtorfitResult.Success(
                        httpClient.request {
                            requestBuilder(this)
                        },
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
