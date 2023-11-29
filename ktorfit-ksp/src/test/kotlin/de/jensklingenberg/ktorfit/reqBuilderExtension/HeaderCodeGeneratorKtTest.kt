package de.jensklingenberg.ktorfit.reqBuilderExtension

import com.google.devtools.ksp.symbol.KSType
import com.google.devtools.ksp.symbol.KSTypeArgument
import com.google.devtools.ksp.symbol.KSTypeReference
import com.google.devtools.ksp.symbol.Variance
import de.jensklingenberg.ktorfit.createKsType
import de.jensklingenberg.ktorfit.model.ParameterData
import de.jensklingenberg.ktorfit.model.ReturnTypeData
import de.jensklingenberg.ktorfit.model.annotations.Headers
import de.jensklingenberg.ktorfit.model.annotations.ParameterAnnotation.Header
import de.jensklingenberg.ktorfit.model.annotations.ParameterAnnotation.HeaderMap
import org.junit.Assert.assertEquals
import org.junit.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever

class HeaderCodeGeneratorKtTest {

    private val arrayType = mock<KSType>()
    private val listTypeMock = mock<KSType>()

    @Test
    fun whenFunctionHasNoHeaderAnnotationThenGenerateEmptyText() {

        val parameterData = ParameterData("test1", ReturnTypeData("String", null))
        val params = listOf(parameterData)
        val text = getHeadersCode(listOf(), params, listTypeMock, arrayType)
        assertEquals("", text)
    }

    @Test
    fun testWithHeaderAnnotationWithString() {
        val parameterData =
            ParameterData(
                "test1",
                ReturnTypeData("String", createKsType("String", "kotlin")),
                annotations = listOf(Header("foo"))
            )
        val params = listOf(parameterData)
        val actualCode = getHeadersCode(listOf(), params, listTypeMock, arrayType)
        val expectedCode = """|headers{
                                |append("foo", test1)
                                |}""".trimMargin()
        assertEquals(expectedCode, actualCode)
    }

    @Test
    fun testWithHeaderAnnotationWithList() {

        val listTypeStar = createKsType("List", "kotlin.collections")

        val stringType = createKsType("String", "kotlin")
        mockList(listTypeStar, stringType)

        val parameterData = ParameterData(
            "test1",
            ReturnTypeData("List<String>", listTypeStar),
            annotations = listOf(Header("foo"))
        )
        val params = listOf(parameterData)
        val actual = getHeadersCode(listOf(), params, listTypeMock, arrayType)
        val expected = """|headers{
                                |test1.forEach{ append("foo", it)}
                                |}""".trimMargin()
        assertEquals(expected, actual)
    }

    private fun mockList(listTypeStar: KSType, vararg typeArgs: KSType) {
        val stringType = typeArgs[0]

        val ksTypeReference = mock<KSTypeReference>()
        whenever(ksTypeReference.resolve()).then { stringType }


        val tt = typeArgs.map {
            val invariantType = mock<KSTypeArgument>()
            whenever(invariantType.toString()).then { "${Variance.INVARIANT} $stringType" }
            whenever(invariantType.type).then { ksTypeReference }
            whenever(invariantType.variance).then { Variance.INVARIANT }
            invariantType
        }

        whenever(listTypeStar.starProjection()).then { listTypeStar }
        whenever(listTypeStar.isAssignableFrom(listTypeMock)).then { true }

        whenever(listTypeStar.arguments).then { tt }
    }

    @Test
    fun testWithHeaderAnnotationWithArray() {
        val listTypeStar = createKsType("Array", "kotlin")
        val stringType = createKsType("String", "kotlin")
        mockList(listTypeStar, stringType)

        val parameterData = ParameterData(
            "test1",
            ReturnTypeData("Array<String>", listTypeStar),
            annotations = listOf(Header("foo"))
        )
        val params = listOf(parameterData)
        val actual = getHeadersCode(listOf(), params, listTypeMock, arrayType)
        val expected = """|headers{
                                |test1.forEach{ append("foo", it)}
                                |}"""
            .trimMargin()
        assertEquals(expected, actual)
    }

    @Test
    fun testWithHeadersAnnotation() {
        val stringType = createKsType("String", "kotlin")
        val parameterData = ParameterData("test1", ReturnTypeData("String", stringType))
        val funcAnnotation = Headers(listOf("Accept:Content", "foo: bar"))
        val params = listOf(parameterData)
        val actual = getHeadersCode(listOf(funcAnnotation), params, listTypeMock, arrayType)
        val expected = """|headers{
                        |append("Accept", "Content")
                        |append("foo", "bar")
                        |}""".trimMargin()
        assertEquals(expected, actual)
    }

    @Test
    fun testWithHeaderMapAnnotationAndHeadersAnnotation() {
        val listTypeStar = createKsType("Map", "kotlin.collections")
        val stringType = createKsType("String", "kotlin")
        mockList(listTypeStar, stringType, stringType)

        val parameterData =
            ParameterData(
                "test1",
                ReturnTypeData("Map<String,String>", listTypeStar),
                annotations = listOf(HeaderMap)
            )
        val funcAnnotation = Headers(listOf("Accept:Content"))
        val params = listOf(parameterData)
        val actual = getHeadersCode(listOf(funcAnnotation), params, listTypeMock, arrayType)
        val expected = """|headers{
                                |test1.forEach{ append(it.key , it.value)}
                                |append("Accept", "Content")
                                |}""".trimMargin()
        assertEquals(expected, actual)
    }
}