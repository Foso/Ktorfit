import de.jensklingenberg.ktorfit.Ktorfit
import de.jensklingenberg.ktorfit.internal.KtorfitClient
import de.jensklingenberg.ktorfit.internal.RequestData
import io.ktor.client.*
import io.ktor.client.request.*
import io.ktor.content.*
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertTrue
import org.junit.Test

class BodyTest {

    @Test
    fun testBodyWithString() {

        val engine = object : TestEngine() {
            override fun getRequestData(data: HttpRequestData) {
                assertTrue( (data.body is TextContent))
                assertTrue( (data.body as TextContent).text == "testBody")

            }
        }

        val ktorfit = Ktorfit.Builder().baseUrl("www.test.de/").httpClient(HttpClient(engine)).build()
        runBlocking {
            val requestData = RequestData(
                method = "GET",
                relativeUrl = "",
                qualifiedRawTypeName = "kotlin.String",
                bodyData = "testBody"
            )
            KtorfitClient(ktorfit).suspendRequest<String, String>(requestData)
        }
    }


}


