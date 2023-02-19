import de.jensklingenberg.ktorfit.Ktorfit
import de.jensklingenberg.ktorfit.internal.*
import io.ktor.client.*
import io.ktor.client.request.*
import io.ktor.util.reflect.*
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
                val requestData = RequestData(
                    method = "GET",
                    relativeUrl = "",
                    returnTypeData = TypeData("kotlinx.coroutines.flow.Flow"),
                    headers = listOf(DH("Accept", "application/json")),
                    requestTypeInfo = typeInfo<String>(), returnTypeInfo = typeInfo<String>()
                )
                KtorfitClient(ktorfit).request<Flow<String>, String>(requestData)
            }
        } catch (exception: Exception) {
            Assert.assertTrue(exception is IllegalArgumentException)

        }
    }
}


