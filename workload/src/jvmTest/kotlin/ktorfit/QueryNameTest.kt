package ktorfit
import de.jensklingenberg.ktorfit.create
import de.jensklingenberg.ktorfit.demo.TestApi
import de.jensklingenberg.ktorfit.demo.jvmClient
import io.ktor.client.plugins.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import org.junit.Assert
import org.junit.Test


class QueryNameTest {

    @Test
    fun testQueryName() {

        jvmClient.plugin(HttpSend).intercept { request ->
            val originalCall = execute(request)
            if (originalCall.response.status.value !in 100..399) {
                execute(request)
            } else {
                Assert.assertEquals("GET", originalCall.request.method.value)
                originalCall
            }
        }
        val exampleApi = de.jensklingenberg.ktorfit.demo.jvmKtorfit.create<TestApi2>()

        runBlocking {
            exampleApi.testQueryName(3,"testName")
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
                Assert.assertEquals("nau%20%20d&do",originalCall.request.url.encodedQuery)
                originalCall
            }
        }
        val exampleApi = de.jensklingenberg.ktorfit.demo.jvmKtorfit.create<TestApi2>()

        runBlocking {
            exampleApi.testQueryNameList(3, listOf("nau  d", null, "do"))
            delay(3000)
        }
    }
}