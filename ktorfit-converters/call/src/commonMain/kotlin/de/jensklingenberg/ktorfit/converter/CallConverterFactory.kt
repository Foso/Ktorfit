package de.jensklingenberg.ktorfit.converter

import de.jensklingenberg.ktorfit.Call
import de.jensklingenberg.ktorfit.Callback
import de.jensklingenberg.ktorfit.Ktorfit
import io.ktor.client.call.body
import io.ktor.client.statement.HttpResponse
import kotlinx.coroutines.launch

/**
 * Factory that enables the use of Call<T> as return type
 */
public class CallConverterFactory : Converter.Factory {
    private class CallResponseConverter(val typeData: TypeData, val ktorfit: Ktorfit) :
        Converter.ResponseConverter<HttpResponse, Call<Any?>> {
        override fun convert(getResponse: suspend () -> HttpResponse): Call<Any?> {
            return object : Call<Any?> {
                override fun onExecute(callBack: Callback<Any?>) {
                    ktorfit.httpClient.launch {
                        try {
                            val response = getResponse()

                            val convertedBody =
                                ktorfit.nextSuspendResponseConverter(
                                    null,
                                    typeData.typeArgs.first(),
                                )?.convert(KtorfitResult.Success(response))
                                    ?: response.body(typeData.typeArgs.first().typeInfo)
                            callBack.onResponse(convertedBody, response)
                        } catch (ex: Exception) {
                            callBack.onError(ex)
                        }
                    }
                }
            }
        }
    }

    private class CallSuspendResponseConverter(val typeData: TypeData, val ktorfit: Ktorfit) :
        Converter.SuspendResponseConverter<HttpResponse, Call<Any?>> {
        override suspend fun convert(result: KtorfitResult): Call<Any?> {
            return object : Call<Any?> {
                override fun onExecute(callBack: Callback<Any?>) {
                    when (result) {
                        is KtorfitResult.Success -> {
                            val response = result.response
                            ktorfit.httpClient.launch {
                                try {
                                    val data =
                                        ktorfit.nextSuspendResponseConverter(
                                            null,
                                            typeData.typeArgs.first(),
                                        )?.convert(result)
                                    callBack.onResponse(data!!, response)
                                } catch (ex: Exception) {
                                    callBack.onError(ex)
                                }
                            }
                        }

                        is KtorfitResult.Failure -> {
                            callBack.onError(result.throwable)
                        }
                    }
                }
            }
        }
    }

    override fun responseConverter(
        typeData: TypeData,
        ktorfit: Ktorfit,
    ): Converter.ResponseConverter<HttpResponse, *>? {
        if (typeData.typeInfo.type == Call::class) {
            return CallResponseConverter(typeData, ktorfit)
        }
        return null
    }

    override fun suspendResponseConverter(
        typeData: TypeData,
        ktorfit: Ktorfit,
    ): Converter.SuspendResponseConverter<HttpResponse, *>? {
        if (typeData.typeInfo.type == Call::class) {
            return CallSuspendResponseConverter(typeData, ktorfit)
        }
        return null
    }
}
