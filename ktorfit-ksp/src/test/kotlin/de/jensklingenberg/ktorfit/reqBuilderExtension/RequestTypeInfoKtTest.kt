package de.jensklingenberg.ktorfit.reqBuilderExtension

import de.jensklingenberg.ktorfit.model.ReturnTypeData
import de.jensklingenberg.ktorfit.requestData.getRequestTypeInfoText
import org.junit.Assert
import org.junit.Test

class RequestTypeInfoKtTest {

    @Test
    fun testWithRequestTypeInfo() {
        val text = getRequestTypeInfoText(ReturnTypeData("String", "kotlin.String", null))
        Assert.assertEquals("requestTypeInfo = typeInfo<String>()", text)
    }
}