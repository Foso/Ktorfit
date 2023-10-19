package de.jensklingenberg.ktorfit.internal

import de.jensklingenberg.ktorfit.*
import de.jensklingenberg.ktorfit.http.GET
import io.ktor.client.*
import io.ktor.client.engine.mock.*
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.utils.io.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

interface ClientTestApi {

    @GET("posts")
    suspend fun checkCorrectHttpMethod(): String

    @GET("http://www.example.com/posts")
    suspend fun whenUrlValueContainsBaseUrl_ThenRemoveBaseUrl()

    @GET("posts")
    fun converterMissing(): Flow<String>
}

class ClientTest {

    @Test
    fun checkIfCorrectHttpMethodIsSet() {
        val testBaseUrl = "http://www.example.com/"
        val testRelativeUrl = "posts"
        val expectedUrl = testBaseUrl + testRelativeUrl
        val expectedHTTPMethod = "GET"

        val engine = object : TestEngine() {
            override fun getRequestData(data: HttpRequestData) {
                assertEquals(expectedHTTPMethod, data.method.value)
                assertEquals(expectedUrl, data.url.toString())
            }
        }

        val ktorfit = Ktorfit.Builder().baseUrl(testBaseUrl).httpClient(HttpClient(engine)).build()

        runBlocking {
            ktorfit.create<ClientTestApi>(_ClientTestApiImpl()).checkCorrectHttpMethod()
        }

    }

    @Test
    fun throwExceptionWhenResponseConverterMissing() {
        try {

            val mockResponseContent = """{"ip":"127.0.0.1"}"""

            val mockEngine = MockEngine { _ ->
                respond(
                    content = ByteReadChannel(mockResponseContent),
                    status = HttpStatusCode.OK,
                    headers = headersOf(HttpHeaders.ContentType, "application/json")
                )
            }

            val client = HttpClient(mockEngine) {}

            val ktorfit = Ktorfit.Builder().baseUrl("http://www.test.de/").httpClient(client).build()
            runBlocking {
                ktorfit.create<ClientTestApi>(_ClientTestApiImpl()).converterMissing()
            }

        } catch (exception: Exception) {
            assertTrue(exception is IllegalStateException)
            assertTrue(exception.message?.startsWith("Add a ResponseConverter") ?: false)
        }
    }

    @Test
    fun throwExceptionWhenRequestConverterMissing() {
        try {

            val ktorfit = Ktorfit.Builder().baseUrl("http://www.test.de/").build()

            val converted = (KtorfitClient(ktorfit) as Client).convertParameterType("4", String::class, Int::class)
            assertEquals(4, converted)

        } catch (ex: Exception) {
            assertTrue(ex is IllegalStateException)
            assertEquals(true, ex.message!!.contains("No RequestConverter found to convert "))
        }
    }

    @Test
    fun whenUrlValueContainsFullUrl_ThenRemoveBaseUrl() {
        val testBaseUrl = "http://www.example.com/"
        val testRelativeUrl = "posts"
        val expectedUrl = testBaseUrl + testRelativeUrl

        val engine = object : TestEngine() {
            override fun getRequestData(data: HttpRequestData) {
                val url = data.url.toString()
                assertEquals(expectedUrl, url)
            }
        }

        val ktorfit = Ktorfit.Builder().baseUrl("http://www.example1.com/").httpClient(HttpClient(engine)).build()
        try {
            runBlocking {
                ktorfit.create<ClientTestApi>(_ClientTestApiImpl()).whenUrlValueContainsBaseUrl_ThenRemoveBaseUrl()

            }
        } catch (ex: Exception) {

        }


    }

}