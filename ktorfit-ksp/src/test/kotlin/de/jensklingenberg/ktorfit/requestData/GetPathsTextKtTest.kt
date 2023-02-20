package de.jensklingenberg.ktorfit.requestData

import de.jensklingenberg.ktorfit.model.ParameterData
import de.jensklingenberg.ktorfit.model.ReturnTypeData
import de.jensklingenberg.ktorfit.model.annotations.Path
import org.junit.Assert
import org.junit.Test

class GetPathsTextKtTest {

    @Test
    fun testWithoutPathAnnotation() {
        val parameterData = ParameterData("test1", ReturnTypeData("String", "kotlin.String"))
        val params = listOf(parameterData)
        val text = getPathsText(params)
        Assert.assertEquals("", text)
    }

    @Test
    fun testWithPathAnnotation() {
        val path = Path("testValue")
        val parameterData =
            ParameterData("test1", ReturnTypeData("String", "kotlin.String"), annotations = listOf(path))
        val params = listOf(parameterData)
        val text = getPathsText(params)
        Assert.assertEquals("paths = listOf(DH(\"testValue\",test1,false))", text)
    }
}