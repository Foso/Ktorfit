package de.jensklingenberg.ktorfit

import de.jensklingenberg.ktorfit.http.Body
import de.jensklingenberg.ktorfit.http.POST
import io.ktor.client.HttpClient
import io.ktor.client.request.HttpRequestData
import io.ktor.content.TextContent
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

interface BodyTestApi {
    @POST("example")
    suspend fun testBody(
        @Body body: String,
    )
}

class BodyTest {
    @Test
    fun testBodyWithString() {
        val engine =
            object : TestEngine() {
                override fun getRequestData(data: HttpRequestData) {
                    assertTrue((data.body is TextContent))
                    assertEquals("testBody", (data.body as TextContent).text)

                    return
                }
            }

        try {
            val ktorfit =
                Ktorfit
                    .Builder()
                    .baseUrl("http://localhost/")
                    .httpClient(HttpClient(engine))
                    .build()

            runBlocking {
                ktorfit.create(_BodyTestApiProvider()).testBody("testBody")
            }
        } catch (ex: Exception) {
        }
    }
}
