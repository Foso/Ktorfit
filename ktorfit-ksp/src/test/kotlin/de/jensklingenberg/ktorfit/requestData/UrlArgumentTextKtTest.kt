package de.jensklingenberg.ktorfit.requestData

import de.jensklingenberg.ktorfit.model.ParameterData
import de.jensklingenberg.ktorfit.model.ReturnTypeData
import de.jensklingenberg.ktorfit.model.annotations.HttpMethod
import de.jensklingenberg.ktorfit.model.annotations.HttpMethodAnnotation
import de.jensklingenberg.ktorfit.model.annotations.Path
import de.jensklingenberg.ktorfit.model.annotations.Url
import de.jensklingenberg.ktorfit.reqBuilderExtension.getUrlCode
import org.junit.Assert
import org.junit.Test

class UrlArgumentTextKtTest {

    @Test
    fun testWithoutUrlAnnotation() {
        val parameterData = ParameterData("test1", ReturnTypeData("String", "kotlin.String", null))
        val params = listOf(parameterData)
        val text = getUrlCode(params, HttpMethodAnnotation("GET", HttpMethod.GET), "")
        Assert.assertEquals("url(ktorfitClient.baseUrl + \"GET\")", text)
    }

    @Test
    fun testWithPathAndUrlAnnotation() {
        val urlAnnotation = Url
        val parameterData =
            ParameterData("test1", ReturnTypeData("String", "kotlin.String", null), annotations = listOf(urlAnnotation))
        val params = listOf(parameterData)
        val text = getUrlCode(params, HttpMethodAnnotation("posts", HttpMethod.GET), "")
        Assert.assertEquals(
            "url((ktorfitClient.baseUrl.takeIf{ !test1.startsWith(\"http\")} ?: \"\") + \"posts\")",
            text
        )
    }

    @Test
    fun testWithUrlAnnotation() {
        val urlAnnotation = Url
        val parameterData =
            ParameterData("test1", ReturnTypeData("String", "kotlin.String", null), annotations = listOf(urlAnnotation))
        val params = listOf(parameterData)
        val text = getUrlCode(params, HttpMethodAnnotation("", HttpMethod.GET), "")
        val expected = String.format(
            "url((ktorfitClient.baseUrl.takeIf{ !test1.startsWith(\"http\")} ?: \"\") + \"%s{test1}\")",
            "$"
        )
        Assert.assertEquals(expected, text)
    }

    @Test
    fun testWithoutPathAnnotation() {
        val parameterData = ParameterData("test1", ReturnTypeData("String", "kotlin.String", null))
        val params = listOf(parameterData)
        val text = getUrlCode(params, HttpMethodAnnotation("", HttpMethod.GET), "")
        Assert.assertEquals("url(ktorfitClient.baseUrl + \"\")", text)
    }

    @Test
    fun testWithPathAnnotation() {
        val path = Path("testValue")
        val parameterData =
            ParameterData("test1", ReturnTypeData("String", "kotlin.String", null), annotations = listOf(path))
        val params = listOf(parameterData)
        val text = getUrlCode(params, HttpMethodAnnotation("user/{testValue}", HttpMethod.GET), "")
        Assert.assertEquals(
            """url(ktorfitClient.baseUrl + "user/ä{"ätest1".encodeURLPath()}")""".replace("ä", "$"),
            text
        )
    }
}