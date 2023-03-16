package de.jensklingenberg.ktorfit.requestData

import de.jensklingenberg.ktorfit.model.ParameterData
import de.jensklingenberg.ktorfit.model.ReturnTypeData
import de.jensklingenberg.ktorfit.model.annotations.Body
import org.junit.Assert
import org.junit.Test

class GetBodyDataTextKtTest{

    @Test
    fun testWithoutBodyAnnotation() {
        val parameterData = ParameterData("test1", ReturnTypeData("String", "kotlin.String"))
        val params = listOf(parameterData)
        val text = getBodyDataText(params)
        Assert.assertEquals("", text)
    }

    @Test
    fun testWithBodyAnnotation() {
        val bodyAnno = Body
        val parameterData = ParameterData("test1", ReturnTypeData("Map<*,String>", "kotlin.Map"), annotations = listOf(bodyAnno))
        val params = listOf(parameterData)
        val text = getBodyDataText(params)
        Assert.assertEquals("bodyData = BodyData(test1, typeInfo<Map<*,String>>())", text)
    }
}