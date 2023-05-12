package de.jensklingenberg.ktorfit.requestData

import com.google.devtools.ksp.symbol.KSType
import de.jensklingenberg.ktorfit.model.ParameterData
import de.jensklingenberg.ktorfit.model.ReturnTypeData
import de.jensklingenberg.ktorfit.model.annotations.Field
import de.jensklingenberg.ktorfit.model.annotations.FieldMap
import de.jensklingenberg.ktorfit.reqBuilderExtension.getFieldArgumentsText
import org.junit.Assert
import org.junit.Test
import org.mockito.kotlin.mock

class FieldArgumentsTextKtTest {

    private val arrayType = mock<KSType>()
    private val listType = mock<KSType>()

    @Test
    fun testWithoutFieldAnnotation() {
        val parameterData = ParameterData("test1", ReturnTypeData("String", "kotlin.String", null))
        val params = listOf(parameterData)
        val text = getFieldArgumentsText(params, listType, arrayType)
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
        val text = getFieldArgumentsText(params, listType, arrayType)
        val expected = "val _formParameters = Parameters.build {\n" +
                "test1?.let{ append(\"world\", \"$"+"{it}\") }\n" +
                "test1?.let{ append(\"world\", \"$"+"{it}\".decodeURLQueryComponent(plusIsSpace = true)) }\n" +
                "}\n" +
                "setBody(FormDataContent(_formParameters))\n"
        Assert.assertEquals(expected, text)
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
        val text = getFieldArgumentsText(params, listType, arrayType)

        val expected = "val _formParameters = Parameters.build {\n" +
                "test1?.let{ append(\"world\", \"$"+"{it}\") }\n" +
                "test2?.let{ append(\"world\", \"$"+"{it}\".decodeURLQueryComponent(plusIsSpace = true)) }\n" +
                "test3?.forEach { entry -> entry.value?.let{ append(entry.key, \"$"+"{entry.value}.decodeURLQueryComponent(plusIsSpace = true)\") } }\n" +
                "}\n" +
                "setBody(FormDataContent(_formParameters))\n"
        Assert.assertEquals(
            expected,
            text
        )
    }
}