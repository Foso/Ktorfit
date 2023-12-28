package de.jensklingenberg.ktorfit.reqBuilderExtension

import de.jensklingenberg.ktorfit.model.ParameterData
import de.jensklingenberg.ktorfit.model.ReturnTypeData
import de.jensklingenberg.ktorfit.model.annotations.HttpMethod
import de.jensklingenberg.ktorfit.model.annotations.HttpMethodAnnotation
import de.jensklingenberg.ktorfit.model.annotations.ParameterAnnotation.Path
import de.jensklingenberg.ktorfit.model.annotations.ParameterAnnotation.Url
import org.junit.Assert.assertEquals
import org.junit.Test

class UrlArgumentTextKtTest {

    @Test
    fun testWithoutUrlAnnotation() {
        val parameterData = ParameterData("test1", ReturnTypeData("String", null))
        val params = listOf(parameterData)
        val text = getUrlCode(params, HttpMethodAnnotation("posts", HttpMethod.GET), "")
        val expected = "url{\n" +
                "takeFrom(_converter.baseUrl + \"posts\")\n" +
                "}".trimMargin()
        assertEquals(
            expected, text
        )
    }

    @Test
    fun testWithPathAndUrlAnnotation() {
        val urlAnnotation = Url
        val parameterData =
            ParameterData("test1", ReturnTypeData("String", null), annotations = listOf(urlAnnotation))
        val params = listOf(parameterData)
        val text = getUrlCode(params, HttpMethodAnnotation("posts", HttpMethod.GET), "")
        assertEquals(
            "url{\n" +
                    "takeFrom((_converter.baseUrl.takeIf{ !test1.startsWith(\"http\")} ?: \"\") + \"posts\")\n" +
                    "}",
            text
        )
    }

    @Test
    fun testWithUrlAnnotation() {
        val urlAnnotation = Url
        val parameterData =
            ParameterData("test1", ReturnTypeData("String", null), annotations = listOf(urlAnnotation))
        val params = listOf(parameterData)
        val text = getUrlCode(params, HttpMethodAnnotation("", HttpMethod.GET), "")
        val expected = String.format(
            "url{\n" +
                    "takeFrom((_converter.baseUrl.takeIf{ !test1.startsWith(\"http\")} ?: \"\") + \"%s{test1}\")\n" +
                    "}",
            "$"
        )
        assertEquals(expected, text)
    }

    @Test
    fun testWithoutPathAnnotation() {
        val parameterData = ParameterData("test1", ReturnTypeData("String", null))
        val params = listOf(parameterData)
        val text = getUrlCode(params, HttpMethodAnnotation("", HttpMethod.GET), "")
        assertEquals("url{\ntakeFrom(_converter.baseUrl + \"\")\n}", text)
    }

    @Test
    fun testWithPathAnnotation() {
        val path = Path("testValue")
        val parameterData =
            ParameterData("test1", ReturnTypeData("String", null), annotations = listOf(path))
        val params = listOf(parameterData)
        val text = getUrlCode(params, HttpMethodAnnotation("user/{testValue}", HttpMethod.GET), "")
        assertEquals(
            """url{
takeFrom(_converter.baseUrl + "user/$/{"$/test1".encodeURLPath()}")
}""".replace("$/", "$"),
            text
        )
    }
}