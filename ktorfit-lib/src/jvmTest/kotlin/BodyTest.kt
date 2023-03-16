import de.jensklingenberg.ktorfit.Ktorfit
import de.jensklingenberg.ktorfit.internal.*
import de.jensklingenberg.ktorfit.internal.KtorfitClient
import io.ktor.client.*
import io.ktor.client.request.*
import io.ktor.content.*
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
            val requestData = RequestData(
                method = "GET",
                relativeUrl = "",
                returnTypeData = TypeData("kotlin.String"),
                bodyData = BodyData("testBody", typeInfo<String>()),
                returnTypeInfo = typeInfo<String>(),
                requestTypeInfo = typeInfo<String>()
            )
            KtorfitClient(ktorfit).suspendRequest<String, String>(requestData)
        }
    }


}


