package de.jensklingenberg.ktorfit.node

import de.jensklingenberg.ktorfit.model.MyFunction
import de.jensklingenberg.ktorfit.model.MyParam
import de.jensklingenberg.ktorfit.model.MyType
import de.jensklingenberg.ktorfit.model.annotations.FormUrlEncoded
import de.jensklingenberg.ktorfit.model.annotations.FunctionAnnotation
import de.jensklingenberg.ktorfit.model.annotations.HttpMethod
import de.jensklingenberg.ktorfit.model.annotations.HttpMethodAnnotation
import de.jensklingenberg.ktorfit.node.HeadersArgumentNode
import de.jensklingenberg.ktorfit.node.RelativeUrlArgumentNode
import org.junit.Assert
import org.junit.Test

class HeaderArgumentNodeTest {

    @Test
    fun whenNoHeadersAnnotation_ReturnEmptyString() {

        val expected = ""

        val funcText = HeadersArgumentNode(emptyList(), emptyList()).toString()
        Assert.assertEquals(expected, funcText)
    }

    @Test
    fun whenFormUrlEncodedAnnotationFound_AddFormUrlEncodedHeader() {

        val funcAnnos = mutableListOf<FunctionAnnotation>(FormUrlEncoded())
        val expected = "headers = listOf(\"Content-Type:application/x-www-form-urlencoded\")"

        val funcText = HeadersArgumentNode(funcAnnos, emptyList()).toString()
        Assert.assertEquals(expected, funcText)
    }

    @Test
    fun whenHeaderAnnotationFound_AddHeader() {
        val testPathParam = MyParam(
            "test",
            MyType("String", "String"),
            annotations = listOf(de.jensklingenberg.ktorfit.model.annotations.Header("postId"))
        )
        val headerMapParameter = MyParam(
            "test",
            MyType("Map<String,String>", "Map<String,String>"),
            annotations = listOf(de.jensklingenberg.ktorfit.model.annotations.HeaderMap())
        )

        val expected = "headers = listOf(\"postId:\"$"+"{test}\"\")"

        val funcText = HeadersArgumentNode(listOf(de.jensklingenberg.ktorfit.model.annotations.Headers(listOf("x:y","a:b"))), listOf(testPathParam,headerMapParameter)).toString()
        Assert.assertEquals(expected, funcText)
    }

}
