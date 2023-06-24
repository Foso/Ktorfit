package de.jensklingenberg.ktorfit.converter

import de.jensklingenberg.ktorfit.Ktorfit
import de.jensklingenberg.ktorfit.TestStringToIntRequestConverter
import de.jensklingenberg.ktorfit.internal.Client
import de.jensklingenberg.ktorfit.internal.KtorfitClient

import kotlin.test.Test
import kotlin.test.assertEquals

class RequestParameterConverterTest {

    @Test
    fun testRequestConverter() {

        val ktorfit =
            Ktorfit.Builder().baseUrl("http://www.test.de/").requestConverter(TestStringToIntRequestConverter()).build()

        val converted = (KtorfitClient(ktorfit) as Client).convertParameterType("4", String::class, Int::class)
        assertEquals(4, converted)

    }



}