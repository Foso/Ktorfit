import de.jensklingenberg.ktorfit.Ktorfit
import de.jensklingenberg.ktorfit.internal.KtorfitClient
import de.jensklingenberg.ktorfit.internal.RequestData
import io.ktor.client.*
import io.ktor.client.request.*
import io.ktor.client.request.forms.*
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertTrue
import org.junit.Test

class PartsTest {

    @Test
    fun testPartsWithString() {

        val engine = object : TestEngine() {
            override fun getRequestData(data: HttpRequestData) {
                assertTrue( (data.body is MultiPartFormDataContent))
            }
        }

        val ktorfit = Ktorfit.Builder().baseUrl("www.test.de/").httpClient(HttpClient(engine)).build()
        runBlocking {
            val requestData = RequestData(
                method = "GET",
                relativeUrl = "",
                qualifiedRawTypeName = "kotlin.String",
                parts = mapOf("description" to "test","description2" to "test")
            )
            KtorfitClient(ktorfit).suspendRequest<String, String>(requestData)
        }
    }


}


