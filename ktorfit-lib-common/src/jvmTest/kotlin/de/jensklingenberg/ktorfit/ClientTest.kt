package de.jensklingenberg.ktorfit

import de.jensklingenberg.ktorfit.http.GET
import de.jensklingenberg.ktorfit.internal.KtorfitClient
import de.jensklingenberg.ktorfit.internal.RequestData
import de.jensklingenberg.ktorfit.internal.TypeData
import io.ktor.client.*
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.util.reflect.*
import kotlinx.coroutines.runBlocking
import org.junit.Assert
import org.junit.Test

interface ClientTestApi {

    @GET("posts")
    suspend fun checkCorrectHttpMethod(): String

    @GET("http://www.example.com/posts")
    suspend fun whenUrlValueContainsBaseUrl_ThenRemoveBaseUrl()
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