import de.jensklingenberg.ktorfit.Ktorfit
import de.jensklingenberg.ktorfit.internal.HeaderData
import de.jensklingenberg.ktorfit.internal.KtorfitClient
import de.jensklingenberg.ktorfit.internal.RequestData
import io.ktor.client.*
import io.ktor.client.request.*
import kotlinx.coroutines.runBlocking
import org.junit.Assert
import org.junit.Test

class ConverterTest {

    @Test
    fun testHeaderWithString() {

        val engine = object : TestEngine(){
            override fun getRequestData(data: HttpRequestData) {
                Assert.assertTrue(data.headers["Accept"] == "application/json")
            }

        }

        val ktorfit = Ktorfit.Builder().baseUrl("www.test.de/").httpClient(HttpClient(engine)).build()
        runBlocking {
            val requestData = RequestData(
                method = "GET", relativeUrl = "", qualifiedRawTypeName = "kotlin.String", headers = listOf(HeaderData("Accept","application/json"))
            )
            KtorfitClient(ktorfit).suspendRequest<String, String>(requestData)
        }
    }



}


