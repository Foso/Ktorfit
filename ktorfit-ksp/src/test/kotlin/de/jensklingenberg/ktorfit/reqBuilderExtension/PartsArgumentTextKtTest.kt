package de.jensklingenberg.ktorfit.reqBuilderExtension

import com.google.devtools.ksp.symbol.KSType
import de.jensklingenberg.ktorfit.model.ParameterData
import de.jensklingenberg.ktorfit.model.ReturnTypeData
import de.jensklingenberg.ktorfit.model.annotations.ParameterAnnotation.Part
import de.jensklingenberg.ktorfit.model.annotations.ParameterAnnotation.PartMap
import org.junit.Assert.assertEquals
import org.junit.Test
import org.mockito.kotlin.mock

class PartsArgumentTextKtTest {
    private val arrayType = mock<KSType>()
    private val listType = mock<KSType>()

    @Test
    fun whenNoPartAnnotationFoundThenKeepPartsCodeEmpty() {
        val parameterData = ParameterData("test1", ReturnTypeData("String", mock<KSType>()))
        val params = listOf(parameterData)
        val text = getPartsCode(params, listType, arrayType)
        assertEquals("", text)
    }

    @Test
    fun returnPartsWithPartAnnotation() {
        val partAnnotation = Part("world")
        val parameterData =
            ParameterData(
                "test1",
                ReturnTypeData("String", mock<KSType>()),
                annotations = listOf(partAnnotation),
            )
        val params = listOf(parameterData)
        val text = getPartsCode(params, listType, arrayType)
        val expected =
            """|val __formData = formData {
                            |test1?.let{ append("world", "$/{it}") }
                            |}
                            |setBody(MultiPartFormDataContent(__formData))
                            |
            """.trimMargin().replace("$/", "$")
        assertEquals(expected, text)
    }

    @Test
    fun returnPartsWithPartMapAnnotation() {
        val partMapAnnotation = PartMap()
        val parameterData1 =
            ParameterData(
                "test1",
                ReturnTypeData("String", mock<KSType>()),
                annotations = listOf(partMapAnnotation),
            )
        val parameterData2 = ParameterData("test2", ReturnTypeData("String", mock<KSType>()))

        val params = listOf(parameterData1, parameterData2)
        val text = getPartsCode(params, listType, arrayType)
        val expected =
            """val __formData = formData {
test1?.forEach { entry -> entry.value?.let{ append(entry.key, "$/{entry.value}") } }
}
setBody(MultiPartFormDataContent(__formData))

            """.trimMargin().replace("$/", "$")
        assertEquals(expected, text)
    }

    @Test
    fun returnPartsWithPartAnnotationAndPartMapAnnotation() {
        val partMapAnnotation = PartMap()
        val partAnnotation = Part("world")

        val parameterData1 =
            ParameterData(
                "test1",
                ReturnTypeData("String", mock<KSType>()),
                annotations = listOf(partMapAnnotation),
            )
        val parameterData2 =
            ParameterData(
                "test2",
                ReturnTypeData("String", mock<KSType>()),
                annotations = listOf(partAnnotation),
            )

        val params = listOf(parameterData1, parameterData2)
        val text = getPartsCode(params, listType, arrayType)
        val expected =
            """|val __formData = formData {
                            |test2?.let{ append("world", "$/{it}") }
                            |test1?.forEach { entry -> entry.value?.let{ append(entry.key, "$/{entry.value}") } }
                            |}
                            |setBody(MultiPartFormDataContent(__formData))
                            |
            """.trimMargin().replace("$/", "$")

        assertEquals(expected, text)
    }
}
