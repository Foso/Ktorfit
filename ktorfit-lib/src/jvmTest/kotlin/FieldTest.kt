import de.jensklingenberg.ktorfit.Ktorfit
import de.jensklingenberg.ktorfit.internal.FieldData
import de.jensklingenberg.ktorfit.internal.FieldType
import de.jensklingenberg.ktorfit.internal.KtorfitClient
import de.jensklingenberg.ktorfit.internal.RequestData
import io.ktor.client.*
import io.ktor.client.request.*
import io.ktor.client.request.forms.*
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertTrue
import org.junit.Test

class FieldTest {

    @Test
    fun testFieldWithString() {

        val testKey = "foo"
        val testValue = "bar fizz"
        val encodedTestValue = "bar%20fizz"

        val engine = object : TestEngine() {
            override fun getRequestData(data: HttpRequestData) {
                assertTrue( (data.body as FormDataContent).formData[testKey] == encodedTestValue)
            }
        }

        val ktorfit = Ktorfit.Builder().baseUrl("http://www.test.de/").httpClient(HttpClient(engine)).build()
        runBlocking {
            val requestData = RequestData(
                method = "GET",
                relativeUrl = "",
                qualifiedRawTypeName = "kotlin.String",
                fields = listOf(FieldData(testKey, testValue, false, FieldType.FIELD)),
            )
            KtorfitClient(ktorfit).suspendRequest<String, String>(requestData)
        }
    }


}


