package de.jensklingenberg.ktorfit.requestData

import de.jensklingenberg.ktorfit.model.ParameterData
import de.jensklingenberg.ktorfit.model.ReturnTypeData
import de.jensklingenberg.ktorfit.model.annotations.HeaderMap
import de.jensklingenberg.ktorfit.model.annotations.Headers
import org.junit.Assert
import org.junit.Test

class HeadersArgumentTextKtTest{

    @Test
    fun testWithoutHeaderAnnotation() {
        val parameterData = ParameterData("test1", ReturnTypeData("String","kotlin.String"))
        val params = listOf(parameterData)
        val text = getHeadersArgumentText(listOf(),params)
        Assert.assertEquals("",text)
    }

    @Test
    fun testWithHeaderAnnotation() {
        val parameterData = ParameterData("test1", ReturnTypeData("String","kotlin.String"))
        val funcAnnotation = Headers(listOf("Accept:Content"))
        val params = listOf(parameterData)
        val text = getHeadersArgumentText(listOf(funcAnnotation),params)
        val expected = "headers·=·listOf(DH(\"Accept\",\"Content\"))"
        Assert.assertEquals(expected,text)
    }

    @Test
    fun testWithHeaderMapAnnotation() {
        val headersMap = HeaderMap
        val parameterData = ParameterData("test1", ReturnTypeData("String","kotlin.String"), annotations = listOf(headersMap))
        val funcAnnotation = Headers(listOf("Accept:Content"))
        val params = listOf(parameterData)
        val text = getHeadersArgumentText(listOf(funcAnnotation),params)
        val expected = "headers·=·listOf(DH(\"Accept\",\"Content\"),·DH(\"\",test1))"
        Assert.assertEquals(expected,text)
    }
}