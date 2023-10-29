package de.jensklingenberg.ktorfit.converter

import de.jensklingenberg.ktorfit.Ktorfit
import de.jensklingenberg.ktorfit.TestEngine
import de.jensklingenberg.ktorfit.TestStringToIntRequestConverter
import de.jensklingenberg.ktorfit.internal.InternalKtorfitApi
import de.jensklingenberg.ktorfit.internal.KtorfitConverterHelper
import io.ktor.client.request.HttpRequestData
import kotlin.test.Test
import kotlin.test.assertEquals

class RequestParameterConverterTest {

    @OptIn(InternalKtorfitApi::class)
    @Test
    fun testRequestConverter() {

        val engine = object : TestEngine() {
            override fun getRequestData(data: HttpRequestData) {
            }
        }

        val ktorfit =
            Ktorfit.Builder().httpClient(engine).baseUrl("http://www.test.de/").requestConverter(TestStringToIntRequestConverter()).build()

        val converted = KtorfitConverterHelper(ktorfit).convertParameterType("4", String::class, Int::class)
        assertEquals(4, converted)

    }



}