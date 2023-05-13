package de.jensklingenberg.ktorfit.requestData

import de.jensklingenberg.ktorfit.model.ReturnTypeData
import org.junit.Assert
import org.junit.Test

class RequestTypeInfoKtTest {

    @Test
    fun testWithRequestTypeInfo() {
        val text = getRequestTypeInfoText(ReturnTypeData("String", "kotlin.String", null))
        Assert.assertEquals("requestTypeInfo = typeInfo<String>()", text)
    }
}