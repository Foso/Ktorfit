package de.jensklingenberg.ktorfit

import de.jensklingenberg.ktorfit.Strings.Companion.BASE_URL_NEEDS_TO_END_WITH
import de.jensklingenberg.ktorfit.Strings.Companion.BASE_URL_REQUIRED
import de.jensklingenberg.ktorfit.http.GET
import io.ktor.client.HttpClient
import io.ktor.client.request.HttpRequestData
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Test

interface BuilderTestApi {
    @GET("posts")
    suspend fun checkIfBaseUrlIsSetWhenUrlCheckIsDisabled(): String
}

class BuilderTest {
    @Test
    fun whenBaseUrlNotEndingWithSlashThrowError() {
        try {
            Ktorfit
                .Builder()
                .baseUrl("http://www.example.com")
                .build()
        } catch (illegal: IllegalStateException) {
            assert(illegal.message == BASE_URL_NEEDS_TO_END_WITH)
        }
    }

    @Test
    fun whenBaseUrlIsEmptyThrowError() {
        try {
            Ktorfit
                .Builder()
                .baseUrl("")
                .build()
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

        val engine =
            object : TestEngine() {
                override fun getRequestData(data: HttpRequestData) {
                    assertEquals(expectedHTTPMethod, data.method.value)
                    assertEquals(expectedUrl, data.url.toString())
                }
            }

        val ktorfit =
            Ktorfit
                .Builder()
                .baseUrl(testBaseUrl, false)
                .httpClient(HttpClient(engine))
                .build()
        runBlocking {
            ktorfit.create<BuilderTestApi>(_BuilderTestApiProvider()).checkIfBaseUrlIsSetWhenUrlCheckIsDisabled()
        }
    }
}
