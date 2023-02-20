package de.jensklingenberg.ktorfit.requestData

import de.jensklingenberg.ktorfit.model.ParameterData
import de.jensklingenberg.ktorfit.model.ReturnTypeData
import de.jensklingenberg.ktorfit.model.annotations.HttpMethod
import de.jensklingenberg.ktorfit.model.annotations.HttpMethodAnnotation
import de.jensklingenberg.ktorfit.model.annotations.Url
import org.junit.Assert
import org.junit.Test

class RelativeUrlArgumentTextKtTest {

    @Test
    fun testWithoutUrlAnnotation() {
        val parameterData = ParameterData("test1", ReturnTypeData("String", "kotlin.String"))
        val params = listOf(parameterData)
        val text = getRelativeUrlArgumentText(HttpMethodAnnotation("GET", HttpMethod.GET), params)
        Assert.assertEquals("relativeUrl=\"GET\"", text)
    }

    @Test
    fun testWithPathAndUrlAnnotation() {
        val urlAnnotation = Url
        val parameterData =
            ParameterData("test1", ReturnTypeData("String", "kotlin.String"), annotations = listOf(urlAnnotation))
        val params = listOf(parameterData)
        val text = getRelativeUrlArgumentText(HttpMethodAnnotation("posts", HttpMethod.GET), params)
        Assert.assertEquals("relativeUrl=\"posts\"", text)
    }

    @Test
    fun testWithUrlAnnotation() {
        val urlAnnotation = Url
        val parameterData =
            ParameterData("test1", ReturnTypeData("String", "kotlin.String"), annotations = listOf(urlAnnotation))
        val params = listOf(parameterData)
        val text = getRelativeUrlArgumentText(HttpMethodAnnotation("", HttpMethod.GET), params)
        val expected = String.format("relativeUrl=\"%s{test1}\"", "$")
        Assert.assertEquals(expected, text)
    }
}