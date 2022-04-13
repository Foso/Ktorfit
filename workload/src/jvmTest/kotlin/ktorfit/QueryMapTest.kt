package ktorfit

import de.jensklingenberg.ktorfit.create
import de.jensklingenberg.ktorfit.demo.jvmClient
import io.ktor.client.plugins.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import org.junit.Assert
import org.junit.Test


class QueryMapTest {

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
        val exampleApi = de.jensklingenberg.ktorfit.demo.jvmKtorfit.create<QueryMapApi>()

        runBlocking {
            val response = exampleApi.testQueryMap(3, mapOf("hallo" to Test("hey huhu")))

            println("LI    " + response)


            delay(3000)
        }
    }



}