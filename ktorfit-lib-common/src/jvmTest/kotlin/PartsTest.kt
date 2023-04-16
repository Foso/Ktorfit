import de.jensklingenberg.ktorfit.Ktorfit
import de.jensklingenberg.ktorfit.internal.InternalKtorfitApi
import de.jensklingenberg.ktorfit.internal.KtorfitClient
import de.jensklingenberg.ktorfit.internal.RequestData
import de.jensklingenberg.ktorfit.internal.TypeData
import io.ktor.client.*
import io.ktor.client.request.*
import io.ktor.client.request.forms.*
import io.ktor.util.reflect.*
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertTrue
import org.junit.Test

@OptIn(InternalKtorfitApi::class)
class PartsTest {

    @Test
    fun testPartsWithString() {

        val engine = object : TestEngine() {
            override fun getRequestData(data: HttpRequestData) {
                assertTrue((data.body is MultiPartFormDataContent))
            }
        }

        val ktorfit = Ktorfit.Builder().baseUrl("http://www.test.de/").httpClient(HttpClient(engine)).build()
        runBlocking {
            val requestData = RequestData(
                method = "GET",
                relativeUrl = "",
                returnTypeData = TypeData("kotlin.String"),
                parts = mapOf("description" to "test", "description2" to "test"),
                requestTypeInfo = typeInfo<String>(), returnTypeInfo = typeInfo<String>()
            )
            KtorfitClient(ktorfit).suspendRequest<String, String>(requestData)
        }
    }


}


