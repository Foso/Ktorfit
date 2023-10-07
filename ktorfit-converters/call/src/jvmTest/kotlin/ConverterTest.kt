import de.jensklingenberg.ktorfit.Call
import de.jensklingenberg.ktorfit.Callback
import de.jensklingenberg.ktorfit.Ktorfit
import de.jensklingenberg.ktorfit.converter.builtin.CallConverterFactory
import de.jensklingenberg.ktorfit.internal.TypeData
import io.ktor.client.*
import io.ktor.client.engine.mock.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.util.reflect.*
import io.ktor.utils.io.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

class ConverterTest {

    @Test
    fun whenCallConverterIsUsedThenRespondSuccessful() = runBlocking {
        val mockResponseContent = """{"ip":"127.0.0.1"}"""

        val mockEngine = MockEngine { _ ->
            respond(
                content = ByteReadChannel(mockResponseContent),
                status = HttpStatusCode.OK,
                headers = headersOf(HttpHeaders.ContentType, "application/json")
            )
        }

        val client = HttpClient(mockEngine) {}

        val responseFunc = suspend { client.request("http://example.org/") }
        val converter = CallConverterFactory()
        val ktor =
            Ktorfit.Builder()
                .baseUrl("http://example.org/")
                .httpClient(client)
                .build()

        val typeData = TypeData(
            "de.jensklingenberg.ktorfit.Call<kotlin.String>",
            typeInfo = typeInfo<Call<String>>(),
            typeArgs = listOf(
                TypeData("kotlin.String", typeInfo = typeInfo<String>())
            )
        )

        val responseConverter = converter.responseConverter(typeData, ktor)
        assertNotNull(responseConverter)
        val converted = responseConverter.convert(responseFunc) as Call<String>
        converted.onExecute(object : Callback<String> {
            override fun onResponse(call: String, response: HttpResponse) {
                assertEquals(mockResponseContent, call)
            }

            override fun onError(exception: Throwable) {
                exception
            }

        })
        delay(100)
    }
}