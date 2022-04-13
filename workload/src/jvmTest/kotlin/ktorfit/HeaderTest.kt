package ktorfit

import de.jensklingenberg.ktorfit.create
import de.jensklingenberg.ktorfit.demo.jvmClient
import io.ktor.client.plugins.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import org.junit.Assert
import org.junit.Test


class HeaderTest {

    @Test
    fun testWithArray() {

        jvmClient.plugin(HttpSend).intercept { request ->
            val originalCall = execute(request)
            if (originalCall.response.status.value !in 100..399) {
                execute(request)
            } else {
                Assert.assertEquals("GET", originalCall.request.method.value)
                Assert.assertEquals("huhu=na%2520%2520%2520ud&huhu=do",originalCall.request.url.encodedQuery)
                originalCall
            }
        }
        val exampleApi = de.jensklingenberg.ktorfit.demo.jvmKtorfit.create<HeaderTestApi>()

        runBlocking {
            val response = exampleApi.testHeaderWithArray(3, arrayOf("na   ud", null, "do"))

            println("LI    " + response)


            delay(3000)
        }
    }

    @Test
    fun testWithList() {

        jvmClient.plugin(HttpSend).intercept { request ->
            val originalCall = execute(request)
            if (originalCall.response.status.value !in 100..399) {
                execute(request)
            } else {
                Assert.assertEquals("GET", originalCall.request.method.value)
                Assert.assertEquals("huhu=na%2520%2520%2520ud&huhu=do",originalCall.request.url.encodedQuery)
                originalCall
            }
        }
        val exampleApi = de.jensklingenberg.ktorfit.demo.jvmKtorfit.create<HeaderTestApi>()

        runBlocking {
             exampleApi.testHeaderWithList(3, listOf("na   ud", null, "do"))
        delay(3000)
        }
    }

    @Test
    fun testWithHeadersMap() {

        jvmClient.plugin(HttpSend).intercept { request ->
            val originalCall = execute(request)
            if (originalCall.response.status.value !in 100..399) {
                execute(request)
            } else {
                Assert.assertEquals("GET", originalCall.request.method.value)
                Assert.assertEquals("huhu=na%2520%2520%2520ud&huhu=do",originalCall.request.url.encodedQuery)
                originalCall
            }
        }
        val exampleApi = de.jensklingenberg.ktorfit.demo.jvmKtorfit.create<HeaderTestApi>()

        runBlocking {
            exampleApi.testHeaderMap(3, mapOf("hey" to "drop"))
            delay(3000)
        }
    }


}