import de.jensklingenberg.ktorfit.Ktorfit
import de.jensklingenberg.ktorfit.internal.*
import io.ktor.client.*
import io.ktor.client.request.*
import io.ktor.client.request.forms.*
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertTrue
import org.junit.Test

@OptIn(InternalKtorfitApi::class)
class FieldTest {

    @Test
    fun testFieldWithString() {
        val baseUrl = "http://www.test.de/"
        val testKey = "foo"
        val testValue = "bar fizz"
        val encodedTestValue = "bar fizz"

        val engine = object : TestEngine() {
            override fun getRequestData(data: HttpRequestData) {
                assertTrue((data.body as FormDataContent).formData[testKey] == encodedTestValue)
            }
        }

        val ktorfit = Ktorfit.Builder().baseUrl(baseUrl).httpClient(HttpClient(engine)).build()
        runBlocking {
            val requestData = RequestData(
                method = "GET",
                relativeUrl = "",
                returnTypeData = TypeData("kotlin.String"),
                fields = listOf(DH(testKey, testValue, false)),
            )
            KtorfitClient(ktorfit).suspendRequest<String, String>(requestData)
        }
    }

    @Test
    fun testFieldWithEncodedString() {
        val baseUrl = "http://www.test.de/"
        val testKey = "foo"
        val testValue = "bar fizz"
        val expected = testValue
        val engine = object : TestEngine() {
            override fun getRequestData(data: HttpRequestData) {
                assertTrue((data.body as FormDataContent).formData[testKey] == expected)
            }
        }

        val ktorfit = Ktorfit.Builder().baseUrl(baseUrl).httpClient(HttpClient(engine)).build()
        runBlocking {
            val requestData = RequestData(
                method = "GET",
                relativeUrl = "",
                returnTypeData = TypeData("kotlin.String"),
                fields = listOf(DH(testKey, testValue, true)),
            )
            KtorfitClient(ktorfit).suspendRequest<String, String>(requestData)
        }
    }

    @Test
    fun testFieldValueNull_IgnoreIt() {
        val baseUrl = "http://www.test.de/"

        val testKey = "foo"
        val testValue = null

        val engine = object : TestEngine() {
            override fun getRequestData(data: HttpRequestData) {
                assertTrue((data.body as FormDataContent).formData[testKey] == null)
            }
        }

        val ktorfit = Ktorfit.Builder().baseUrl(baseUrl).httpClient(HttpClient(engine)).build()
        runBlocking {
            val requestData = RequestData(
                method = "GET",
                relativeUrl = "",
                returnTypeData = TypeData("kotlin.String"),
                fields = listOf(DH(testKey, testValue, false)),
            )
            KtorfitClient(ktorfit).suspendRequest<String, String>(requestData)
        }
    }

    @Test
    fun whenFieldMapContainsNullValues_SkipTheNullValues() {
        val baseUrl = "http://www.test.de/"
        val testKey = "foo"
        val testMap = mapOf("foo" to "bar", "fizz bar" to null)

        val engine = object : TestEngine() {
            override fun getRequestData(data: HttpRequestData) {
                assertTrue((data.body as FormDataContent).formData["foo"] == "bar")
                assertTrue((data.body as FormDataContent).formData["fizz bar"] == null)
            }
        }

        val ktorfit = Ktorfit.Builder().baseUrl(baseUrl).httpClient(HttpClient(engine)).build()
        runBlocking {
            val requestData = RequestData(
                method = "POST",
                relativeUrl = "",
                returnTypeData = TypeData("kotlin.String"),
                fields = listOf(DH(testKey, testMap, false))
            )
            KtorfitClient(ktorfit).suspendRequest<String, String>(requestData)
        }
    }


}


