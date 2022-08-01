import de.jensklingenberg.ktorfit.Ktorfit
import de.jensklingenberg.ktorfit.internal.KtorfitClient
import de.jensklingenberg.ktorfit.internal.QueryData
import de.jensklingenberg.ktorfit.internal.QueryType
import de.jensklingenberg.ktorfit.internal.RequestData
import io.ktor.client.*
import io.ktor.client.request.*
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertTrue
import org.junit.Test

class QueryTest {

    @Test
    fun testQueryWithString() {

        val testKey = "foo"
        val testValue = "bar fizz"
        val encodedTestValue = "bar+fizz"

        val engine = object : TestEngine() {
            override fun getRequestData(data: HttpRequestData) {

                assertTrue(data.url.encodedQuery == "$testKey=$encodedTestValue")
            }
        }

        val ktorfit = Ktorfit("www.test.de/", HttpClient(engine))
        runBlocking {
            val requestData = RequestData(
                method = "GET",
                relativeUrl = "",
                qualifiedRawTypeName = "kotlin.String",
                queries = listOf(QueryData(false, testKey, testValue, QueryType.QUERY))
            )
            KtorfitClient(ktorfit).suspendRequest<String, String>(requestData)
        }
    }

    @Test
    fun testQueryWithEncodedString() {

        val testKey = "foo"
        val testValue = "bar fizz"
        val expected = testValue
        val engine = object : TestEngine() {
            override fun getRequestData(data: HttpRequestData) {
                assertTrue(data.url.encodedQuery == "$testKey=$expected")
            }
        }

        val ktorfit = Ktorfit("www.test.de/", HttpClient(engine))
        runBlocking {
            val requestData = RequestData(
                method = "GET",
                relativeUrl = "",
                qualifiedRawTypeName = "kotlin.String",
                queries = listOf(QueryData(true, testKey, testValue, QueryType.QUERY))
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

        val engine = object : TestEngine() {
            override fun getRequestData(data: HttpRequestData) {
                assertTrue(data.url.parameters.getAll(testKey) == testValue.filterNotNull())
            }
        }

        val ktorfit = Ktorfit("www.test.de/", HttpClient(engine))
        runBlocking {
            val requestData = RequestData(
                method = "GET",
                relativeUrl = "",
                qualifiedRawTypeName = "kotlin.String",
                queries = listOf(QueryData(false, testKey, testValue, QueryType.QUERY))
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

        val engine = object : TestEngine() {
            override fun getRequestData(data: HttpRequestData) {
                assertTrue(data.url.parameters.getAll(testKey) == testValue.filterNotNull())
            }
        }

        val ktorfit = Ktorfit("www.test.de/", HttpClient(engine))
        runBlocking {
            val requestData = RequestData(
                method = "GET",
                relativeUrl = "",
                qualifiedRawTypeName = "kotlin.String",
                queries = listOf(QueryData(false, testKey, testValue, QueryType.QUERY))
            )
            KtorfitClient(ktorfit).suspendRequest<String, String>(requestData)
        }
    }

    @Test
    fun testQueryNameWithString() {

        val testKey = "foo"
        val testValue = "bar fizz"
        val encodedTestValue = "bar%20fizz"

        val engine = object : TestEngine() {
            override fun getRequestData(data: HttpRequestData) {
                assertTrue(data.url.encodedQuery == encodedTestValue)
            }
        }

        val ktorfit = Ktorfit("www.test.de/", HttpClient(engine))
        runBlocking {
            val requestData = RequestData(
                method = "GET",
                relativeUrl = "",
                qualifiedRawTypeName = "kotlin.String",
                queries = listOf(QueryData(false, testKey, testValue, QueryType.QUERYNAME))
            )
            KtorfitClient(ktorfit).suspendRequest<String, String>(requestData)
        }
    }

    @Test
    fun testQueryNameWithStringList() {

        val testKey = "foo"
        val testValue = listOf("foo", null, "bar fizz")

        val engine = object : TestEngine() {
            override fun getRequestData(data: HttpRequestData) {
                assertTrue(data.url.encodedQuery == "foo&bar%20fizz")
            }
        }

        val ktorfit = Ktorfit("www.test.de/", HttpClient(engine))
        runBlocking {
            val requestData = RequestData(
                method = "GET",
                relativeUrl = "",
                qualifiedRawTypeName = "kotlin.String",
                queries = listOf(QueryData(false, testKey, testValue, QueryType.QUERYNAME))
            )
            KtorfitClient(ktorfit).suspendRequest<String, String>(requestData)
        }
    }

    @Test
    fun testQueryMap() {

        val testKey = "foo"
        val testMap = mapOf("foo" to "bar", "fizz bar" to "buzz")

        val engine = object : TestEngine() {
            override fun getRequestData(data: HttpRequestData) {
                assertTrue(data.url.encodedQuery == "foo=bar&fizz%20bar=buzz")
            }
        }

        val ktorfit = Ktorfit("www.test.de/", HttpClient(engine))
        runBlocking {
            val requestData = RequestData(
                method = "GET",
                relativeUrl = "",
                qualifiedRawTypeName = "kotlin.String",
                queries = listOf(QueryData(false, testKey, testMap, QueryType.QUERYMAP))
            )
            KtorfitClient(ktorfit).suspendRequest<String, String>(requestData)
        }
    }

}


