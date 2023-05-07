package de.jensklingenberg.ktorfit.requestData

import com.google.devtools.ksp.symbol.KSType
import com.google.devtools.ksp.symbol.KSTypeReference
import de.jensklingenberg.ktorfit.model.ParameterData
import de.jensklingenberg.ktorfit.model.ReturnTypeData
import de.jensklingenberg.ktorfit.model.annotations.Header
import de.jensklingenberg.ktorfit.model.annotations.HeaderMap
import de.jensklingenberg.ktorfit.model.annotations.Headers
import de.jensklingenberg.ktorfit.reqBuilderExtension.getHeadersCode
import org.junit.Assert
import org.junit.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever

class HeadersArgumentTextKtTest {

    private val arrayType = mock<KSType>()
    private val listType = mock<KSType>()

    @Test
    fun whenFunctionHasNoHeaderAnnotationThenGenerateEmptyText() {

        val parameterData = ParameterData("test1", ReturnTypeData("String", "kotlin.String", null))
        val params = listOf(parameterData)
        val text = getHeadersCode(listOf(), params, listType, arrayType)
        Assert.assertEquals("", text)
    }

    @Test
    fun testWithHeaderAnnotationWithString() {
        val parameterData =
            ParameterData("test1", ReturnTypeData("String", "kotlin.String", null), annotations = listOf(Header("foo")))
        val params = listOf(parameterData)
        val text = getHeadersCode(listOf(), params, listType, arrayType)
        val expected = "headers{\n" +
                "append(\"foo\", test1.toString())}"
        Assert.assertEquals(expected, text)
    }

    @Test
    fun testWithHeaderAnnotationWithList() {

        val typeRef = mock<KSTypeReference>()
        whenever(listType.starProjection()).then { listType }
        whenever(listType.isAssignableFrom(listType)).then { true }
        whenever(typeRef.resolve()).then { listType }

        val parameterData = ParameterData(
            "test1",
            ReturnTypeData("List<String>", "kotlin.collections.List", typeRef),
            annotations = listOf(Header("foo"))
        )
        val params = listOf(parameterData)
        val text = getHeadersCode(listOf(), params, listType, arrayType)
        val expected = "headers{\n" +
                "test1?.filterNotNull()?.forEach { append(\"foo\", it.toString()) }}"
        Assert.assertEquals(expected, text)
    }

    @Test
    fun testWithHeaderAnnotationWithArray() {

        val typeRef = mock<KSTypeReference>()
        whenever(arrayType.starProjection()).then { arrayType }
        whenever(arrayType.isAssignableFrom(arrayType)).then { true }
        whenever(typeRef.resolve()).then { arrayType }

        val parameterData = ParameterData(
            "test1",
            ReturnTypeData("Array<String>", "kotlin.Array", typeRef),
            annotations = listOf(Header("foo"))
        )
        val params = listOf(parameterData)
        val text = getHeadersCode(listOf(), params, listType, arrayType)
        val expected = "headers{\n" +
                "test1?.filterNotNull()?.forEach { append(\"foo\", it.toString()) }}"
        Assert.assertEquals(expected, text)
    }

    @Test
    fun testWithHeadersAnnotation() {
        val parameterData = ParameterData("test1", ReturnTypeData("String", "kotlin.String", null))
        val funcAnnotation = Headers(listOf("Accept:Content", "foo: bar"))
        val params = listOf(parameterData)
        val text = getHeadersCode(listOf(funcAnnotation), params, listType, arrayType)
        val expected = "headers{\n" +
                "append(\"Accept\", \"Content\")\n" +
                "append(\"foo\", \"bar\")\n" +
                "}"
        Assert.assertEquals(expected, text)
    }

    @Test
    fun testWithHeaderMapAnnotationAndHeadersAnnotation() {
        val parameterData =
            ParameterData("test1", ReturnTypeData("String", "kotlin.String", null), annotations = listOf(HeaderMap))
        val funcAnnotation = Headers(listOf("Accept:Content"))
        val params = listOf(parameterData)
        val text = getHeadersCode(listOf(funcAnnotation), params, listType, arrayType)
        val expected = "headers{\n" +
                "test1?.forEach { append(it.key, it.value.toString()) }\n" +
                "append(\"Accept\", \"Content\")\n" +
                "}"
        Assert.assertEquals(expected, text)
    }
}