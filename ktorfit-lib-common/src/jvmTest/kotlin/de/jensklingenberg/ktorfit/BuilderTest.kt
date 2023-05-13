package de.jensklingenberg.ktorfit

import de.jensklingenberg.ktorfit.Strings.Companion.BASE_URL_REQUIRED
import de.jensklingenberg.ktorfit.http.GET
import de.jensklingenberg.ktorfit.http.Url
import de.jensklingenberg.ktorfit.internal.InternalKtorfitApi
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


interface BuilderTestApi{

    @GET("posts")
    suspend fun checkIfBaseUrlIsSetWhenUrlCheckIsDisabled(): String
}

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
            assert(illegal.message == BASE_URL_REQUIRED)
        }

    }

    @Test
    fun whenBaseUrlDontStartsWithHttpThrowError() {
        try {
            Ktorfit.Builder().baseUrl("http://www.example.com/").build()
        } catch (illegal: IllegalStateException) {
            assert(illegal.message == Strings.EXPECTED_URL_SCHEME)
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
           ktorfit.create<BuilderTestApi>(_BuilderTestApiImpl()).checkIfBaseUrlIsSetWhenUrlCheckIsDisabled()
        }
    }
}