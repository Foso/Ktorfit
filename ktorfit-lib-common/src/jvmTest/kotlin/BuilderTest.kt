import de.jensklingenberg.ktorfit.Ktorfit
import de.jensklingenberg.ktorfit.Strings.Companion.EXPECTED_URL_SCHEME
import de.jensklingenberg.ktorfit.internal.InternalKtorfitApi
import de.jensklingenberg.ktorfit.internal.KtorfitClient
import de.jensklingenberg.ktorfit.internal.RequestData
import de.jensklingenberg.ktorfit.internal.TypeData
import io.ktor.client.*
import io.ktor.client.request.*
import io.ktor.util.reflect.*
import kotlinx.coroutines.runBlocking
import org.junit.Assert
import org.junit.Test

@OptIn(InternalKtorfitApi::class)
class BuilderTest {

    @Test
    fun whenBaseUrlNotEndingWithSlashThrowError() {
        try {
           Ktorfit.Builder().baseUrl("http://www.example.com").build()
        } catch (illegal: IllegalStateException) {
            assert(illegal.message == "Base URL needs to end with /")
        }
    }

    @Test
    fun whenBaseUrlIsEmptyThrowError() {
        try {
            Ktorfit.Builder().baseUrl("").build()
        } catch (illegal: IllegalStateException) {
            assert(illegal.message == "Base URL required")
        }

    }

    @Test
    fun whenBaseUrlDontStartsWithHttpThrowError() {
        try {
            Ktorfit.Builder().baseUrl("http://www.example.com/").build()
        } catch (illegal: IllegalStateException) {
            assert(illegal.message == EXPECTED_URL_SCHEME)
        }

    }

    @Test
    fun checkIfCorrectHttpMethodIsSet() {
        val testBaseUrl = "http://www.example.com/"
        val testRelativeUrl = "posts"
        val expectedUrl = testBaseUrl + testRelativeUrl
        val expectedHTTPMethod = "GET"

        val engine = object : TestEngine() {
            override fun getRequestData(data: HttpRequestData) {
                Assert.assertEquals(expectedHTTPMethod, data.method.value)
                Assert.assertEquals(expectedUrl, data.url.toString())

            }
        }

        val ktorfit = Ktorfit.Builder().baseUrl(testBaseUrl).httpClient(HttpClient(engine)).build()
        runBlocking {
            val requestData = RequestData(
                method = expectedHTTPMethod,
                relativeUrl = testRelativeUrl,
                returnTypeData = TypeData("kotlin.String"),
                requestTypeInfo = typeInfo<String>(),
                returnTypeInfo = typeInfo<String>()
            )
            KtorfitClient(ktorfit).suspendRequest<String, String>(requestData)
        }


    }

    @Test
    fun checkIfBaseUrlIsSetWhenUrlCheckIsDisabled() {
        val testBaseUrl = "http://www.example.com"
        val testRelativeUrl = "posts"
        val expectedUrl = testBaseUrl + testRelativeUrl
        val expectedHTTPMethod = "GET"

        val engine = object : TestEngine() {
            override fun getRequestData(data: HttpRequestData) {
                Assert.assertEquals(expectedHTTPMethod, data.method.value)
                Assert.assertEquals(expectedUrl, data.url.toString())
            }
        }

        val ktorfit = Ktorfit.Builder().baseUrl(testBaseUrl, false).httpClient(HttpClient(engine)).build()
        runBlocking {
            val requestData = RequestData(
                method = expectedHTTPMethod,
                relativeUrl = testRelativeUrl,
                returnTypeData = TypeData("kotlin.String"),
                requestTypeInfo = typeInfo<String>(),
                returnTypeInfo = typeInfo<String>()
            )
            KtorfitClient(ktorfit).suspendRequest<String, String>(requestData)
        }


    }


    @Test
    fun whenUrlValueContainsBaseUrl_ThenRemoveBaseUrl() {
        val testBaseUrl = "http://www.example.com/"
        val testRelativeUrl = "posts"
        val expectedUrl = testBaseUrl + testRelativeUrl
        val engine = object : TestEngine() {
            override fun getRequestData(data: HttpRequestData) {
                val url = data.url.toString().removePrefix("http://localhost/")
                Assert.assertEquals(expectedUrl, url)
            }
        }

        val ktorfit = Ktorfit.Builder().baseUrl(testBaseUrl).httpClient(HttpClient(engine)).build()
        runBlocking {
            val requestData = RequestData(
                method = "GET",
                relativeUrl = testRelativeUrl,
                returnTypeData = TypeData("kotlin.String"),
                requestTypeInfo = typeInfo<String>(),
                returnTypeInfo = typeInfo<String>()
            )
            KtorfitClient(ktorfit).suspendRequest<String, String>(requestData)
        }


    }

    @Test
    fun whenUrlValueContainsAUrl_ThenUseItAsRequestUrl() {

        val baseUrl = "http://www.test.de/"
        val relativeUrl = "http://www.example.com/posts"

        val expectedRequestUrl = "http://www.example.com/posts"

        val engine = object : TestEngine() {
            override fun getRequestData(data: HttpRequestData) {
                val url = data.url.toString().removePrefix("http://localhost/")
                Assert.assertEquals(expectedRequestUrl, url)
            }
        }

        val ktorfit = Ktorfit.Builder().baseUrl(baseUrl).httpClient(HttpClient(engine)).build()
        runBlocking {
            val requestData = RequestData(
                method = "GET",
                relativeUrl = relativeUrl,
                returnTypeData = TypeData("kotlin.String"),
                requestTypeInfo = typeInfo<String>(),
                returnTypeInfo = typeInfo<String>()
            )
            KtorfitClient(ktorfit).suspendRequest<String, String>(requestData)
        }


    }

}

