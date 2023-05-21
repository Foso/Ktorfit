package de.jensklingenberg.ktorfit.converter

import de.jensklingenberg.ktorfit.Ktorfit
import de.jensklingenberg.ktorfit.TestEngine
import de.jensklingenberg.ktorfit.internal.TypeData
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.util.reflect.*
import kotlinx.coroutines.runBlocking
import org.junit.Assert
import org.junit.Test



class ConverterFactoryTest {

    @Test
    fun whenClientExceptionOccurs_HandleItInsideSuspendConverter() {

            val engine = object : TestEngine() {
                override fun getRequestData(data: HttpRequestData) {
                }
            }
            val test = object : Converter.Factory {

                override fun suspendResponseConverter(
                    typeData: TypeData,
                    ktorfit: Ktorfit
                ): Converter.SuspendResponseConverter<HttpResponse, *>? {
                    if (typeData.qualifiedName == "kotlinx.coroutines.flow.Flow") {
                        return object : Converter.SuspendResponseConverter<HttpResponse, Any> {
                            override suspend fun convert(response: HttpResponse): Any {
                                return try {
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

            val ktorfit = Ktorfit.Builder()
                .httpClient(engine)
                .baseUrl("http://www.jensklingenberg.de/")
                .converterFactories(test)
                .build()

            runBlocking {
                ktorfit.create<ConverterTestApi>(_ConverterTestApiImpl()).clientException()
            }


    }


}