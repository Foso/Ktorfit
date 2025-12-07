import de.jensklingenberg.ktorfit.Call
import de.jensklingenberg.ktorfit.Callback
import de.jensklingenberg.ktorfit.Ktorfit
import de.jensklingenberg.ktorfit.converter.CallConverterFactory
import de.jensklingenberg.ktorfit.converter.TypeData2
import io.ktor.client.HttpClient
import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.respond
import io.ktor.client.request.request
import io.ktor.client.statement.HttpResponse
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.http.headersOf
import io.ktor.util.reflect.typeInfo
import io.ktor.utils.io.ByteReadChannel
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

class ConverterDefaultResponseConverter {
    @Test
    fun whenCallConverterIsUsedThenRespondSuccessful() =
        runBlocking {
            val mockResponseContent = """{"ip":"127.0.0.1"}"""

            val mockEngine =
                MockEngine { _ ->
                    respond(
                        content = ByteReadChannel(mockResponseContent),
                        status = HttpStatusCode.OK,
                        headers = headersOf(HttpHeaders.ContentType, "application/json"),
                    )
                }

            val client = HttpClient(mockEngine) {}

            val responseFunc = suspend { client.request("http://example.org/") }
            val converter = CallConverterFactory()
            val ktor =
                Ktorfit
                    .Builder()
                    .baseUrl("http://example.org/")
                    .httpClient(client)
                    .build()

            val typeData2 =
                TypeData2(
                    "de.jensklingenberg.ktorfit.Call<kotlin.String>",
                    typeInfo = typeInfo<Call<String>>(),
                    typeArgs =
                        listOf(
                            TypeData2("kotlin.String", typeInfo = typeInfo<String>()),
                        ),
                )

            val responseConverter = converter.responseConverter(typeData2, ktor)
            assertNotNull(responseConverter)
            val converted = responseConverter.convert(responseFunc) as Call<String>
            converted.onExecute(
                object : Callback<String> {
                    override fun onResponse(
                        call: String,
                        response: HttpResponse,
                    ) {
                        assertEquals(mockResponseContent, call)
                    }

                    override fun onError(exception: Throwable) {
                    }
                },
            )
            delay(100)
        }
}
