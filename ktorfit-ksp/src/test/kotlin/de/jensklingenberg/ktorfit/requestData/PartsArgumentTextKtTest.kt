package de.jensklingenberg.ktorfit.requestData

import de.jensklingenberg.ktorfit.model.ParameterData
import de.jensklingenberg.ktorfit.model.ReturnTypeData
import de.jensklingenberg.ktorfit.model.annotations.Part
import de.jensklingenberg.ktorfit.model.annotations.PartMap
import org.junit.Assert
import org.junit.Test

class PartsArgumentTextKtTest {

    @Test
    fun testWithoutPartAnnotation() {
        val parameterData = ParameterData("test1", ReturnTypeData("String", "kotlin.String", null))
        val params = listOf(parameterData)
        val text = getPartsArgumentText(params)
        Assert.assertEquals("", text)
    }

    @Test
    fun returnPartsWithPartAnnotation() {
        val partAnnotation = Part("world")
        val parameterData = ParameterData(
            "test1",
            ReturnTypeData("String", "kotlin.String", null),
            annotations = listOf(partAnnotation)
        )
        val params = listOf(parameterData)
        val text = getPartsArgumentText(params)
        Assert.assertEquals("parts = mapOf(\"world\" to test1)", text)
    }

    @Test
    fun returnPartsWithPartMapAnnotation() {
        val partMapAnnotation = PartMap()
        val parameterData1 = ParameterData(
            "test1",
            ReturnTypeData("String", "kotlin.String", null),
            annotations = listOf(partMapAnnotation)
        )
        val parameterData2 = ParameterData("test2", ReturnTypeData("String", "kotlin.String", null))

        val params = listOf(parameterData1, parameterData2)
        val text = getPartsArgumentText(params)
        Assert.assertEquals("parts = test1", text)
    }

    @Test
    fun returnPartsWithPartAnnotationAndPartMapAnnotation() {
        val partMapAnnotation = PartMap()
        val partAnnotation = Part("world")

        val parameterData1 = ParameterData(
            "test1",
            ReturnTypeData("String", "kotlin.String", null),
            annotations = listOf(partMapAnnotation)
        )
        val parameterData2 = ParameterData(
            "test2",
            ReturnTypeData("String", "kotlin.String", null),
            annotations = listOf(partAnnotation)
        )

        val params = listOf(parameterData1, parameterData2)
        val text = getPartsArgumentText(params)
        Assert.assertEquals("parts = mapOf(\"world\" to test2)+test1", text)
    }
}