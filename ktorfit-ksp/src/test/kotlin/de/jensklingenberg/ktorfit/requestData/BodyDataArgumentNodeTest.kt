package de.jensklingenberg.ktorfit.requestData

import de.jensklingenberg.ktorfit.model.MyParam
import de.jensklingenberg.ktorfit.model.MyType
import org.junit.Assert
import org.junit.Test

class BodyDataArgumentNodeTest {

    val testPathParam = MyParam(
        "test",
        MyType("String", "String"),
        annotations = listOf(de.jensklingenberg.ktorfit.model.annotations.Path("postId"))
    )
    val testBodyParam = MyParam(
        "test",
        MyType("String", "String"),
        annotations = listOf(de.jensklingenberg.ktorfit.model.annotations.Body())
    )

    @Test
    fun whenNoBodyParamFound_ReturnEmpty() {
        val expected = ""

        val funcText = BodyDataArgumentNode(listOf(testPathParam)).toString()
        Assert.assertEquals(expected, funcText)
    }

    @Test
    fun whenBodyParamFound_ReturnBodyDataText() {
        val expected = "bodyData = test"

        val funcText = BodyDataArgumentNode(listOf(testBodyParam)).toString()
        Assert.assertEquals(expected, funcText)
    }

}
