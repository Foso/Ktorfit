package de.jensklingenberg.ktorfit.reqBuilderExtension

import de.jensklingenberg.ktorfit.model.ParameterData
import de.jensklingenberg.ktorfit.model.ReturnTypeData
import de.jensklingenberg.ktorfit.model.annotations.ParameterAnnotation.RequestBuilder
import org.junit.Assert.assertEquals
import org.junit.Test

class GetRequestBuilderTextKtTest {

    @Test
    fun testWithoutRequestBuilderAnnotation() {
        val parameterData = ParameterData("test1", ReturnTypeData("String", "kotlin.String", null))
        val params = listOf(parameterData)
        val text = getCustomRequestBuilderText(params)
        assertEquals("", text)
    }

    @Test
    fun testWithRequestBuilderAnnotation() {
        val bodyAnno = RequestBuilder
        val parameterData =
            ParameterData("test1", ReturnTypeData("String", "kotlin.String", null), annotations = listOf(bodyAnno))
        val params = listOf(parameterData)
        val text = getCustomRequestBuilderText(params)
        assertEquals("test1(this)", text)
    }
}