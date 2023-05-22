package de.jensklingenberg.ktorfit.reqBuilderExtension

import de.jensklingenberg.ktorfit.model.ReturnTypeData
import de.jensklingenberg.ktorfit.requestData.getReturnTypeInfoText
import org.junit.Assert
import org.junit.Test

class ReturnTypeInfoKtTest {

    @Test
    fun testWithReturnTypeInfo() {
        val text = getReturnTypeInfoText("String")
        Assert.assertEquals("returnTypeInfo = typeInfo<String>()", text)
    }
}