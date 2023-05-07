package de.jensklingenberg.ktorfit.requestData

import de.jensklingenberg.ktorfit.model.ParameterData
import de.jensklingenberg.ktorfit.model.ReturnTypeData
import de.jensklingenberg.ktorfit.model.annotations.Field
import de.jensklingenberg.ktorfit.model.annotations.FieldMap
import org.junit.Assert
import org.junit.Test

class FieldArgumentsTextKtTest {

    @Test
    fun testWithoutFieldAnnotation() {
        val parameterData = ParameterData("test1", ReturnTypeData("String", "kotlin.String", null))
        val params = listOf(parameterData)
        val text = getFieldArgumentsText(params)
        Assert.assertEquals("", text)
    }

    @Test
    fun returnFieldWithPartAnnotation() {
        val fieldAnnotation = Field("world")
        val fieldAnnotationEncoded = Field("world", true)

        val parameterData1 = ParameterData(
            "test1",
            ReturnTypeData("String", "kotlin.String", null),
            annotations = listOf(fieldAnnotation)
        )
        val parameterData2 = ParameterData(
            "test1",
            ReturnTypeData("String", "kotlin.String", null),
            annotations = listOf(fieldAnnotationEncoded)
        )

        val params = listOf(parameterData1, parameterData2)
        val text = getFieldArgumentsText(params)
        Assert.assertEquals("fields = listOf(DH(\"world\",test1,false), DH(\"world\",test1,true))", text)
    }

    @Test
    fun returnFieldsWithFieldAndFieldMapAnnotation() {
        val fieldAnnotation = Field("world")
        val fieldAnnotationEncoded = Field("world", true)
        val fieldMapAnnotation = FieldMap(true)


        val parameterData1 = ParameterData(
            "test1",
            ReturnTypeData("String", "kotlin.String", null),
            annotations = listOf(fieldAnnotation)
        )
        val parameterData2 = ParameterData(
            "test2",
            ReturnTypeData("String", "kotlin.String", null),
            annotations = listOf(fieldAnnotationEncoded)
        )
        val parameterData3 = ParameterData(
            "test3",
            ReturnTypeData("String", "kotlin.String", null),
            annotations = listOf(fieldMapAnnotation)
        )

        val params = listOf(parameterData1, parameterData2, parameterData3)
        val text = getFieldArgumentsText(params)
        Assert.assertEquals(
            "fields = listOf(DH(\"world\",test1,false), DH(\"world\",test2,true), DH(\"\",test3,true))",
            text
        )
    }
}