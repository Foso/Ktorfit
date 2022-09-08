import de.jensklingenberg.ktorfit.Ktorfit
import de.jensklingenberg.ktorfit.internal.HeaderData
import de.jensklingenberg.ktorfit.internal.KtorfitClient
import de.jensklingenberg.ktorfit.internal.RequestData
import io.ktor.client.*
import io.ktor.client.request.*
import kotlinx.coroutines.runBlocking
import org.junit.Assert
import org.junit.Test

class HeaderTest {

    @Test
    fun testHeaderWithString() {

        val engine = object : TestEngine(){
            override fun getRequestData(data: HttpRequestData) {
                Assert.assertTrue(data.headers["Accept"] == "application/json")
            }
        }

        val ktorfit = Ktorfit.Builder().baseUrl("http://www.test.de/").httpClient(HttpClient(engine)).build()
        runBlocking {
            val requestData = RequestData(
                method = "GET", relativeUrl = "", qualifiedRawTypeName = "kotlin.String", headers = listOf(HeaderData("Accept","application/json"))
            )
            KtorfitClient(ktorfit).suspendRequest<String, String>(requestData)
        }
    }

    @Test
    fun testHeaderWithList() {
        /**
         * When Header value is a List of String it should result in a List with headers where every item of the list has the header key
         * null values are ignored
         */

        val testList = listOf("foo",null,"bar")

        val engine = object : TestEngine(){
            override fun getRequestData(data: HttpRequestData) {
                val acceptHeaderData = data.headers.getAll("Accept")
                Assert.assertTrue( acceptHeaderData == testList.filterNotNull() )
            }
        }

        val ktorfit = Ktorfit.Builder().baseUrl("http://www.test.de/").httpClient(HttpClient(engine)).build()
        runBlocking {
            val requestData = RequestData(
                method = "GET", relativeUrl = "", qualifiedRawTypeName = "kotlin.String", headers = listOf(HeaderData("Accept",testList))
            )
            KtorfitClient(ktorfit).suspendRequest<String, String>(requestData)
        }
    }

    @Test
    fun testHeaderWithArray() {
        /**
         * When Header value is an Array of String it should result in a List with headers where every item of the list has the header key
         * null values are ignored
         */

        val testList = arrayOf("foo",null,"bar")

        val engine = object : TestEngine(){
            override fun getRequestData(data: HttpRequestData) {
                val acceptHeaderData = data.headers.getAll("Accept")
                Assert.assertTrue( acceptHeaderData == testList.filterNotNull() )
            }
        }

        val ktorfit = Ktorfit.Builder().baseUrl("http://www.test.de/").httpClient(HttpClient(engine)).build()
        runBlocking {
            val requestData = RequestData(
                method = "GET", relativeUrl = "", qualifiedRawTypeName = "kotlin.String", headers = listOf(HeaderData("Accept",testList))
            )
            KtorfitClient(ktorfit).suspendRequest<String, String>(requestData)
        }
    }

    @Test
    fun testHeaderWithMap() {
        /**
         * A Header Map should result in a Header for every entry of the map
         */

        val testMap = mapOf("foo" to "bar","fizz" to "buzz")

        val engine = object : TestEngine(){
            override fun getRequestData(data: HttpRequestData) {
                testMap.forEach {
                    Assert.assertTrue( data.headers[it.key] == it.value )
                }
            }
        }

        val ktorfit = Ktorfit.Builder().baseUrl("http://www.test.de/").httpClient(HttpClient(engine)).build()
        runBlocking {
            val requestData = RequestData(
                method = "GET", relativeUrl = "", qualifiedRawTypeName = "kotlin.String", headers = listOf(HeaderData("",testMap))
            )
            KtorfitClient(ktorfit).suspendRequest<String, String>(requestData)
        }
    }


}


