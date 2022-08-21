import de.jensklingenberg.ktorfit.Ktorfit
import de.jensklingenberg.ktorfit.internal.HeaderData
import de.jensklingenberg.ktorfit.internal.KtorfitClient
import de.jensklingenberg.ktorfit.internal.RequestData
import io.ktor.client.*
import io.ktor.client.request.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.runBlocking
import org.junit.Assert
import org.junit.Test

class ConverterTest {

    @Test
    fun throwExceptionWhenConverterMissing() {

        try {
            val engine = object : TestEngine() {
                override fun getRequestData(data: HttpRequestData) {
                }
            }

            val ktorfit = Ktorfit.Builder().baseUrl("www.test.de/").httpClient(HttpClient(engine)).build()
            runBlocking {
                val requestData = RequestData(
                    method = "GET",
                    relativeUrl = "",
                    qualifiedRawTypeName = "kotlinx.coroutines.flow.Flow",
                    headers = listOf(HeaderData("Accept", "application/json"))
                )
                KtorfitClient(ktorfit).request<Flow<String>, String>(requestData)
            }
        } catch (exception: Exception) {
            Assert.assertTrue(exception is IllegalArgumentException)

        }
    }


}


