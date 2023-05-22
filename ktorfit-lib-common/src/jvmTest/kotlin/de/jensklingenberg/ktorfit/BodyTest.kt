package de.jensklingenberg.ktorfit

import de.jensklingenberg.ktorfit.http.Body
import de.jensklingenberg.ktorfit.http.POST
import io.ktor.client.*
import io.ktor.client.request.*
import io.ktor.content.*
import kotlinx.coroutines.runBlocking
import org.junit.Assert
import org.junit.Test

interface BodyTestApi {

    @POST("example")
    suspend fun testBody(@Body body: String)
}

class BodyTest {

    @Test
    fun testBodyWithString() {

        val engine = object : TestEngine() {
            override fun getRequestData(data: HttpRequestData) {
                Assert.assertEquals(true, (data.body is TextContent))
                Assert.assertEquals("testBody", (data.body as TextContent).text)

                return
            }
        }

        try {
            val ktorfit = Ktorfit
                .Builder()
                .baseUrl("http://localhost/")
                .httpClient(HttpClient(engine))
                .build()

            runBlocking {
                ktorfit.create<BodyTestApi>(_BodyTestApiImpl()).testBody("testBody")
            }
        } catch (ex: Exception) {

        }
    }


}