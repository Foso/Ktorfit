package de.jensklingenberg.ktorfit.reqBuilderExtension

import com.google.devtools.ksp.symbol.KSType
import de.jensklingenberg.ktorfit.createKsType
import de.jensklingenberg.ktorfit.model.ParameterData
import de.jensklingenberg.ktorfit.model.ReturnTypeData
import de.jensklingenberg.ktorfit.model.annotations.ParameterAnnotation.Field
import de.jensklingenberg.ktorfit.model.annotations.ParameterAnnotation.FieldMap
import org.junit.Assert.assertEquals
import org.junit.Test
import org.mockito.kotlin.mock

class FieldArgumentsTextKtTest {
    private val arrayType = mock<KSType>()
    private val listType = mock<KSType>()

    @Test
    fun testWithoutFieldAnnotation() {
        val stringType = createKsType("String", "kotlin")
        val parameterData = ParameterData("test1", ReturnTypeData("String", stringType))
        val params = listOf(parameterData)
        val text = getFieldArgumentsText(params, listType, arrayType)
        assertEquals("", text)
    }

    @Test
    fun returnFieldWithPartAnnotation() {
        val fieldAnnotation = Field("world")
        val fieldAnnotationEncoded = Field("world", true)

        val parameterData1 =
            ParameterData(
                "test1",
                ReturnTypeData("String", null),
                annotations = listOf(fieldAnnotation),
            )
        val parameterData2 =
            ParameterData(
                "test1",
                ReturnTypeData("String", null),
                annotations = listOf(fieldAnnotationEncoded),
            )

        val params = listOf(parameterData1, parameterData2)
        val text = getFieldArgumentsText(params, listType, arrayType)
        val expected =
            """|val __formParameters = Parameters.build {
                                |test1?.let{ append("world", "$/{it}") }
                                |test1?.let{ append("world", "$/{it}".decodeURLQueryComponent(plusIsSpace = true)) }
                                |}
                                |setBody(FormDataContent(__formParameters))
                                |""".replace("$/", "$").trimMargin()
        assertEquals(expected, text)
    }

    @Test
    fun returnFieldsWithFieldAndFieldMapAnnotation() {
        val fieldAnnotation = Field("world")
        val fieldAnnotationEncoded = Field("world", true)
        val fieldMapAnnotation = FieldMap(true)

        val parameterData1 =
            ParameterData(
                "test1",
                ReturnTypeData("String", null),
                annotations = listOf(fieldAnnotation),
            )
        val parameterData2 =
            ParameterData(
                "test2",
                ReturnTypeData("String", null),
                annotations = listOf(fieldAnnotationEncoded),
            )
        val parameterData3 =
            ParameterData(
                "test3",
                ReturnTypeData("String", null),
                annotations = listOf(fieldMapAnnotation),
            )

        val params = listOf(parameterData1, parameterData2, parameterData3)
        val text = getFieldArgumentsText(params, listType, arrayType)

        val expected =
            """|val __formParameters = Parameters.build {
                                |test1?.let{ append("world", "$/{it}") }
                                |test2?.let{ append("world", "$/{it}".decodeURLQueryComponent(plusIsSpace = true)) }
                                |test3?.forEach { entry -> entry.value?.let{ append(entry.key, "$/{entry.value}.decodeURLQueryComponent(plusIsSpace = true)") } }
                                |}
                                |setBody(FormDataContent(__formParameters))
|""".replace("$/", "$").trimMargin()
        assertEquals(
            expected,
            text,
        )
    }
}
