import de.jensklingenberg.ktorfit.Ktorfit
import de.jensklingenberg.ktorfit.internal.InternalKtorfitApi
import de.jensklingenberg.ktorfit.internal.KtorfitClient
import de.jensklingenberg.ktorfit.internal.RequestData
import de.jensklingenberg.ktorfit.internal.TypeData
import io.ktor.client.*
import io.ktor.client.request.*
import io.ktor.content.*
import io.ktor.http.*
import io.ktor.util.reflect.*
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Test

@OptIn(InternalKtorfitApi::class)
class BodyTest {

    @Test
    fun testBodyWithString() {

        val engine = object : TestEngine() {
            override fun getRequestData(data: HttpRequestData) {
                assertEquals(true, (data.body is TextContent))
                assertEquals("testBody", (data.body as TextContent).text)
            }
        }

        val ktorfit = Ktorfit.Builder().baseUrl("http://www.test.de/").httpClient(HttpClient(engine)).build()
        runBlocking {
            val ext: HttpRequestBuilder.() -> Unit = {
                method = HttpMethod.parse("GET")
                setBody("testBody")
            }
            val requestData = RequestData(
                relativeUrl = "",
                returnTypeData = TypeData("kotlin.String"),
                requestTypeInfo = typeInfo<String>(),
                returnTypeInfo = typeInfo<String>(),
                ktorfitRequestBuilder = ext
            )
            KtorfitClient(ktorfit).suspendRequest<String, String>(requestData)
        }
    }


}


