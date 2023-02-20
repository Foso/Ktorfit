package de.jensklingenberg.ktorfit.requestData

import de.jensklingenberg.ktorfit.model.ReturnTypeData
import org.junit.Assert
import org.junit.Test

class ReturnTypeInfoKtTest{

    @Test
    fun testWithReturnTypeInfo() {
        val text = getReturnTypeInfoText(ReturnTypeData("String","kotlin.String"))
        Assert.assertEquals("returnTypeInfo = typeInfo<String>()", text)
    }
}