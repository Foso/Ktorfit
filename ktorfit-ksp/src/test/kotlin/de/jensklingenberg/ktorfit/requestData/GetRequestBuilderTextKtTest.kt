package de.jensklingenberg.ktorfit.requestData

import de.jensklingenberg.ktorfit.model.ParameterData
import de.jensklingenberg.ktorfit.model.ReturnTypeData
import de.jensklingenberg.ktorfit.model.annotations.RequestBuilder
import de.jensklingenberg.ktorfit.reqBuilderExtension.getCustomRequestBuilderText
import org.junit.Assert
import org.junit.Test

class GetRequestBuilderTextKtTest {

    @Test
    fun testWithoutBodyAnnotation() {
        val parameterData = ParameterData("test1", ReturnTypeData("String", "kotlin.String", null))
        val params = listOf(parameterData)
        val text = getCustomRequestBuilderText(params)
        Assert.assertEquals("", text)
    }

    @Test
    fun testWithBodyAnnotation() {
        val bodyAnno = RequestBuilder
        val parameterData =
            ParameterData("test1", ReturnTypeData("String", "kotlin.String", null), annotations = listOf(bodyAnno))
        val params = listOf(parameterData)
        val text = getCustomRequestBuilderText(params)
        Assert.assertEquals("test1(this)", text)
    }
}