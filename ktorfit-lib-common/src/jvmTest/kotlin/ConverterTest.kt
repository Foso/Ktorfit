import de.jensklingenberg.ktorfit.Ktorfit
import de.jensklingenberg.ktorfit.converter.SuspendResponseConverter
import de.jensklingenberg.ktorfit.internal.*
import io.ktor.client.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.util.reflect.*
import io.ktor.utils.io.errors.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.runBlocking
import org.junit.Assert
import org.junit.Test

@OptIn(InternalKtorfitApi::class)
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
                val ext: HttpRequestBuilder.() -> Unit = {
                    method = HttpMethod.parse("GET")
                }
                val requestData = RequestData(
                    ktorfitRequestBuilder = ext,
                    relativeUrl = "",
                    returnTypeData = TypeData("kotlinx.coroutines.flow.Flow"),
                    requestTypeInfo = typeInfo<String>(), returnTypeInfo = typeInfo<String>()
                )
                KtorfitClient(ktorfit).request<Flow<String>, String>(requestData)
            }
        } catch (exception: Exception) {
            Assert.assertTrue(exception is IllegalArgumentException)
        }
    }


    @Test
    fun whenClientExceptionOccurs_HandleItInsideSuspendConverter() {

        try {

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


            val ktorfit = Ktorfit.Builder().baseUrl("http://www.jensklingenberg.de/").responseConverter(test).build()
            runBlocking {
                val ext: HttpRequestBuilder.() -> Unit = {
                    method = HttpMethod.parse("GET")
                }
                val requestData = RequestData(
                    ktorfitRequestBuilder = ext,
                    relativeUrl = "notexisting",
                    returnTypeData = TypeData("kotlinx.coroutines.flow.Flow"),
                    requestTypeInfo = typeInfo<String>(), returnTypeInfo = typeInfo<String>()
                )
                KtorfitClient(ktorfit).suspendRequest<Flow<String>, String>(requestData)
            }
        } catch (exception: Exception) {
            Assert.assertTrue(exception is IllegalArgumentException)

        }
    }


}


