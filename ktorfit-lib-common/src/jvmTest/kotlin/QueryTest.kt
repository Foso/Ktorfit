import de.jensklingenberg.ktorfit.Ktorfit
import de.jensklingenberg.ktorfit.internal.*
import io.ktor.client.*
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.util.reflect.*
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

@OptIn(InternalKtorfitApi::class)
class QueryTest {

    @Test
    fun testQueryWithString() {
        val baseUrl = "http://www.test.de/"

        val testKey = "foo"
        val testValue = "bar fizz"
        val encodedTestValue = "bar+fizz"

        val engine = object : TestEngine() {
            override fun getRequestData(data: HttpRequestData) {

                assertTrue(data.url.encodedQuery == "$testKey=$encodedTestValue")
            }
        }

        val ktorfit = Ktorfit.Builder().baseUrl(baseUrl).httpClient(HttpClient(engine)).build()
        runBlocking {
            val ext : HttpRequestBuilder.()->Unit = {
                method = HttpMethod.parse("GET")
            }
            val requestData = RequestData(
                ktorfitRequestBuilder = ext,
                relativeUrl = "",
                returnTypeData = TypeData("kotlin.String"),
                queries = listOf(DH(testKey, testValue, false)),
                requestTypeInfo = typeInfo<String>(), returnTypeInfo = typeInfo<String>()
            )
            KtorfitClient(ktorfit).suspendRequest<String, String>(requestData)
        }
    }

    @Test
    fun testQueryWithNullableStringIsNull_IgnoreIt() {

        val testKey = "foo"
        val baseUrl = "http://www.test.de/"

        val engine = object : TestEngine() {
            override fun getRequestData(data: HttpRequestData) {
                assertTrue(data.url.encodedQuery == "")
            }
        }

        val ktorfit = Ktorfit.Builder().baseUrl(baseUrl).httpClient(HttpClient(engine)).build()
        runBlocking {
            val ext : HttpRequestBuilder.()->Unit = {
                method = HttpMethod.parse("GET")
            }
            val requestData = RequestData(
                ktorfitRequestBuilder = ext,
                relativeUrl = "",
                returnTypeData = TypeData("kotlin.String"),
                queries = listOf(DH(testKey, null, false)),
                requestTypeInfo = typeInfo<String>(), returnTypeInfo = typeInfo<String>()
            )
            KtorfitClient(ktorfit).suspendRequest<String, String>(requestData)
        }
    }

    @Test
    fun testQueryWithEncodedString() {
        val baseUrl = "http://www.test.de/"

        val testKey = "foo"
        val testValue = "bar fizz"
        val expected = testValue
        val engine = object : TestEngine() {
            override fun getRequestData(data: HttpRequestData) {
                assertTrue(data.url.encodedQuery == "$testKey=$expected")
            }
        }

        val ktorfit = Ktorfit.Builder().baseUrl(baseUrl).httpClient(HttpClient(engine)).build()
        runBlocking {
            val ext : HttpRequestBuilder.()->Unit = {
                method = HttpMethod.parse("GET")
            }
            val requestData = RequestData(
                ktorfitRequestBuilder = ext,
                relativeUrl = "",
                returnTypeData = TypeData("kotlin.String"),
                queries = listOf(DH(testKey, testValue, true)),
                requestTypeInfo = typeInfo<String>(), returnTypeInfo = typeInfo<String>()
            )
            KtorfitClient(ktorfit).suspendRequest<String, String>(requestData)
        }
    }

    @Test
    fun testQueryWithStringList() {
        /**
         * When query value is a List of String it should result in a query for each non-null entry.
         */
        val testKey = "foo"
        val testValue = listOf("foo", null, "bar")
        val baseUrl = "http://www.test.de/"
        val expectedValue = testValue.filterNotNull()
        val engine = object : TestEngine() {
            override fun getRequestData(data: HttpRequestData) {
                assertEquals(expectedValue, data.url.parameters.getAll(testKey))
            }
        }

        val ktorfit = Ktorfit.Builder().baseUrl(baseUrl).httpClient(HttpClient(engine)).build()
        runBlocking {
            val ext : HttpRequestBuilder.()->Unit = {
                method = HttpMethod.parse("GET")
            }
            val requestData = RequestData(
                ktorfitRequestBuilder = ext,
                relativeUrl = "",
                returnTypeData = TypeData("kotlin.String"),
                queries = listOf(DH(testKey, testValue, false)),
                requestTypeInfo = typeInfo<String>(), returnTypeInfo = typeInfo<String>()
            )
            KtorfitClient(ktorfit).suspendRequest<String, String>(requestData)
        }
    }

