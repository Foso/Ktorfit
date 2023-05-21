package de.jensklingenberg.ktorfit.internal

import de.jensklingenberg.ktorfit.*
import de.jensklingenberg.ktorfit.converter.ConverterTestApi
import de.jensklingenberg.ktorfit.http.GET
import io.ktor.client.*
import io.ktor.client.request.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.runBlocking
import org.junit.Assert
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
                Assert.assertEquals(expectedHTTPMethod, data.method.value)
                Assert.assertEquals(expectedUrl, data.url.toString())
            }
        }

        val ktorfit = Ktorfit.Builder().baseUrl(testBaseUrl).httpClient(HttpClient(engine)).build()
        try {
            runBlocking {
                ktorfit.create<ClientTestApi>(_ClientTestApiImpl()).checkCorrectHttpMethod()

            }
        } catch (exception: Exception) {
            exception
        }
    }

    @Test
    fun throwExceptionWhenResponseConverterMissing() {

        try {
            val engine = object : TestEngine() {
                override fun getRequestData(data: HttpRequestData) {
                }
            }

            val ktorfit = Ktorfit.Builder().baseUrl("http://www.test.de/").httpClient(HttpClient(engine)).build()
            runBlocking {
                ktorfit.create<ClientTestApi>(_ClientTestApiImpl()).converterMissing()
            }

        } catch (exception: Exception) {
            Assert.assertTrue(exception is IllegalArgumentException)
            Assert.assertTrue(exception.message?.startsWith("Add a ResponseConverter") ?: false)
        }
    }

    @Test
    fun throwExceptionWhenRequestConverterMissing() {
        try {

            val ktorfit = Ktorfit.Builder().baseUrl("http://www.test.de/").build()

            val converted = (KtorfitClient(ktorfit) as Client).convertParameterType("4", String::class, Int::class)
            Assert.assertEquals(4, converted)

        } catch (ex: Exception) {
            Assert.assertTrue(ex is IllegalArgumentException)
            Assert.assertEquals(true, ex.message!!.contains("No RequestConverter found to convert "))
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
                Assert.assertEquals(expectedUrl, url)
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