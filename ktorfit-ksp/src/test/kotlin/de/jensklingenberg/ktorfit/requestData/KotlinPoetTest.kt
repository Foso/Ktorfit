package de.jensklingenberg.ktorfit.requestData

import de.jensklingenberg.ktorfit.generator.getFileSpec
import de.jensklingenberg.ktorfit.model.MyClass
import de.jensklingenberg.ktorfit.model.MyFunction
import de.jensklingenberg.ktorfit.model.MyParam
import de.jensklingenberg.ktorfit.model.MyType
import de.jensklingenberg.ktorfit.model.annotations.*
import org.junit.Assert
import org.junit.Test

class HeaderArgumentNodeTest {

    @Test
    fun whenNoHeadersAnnotation_ReturnEmptyString() {
        val testPathParam = MyParam(
            "test",
            MyType("String", "String"),
            annotations = listOf(Path("postId"))
        )

        val testPathParam2 = MyParam(
            "postId",
            MyType("String", "String"),
            annotations = listOf(Path("postId"))
        )

        val myFunction1 = MyFunction(
            name = "getTest",
            returnType = MyType("Int", "kotlin.Int"),
            isSuspend = true,
            params = emptyList(),
            annotations = emptyList(),
            httpMethodAnnotation = HttpMethodAnnotation("posts", HttpMethod.GET)
        )

        val myFunction2 = MyFunction(
            name = "getTest2",
            returnType = MyType("Boolean", "kotlin.Boolean"),
            isSuspend = false,
            params = listOf(testPathParam, testPathParam2),
            annotations = emptyList(),
            httpMethodAnnotation = HttpMethodAnnotation("posts", HttpMethod.POST)
        )


        val jvmPlaceHolderApiClass = MyClass(
            name = "JvmPlaceHolderApi",
            packageName = "de.jensklingenberg.ktorfit.demo",
            functions = listOf(myFunction1, myFunction2),
            imports = listOf(),
            superClasses = listOf("com.example.api.StarWarsApi"),
            properties = listOf()
        )

        listOf(jvmPlaceHolderApiClass).forEach { myClass ->


            val file = getFileSpec(myClass)
            file
        }


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

        val expected = "headers = listOf(\"postId:\"$" + "{test}\"\")"

        val funcText = HeadersArgumentNode(
            listOf(de.jensklingenberg.ktorfit.model.annotations.Headers(listOf("x:y", "a:b"))),
            listOf(testPathParam, headerMapParameter)
        ).toString()
        Assert.assertEquals(expected, funcText)
    }

}