    @Test
    fun testQueryWithStringArray() {
        /**
         * When query value is a List of String it should result in a query for each non-null entry.
         */
        val testKey = "foo"
        val testValue = arrayOf("foo", null, "bar")
        val baseUrl = "http://www.test.de/"

        val engine = object : TestEngine() {
            override fun getRequestData(data: HttpRequestData) {
                assertTrue(data.url.parameters.getAll(testKey) == testValue.filterNotNull())
            }
        }

        val ktorfit = Ktorfit.Builder().baseUrl(baseUrl).httpClient(HttpClient(engine)).build()
        runBlocking {
            val ext : HttpRequestBuilder.()->Unit = {
                method = HttpMethod.parse("GET")
            }
            val requestData = RequestData(
                ktorfitRequestBuilder = ext,
                relativeUrl = "",
                returnTypeData = TypeData("kotlin.String"),
                queries = listOf(DH(testKey, testValue, false)),
                requestTypeInfo = typeInfo<String>(), returnTypeInfo = typeInfo<String>()
            )
            KtorfitClient(ktorfit).suspendRequest<String, String>(requestData)
        }
    }

    @Test
    fun testQueryNameWithString() {
        val baseUrl = "http://www.test.de/"
        val testKey = ""
        val testValue = "bar fizz"
        val encodedTestValue = "bar%20fizz"

        val engine = object : TestEngine() {
            override fun getRequestData(data: HttpRequestData) {
                assertTrue(data.url.encodedQuery == encodedTestValue)
            }
        }

        val ktorfit = Ktorfit.Builder().baseUrl(baseUrl).httpClient(HttpClient(engine)).build()
        runBlocking {
            val ext : HttpRequestBuilder.()->Unit = {
                method = HttpMethod.parse("GET")
            }
            val requestData = RequestData(
                ktorfitRequestBuilder = ext,
                relativeUrl = "",
                returnTypeData = TypeData("kotlin.String"),
                queries = listOf(DH(testKey, testValue, false)),
                requestTypeInfo = typeInfo<String>(), returnTypeInfo = typeInfo<String>()
            )
            KtorfitClient(ktorfit).suspendRequest<String, String>(requestData)
        }
    }

    @Test
    fun testQueryNameWithStringList() {
        val baseUrl = "http://www.test.de/"

        val testKey = ""
        val testValue = listOf("foo", null, "bar fizz")

        val engine = object : TestEngine() {
            override fun getRequestData(data: HttpRequestData) {
                assertTrue(data.url.encodedQuery == "foo&bar%20fizz")
            }
        }

        val ktorfit = Ktorfit.Builder().baseUrl(baseUrl).httpClient(HttpClient(engine)).build()
        runBlocking {
            val ext : HttpRequestBuilder.()->Unit = {
                method = HttpMethod.parse("GET")
            }
            val requestData = RequestData(
                ktorfitRequestBuilder = ext,
                relativeUrl = "",
                returnTypeData = TypeData("kotlin.String"),
                queries = listOf(DH(testKey, testValue, false)),
                requestTypeInfo = typeInfo<String>(), returnTypeInfo = typeInfo<String>()
            )
            KtorfitClient(ktorfit).suspendRequest<String, String>(requestData)
        }
    }

    @Test
    fun testQueryMap() {
        val baseUrl = "http://www.test.de/"
        val testKey = "foo"
        val testMap = mapOf("foo" to "bar", "fizz bar" to "buzz")

        val engine = object : TestEngine() {
            override fun getRequestData(data: HttpRequestData) {
                assertTrue(data.url.encodedQuery == "foo=bar&fizz%20bar=buzz")
            }
        }

        val ktorfit = Ktorfit.Builder().baseUrl(baseUrl).httpClient(HttpClient(engine)).build()
        runBlocking {
            val ext : HttpRequestBuilder.()->Unit = {
                method = HttpMethod.parse("GET")
            }
            val requestData = RequestData(
                ktorfitRequestBuilder = ext,
                relativeUrl = "",
                returnTypeData = TypeData("kotlin.String"),
                queries = listOf(DH(testKey, testMap, false)),
                requestTypeInfo = typeInfo<String>(), returnTypeInfo = typeInfo<String>()
            )
            KtorfitClient(ktorfit).suspendRequest<String, String>(requestData)
        }
    }

    @Test
    fun whenQueryMapContainsNullValues_SkipTheNullValues() {
        val baseUrl = "http://www.test.de/"
        val testKey = "foo"
        val testMap = mapOf("foo" to "bar", "fizz bar" to null)

        val engine = object : TestEngine() {
            override fun getRequestData(data: HttpRequestData) {
                assertTrue(data.url.encodedQuery == "foo=bar")
            }
        }

        val ktorfit = Ktorfit.Builder().baseUrl(baseUrl).httpClient(HttpClient(engine)).build()
        runBlocking {
            val ext : HttpRequestBuilder.()->Unit = {
                method = HttpMethod.parse("GET")
            }
            val requestData = RequestData(
                ktorfitRequestBuilder = ext,
                relativeUrl = "",
                returnTypeData = TypeData("kotlin.String"),
                queries = listOf(DH(testKey, testMap, false)),
                requestTypeInfo = typeInfo<String>(), returnTypeInfo = typeInfo<String>()
            )
            KtorfitClient(ktorfit).suspendRequest<String, String>(requestData)
        }
    }

}


