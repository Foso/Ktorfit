package de.jensklingenberg.ktorfit.reqBuilderExtension

import de.jensklingenberg.ktorfit.model.ParameterData
import de.jensklingenberg.ktorfit.model.ReturnTypeData
import de.jensklingenberg.ktorfit.model.annotations.ParameterAnnotation.Body
import org.junit.Assert.assertEquals
import org.junit.Test

class GetBodyDataTextKtTest {
    @Test
    fun testWithoutBodyAnnotation() {
        val parameterData = ParameterData("test1", ReturnTypeData("String", null))
        val params = listOf(parameterData)
        val text = getBodyDataText(params)
        assertEquals("", text)
    }

    @Test
    fun testWithBodyAnnotation() {
        val bodyAnno = Body
        val parameterData =
            ParameterData("test1", ReturnTypeData("Map<*,String>", null), annotations = listOf(bodyAnno))
        val params = listOf(parameterData)
        val text = getBodyDataText(params)
        assertEquals("setBody(test1)", text)
    }
}
