package de.jensklingenberg.ktorfit

import de.jensklingenberg.ktorfit.internal.Client
import de.jensklingenberg.ktorfit.internal.InternalKtorfitApi
import de.jensklingenberg.ktorfit.internal.KtorfitClient
import org.junit.Assert
import org.junit.Test

class RequestParameterConverterTest {

    @Test
    fun testRequestConverter() {

        val ktorfit =
            Ktorfit.Builder().baseUrl("http://www.test.de/").requestConverter(TestStringToIntRequestConverter()).build()

        val converted = (KtorfitClient(ktorfit) as Client).convertParameterType("4", String::class, Int::class)
        Assert.assertEquals(4, converted)

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

}