package de.jensklingenberg.ktorfit.requestData

import de.jensklingenberg.ktorfit.model.ParameterData
import de.jensklingenberg.ktorfit.model.TypeData
import de.jensklingenberg.ktorfit.model.annotations.HttpMethod
import de.jensklingenberg.ktorfit.model.annotations.HttpMethodAnnotation
import org.junit.Assert
import org.junit.Test

class RelativeUrlArgumentsTextTest {

    /**
     * @GET("posts")
     * fun example()
     */
    @Test
    fun testRelativeUrlArguments() {

        val getAnnotation = HttpMethodAnnotation("posts", HttpMethod.GET)
        val actual = getRelativeUrlArgumentText(getAnnotation, emptyList())

        val expected = """relativeUrl="posts""""

        Assert.assertEquals(expected, actual)
    }

    /**
     * @GET("posts/{postId}")
     * fun example(@Path("postId") test: String)
     */
    @Test
    fun testRelativeUrlArgumentsWithPath() {

        val testPathParam = ParameterData(
            "test",
            TypeData("String", "String"),
            annotations = listOf(de.jensklingenberg.ktorfit.model.annotations.Path("postId"))
        )

        val getAnnotation = HttpMethodAnnotation("posts/{postId}", HttpMethod.GET)
        val actual = getRelativeUrlArgumentText(getAnnotation, listOf(testPathParam))
        //relativeUrl="posts/${client.encode(test)}"
        val expected ="relativeUrl=\"posts/$" + "{client.encode(test)}\""

        Assert.assertEquals(expected, actual)
    }

    /**
     * @GET("posts/{postId}")
     * fun example(@Path("postId",encoded= true) test: String)
     */
    @Test
    fun testRelativeUrlArgumentsWithEncodedPath() {

        val testPathParam = ParameterData(
            "test",
            TypeData("String", "String"),
            annotations = listOf(de.jensklingenberg.ktorfit.model.annotations.Path("postId",true))
        )

        val getAnnotation = HttpMethodAnnotation("posts/{postId}", HttpMethod.GET)
        val actual = getRelativeUrlArgumentText(getAnnotation, listOf(testPathParam))
        //relativeUrl="posts/${test}"
        val expected ="relativeUrl=\"posts/$" + "{test}\""

        Assert.assertEquals(expected, actual)
    }



}

