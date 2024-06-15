package de.jensklingenberg.ktorfit.converter

import de.jensklingenberg.ktorfit.Ktorfit
import de.jensklingenberg.ktorfit.TestEngine
import de.jensklingenberg.ktorfit.http.GET
import io.ktor.client.call.NoTransformationFoundException
import io.ktor.client.call.body
import io.ktor.client.request.HttpRequestData
import io.ktor.client.statement.HttpResponse
import io.ktor.util.reflect.typeInfo
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.runBlocking
import org.junit.Assert
import org.junit.Test

interface ConverterFactoryTestApi {
    @GET("posts")
    suspend fun suspendClientException(): Flow<String>

    @GET("posts")
    fun clientException(): Flow<String>
}

class ConverterFactoryTest {
    @Test
    fun whenClientExceptionOccurs_HandleItInsideSuspendResponseConverter() {
        val engine =
            object : TestEngine() {
                override fun getRequestData(data: HttpRequestData) {
                }
            }
        val testConverterFactory =
            object : Converter.Factory {
                override fun suspendResponseConverter(
                    typeData: TypeData,
                    ktorfit: Ktorfit,
                ): Converter.SuspendResponseConverter<HttpResponse, *>? {
                    if (typeData.qualifiedName == "kotlinx.coroutines.flow.Flow") {
                        return object : Converter.SuspendResponseConverter<HttpResponse, Any> {
                            override suspend fun convert(result: KtorfitResult): Any {
                                when (result) {
                                    is KtorfitResult.Success -> {
                                        return try {
                                            result.response.body(typeInfo<Int>())
                                        } catch (ex: Exception) {
                                            Assert.assertTrue(ex is NoTransformationFoundException)
                                        }
                                    }
                                    is KtorfitResult.Failure -> {
                                        throw result.throwable
                                    }
                                }
                            }
                        }
                    }
                    return null
                }
            }

        val ktorfit =
            Ktorfit
                .Builder()
                .httpClient(engine)
                .baseUrl("http://www.jensklingenberg.de/")
                .converterFactories(testConverterFactory)
                .build()

        runBlocking {
            ktorfit.create<ConverterFactoryTestApi>(_ConverterFactoryTestApiProvider()).suspendClientException()
        }
    }

    @Test
    fun whenClientExceptionOccurs_HandleItInsideResponseConverter() {
        val engine =
            object : TestEngine() {
                override fun getRequestData(data: HttpRequestData) {
                }
            }
        val test =
            object : Converter.Factory {
                override fun responseConverter(
                    typeData: TypeData,
                    ktorfit: Ktorfit,
                ): Converter.ResponseConverter<HttpResponse, *>? {
                    if (typeData.typeInfo.type == Flow::class) {
                        return object : Converter.ResponseConverter<HttpResponse, Flow<Any>> {
                            override fun convert(getResponse: suspend () -> HttpResponse): Flow<Any> =
                                flow {
                                    val response = getResponse()
                                    try {
                                        response.body(typeInfo<Int>())
                                    } catch (ex: Exception) {
                                        Assert.assertTrue(ex is NoTransformationFoundException)
                                    }
                                }
                        }
                    }
                    return null
                }
            }

        val ktorfit =
            Ktorfit
                .Builder()
                .httpClient(engine)
                .baseUrl("http://www.jensklingenberg.de/")
                .converterFactories(test)
                .build()

        runBlocking {
            ktorfit.create<ConverterFactoryTestApi>(_ConverterFactoryTestApiProvider()).clientException().collect()
        }
    }
}
