package de.jensklingenberg.ktorfit

import de.jensklingenberg.ktorfit.converter.Converter
import de.jensklingenberg.ktorfit.converter.SuspendResponseConverter
import de.jensklingenberg.ktorfit.http.GET
import de.jensklingenberg.ktorfit.internal.TypeData
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.util.reflect.*
import io.ktor.utils.io.errors.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.runBlocking
import org.junit.Assert
import org.junit.Test

interface ConverterTestApi {
    @GET("posts")
    fun converterMissing(): Flow<String>

    @GET("posts")
    suspend fun clientException(): Flow<String>
}

class ConverterTest {

    @Test
    fun throwExceptionWhenConverterMissing() {

        try {
            val engine = object : TestEngine() {
                override fun getRequestData(data: HttpRequestData) {
                }
            }

            val ktorfit = Ktorfit.Builder().baseUrl("http://www.test.de/").httpClient(HttpClient(engine)).build()
            runBlocking {
                ktorfit.create<ConverterTestApi>(_ConverterTestApiImpl()).converterMissing()

            }
        } catch (exception: Exception) {
            Assert.assertTrue(exception is IllegalArgumentException)
            Assert.assertTrue(exception.message?.startsWith("Add a ResponseConverter") ?: false)
        }
    }


    @Test
    fun whenClientExceptionOccurs_HandleItInsideSuspendConverter() {

        try {
            val engine = object : TestEngine() {
                override fun getRequestData(data: HttpRequestData) {
                }
            }
            val test = object : SuspendResponseConverter {
                override suspend fun <RequestType> wrapSuspendResponse(
                    typeData: TypeData,
                    requestFunction: suspend () -> Pair<TypeInfo, HttpResponse>,
                    ktorfit: Ktorfit
                ): Any {
                    return try {
                        val (info, response) = requestFunction()
                        return response
                    } catch (ex: Exception) {
                        Assert.assertTrue(ex is IOException)
                    }
                }

                override fun supportedType(typeData: TypeData, isSuspend: Boolean): Boolean {
                    return typeData.qualifiedName == "kotlinx.coroutines.flow.Flow"
                }

            }


            val ktorfit = Ktorfit.Builder().httpClient(engine).baseUrl("http://www.jensklingenberg.de/").responseConverter(test).build()
            runBlocking {
                ktorfit.create<ConverterTestApi>(_ConverterTestApiImpl()).clientException()

            }
        } catch (exception: Exception) {
            Assert.assertTrue(exception is IllegalArgumentException)

        }
    }




}