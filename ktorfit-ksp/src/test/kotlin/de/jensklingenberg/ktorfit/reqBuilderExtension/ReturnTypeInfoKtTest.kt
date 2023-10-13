package de.jensklingenberg.ktorfit.reqBuilderExtension

import de.jensklingenberg.ktorfit.typeData.getReturnTypeInfoText
import org.junit.Assert.assertEquals
import org.junit.Test

class ReturnTypeInfoKtTest {

    @Test
    fun testWithReturnTypeInfo() {
        val text = getReturnTypeInfoText("String")
        assertEquals("typeInfo = typeInfo<String>()", text)
    }
}