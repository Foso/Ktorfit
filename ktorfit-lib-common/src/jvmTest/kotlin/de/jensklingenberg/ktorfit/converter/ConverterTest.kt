package de.jensklingenberg.ktorfit.converter

import de.jensklingenberg.ktorfit.Ktorfit
import de.jensklingenberg.ktorfit.TestEngine
import de.jensklingenberg.ktorfit.http.GET
import de.jensklingenberg.ktorfit.internal.TypeData
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.util.reflect.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.runBlocking
import org.junit.Assert
import org.junit.Test

interface ConverterTestApi {

    @GET("posts")
    suspend fun clientException(): Flow<String>
}

class ConverterTest {

    @Test
    fun whenClientExceptionOccurs_HandleItInsideSuspendConverter() {

        val test = object : SuspendResponseConverter {
            override suspend fun <RequestType> wrapSuspendResponse(
                typeData: TypeData,
                requestFunction: suspend () -> Pair<TypeInfo, HttpResponse>,
                ktorfit: Ktorfit
            ): Any {
                return try {
                    val (info, response) = requestFunction()
                    response.body(typeInfo<Int>())
                } catch (ex: Exception) {
                    Assert.assertTrue(ex is NoTransformationFoundException)
                }
            }

            override fun supportedType(typeData: TypeData, isSuspend: Boolean): Boolean {
                return typeData.qualifiedName == "kotlinx.coroutines.flow.Flow"
            }

        }


        try {
            val engine = object : TestEngine() {
                override fun getRequestData(data: HttpRequestData) {
                }
            }


            val ktorfit =
                Ktorfit.Builder().httpClient(engine).baseUrl("http://www.jensklingenberg.de/").responseConverter(test)
                    .build()
            runBlocking {
                ktorfit.create<ConverterTestApi>(_ConverterTestApiImpl()).clientException().collect()
            }

        } catch (exception: Exception) {

        }
    }
}