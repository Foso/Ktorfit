package de.jensklingenberg.ktorfit.requestData

import de.jensklingenberg.ktorfit.model.ParameterData
import de.jensklingenberg.ktorfit.model.ReturnTypeData
import de.jensklingenberg.ktorfit.model.annotations.Query
import de.jensklingenberg.ktorfit.model.annotations.QueryMap
import de.jensklingenberg.ktorfit.model.annotations.QueryName
import org.junit.Assert
import org.junit.Test

class QueryArgumentsTextKtTest {

    @Test
    fun testWithoutQueryAnnotation() {
        val parameterData = ParameterData("test1", ReturnTypeData("String", "kotlin.String", null))
        val params = listOf(parameterData)
        val text = getQueryArgumentText(params)
        Assert.assertEquals("", text)
    }

    @Test
    fun returnQueryWitQueryAnnotation() {
        val expected =
            "queries = listOf(DH(\"world\",test1,false), DH(\"world\",test1,true))"
        val QueryAnnotation = Query("world")
        val QueryAnnotationEncoded = Query("world", true)
        //QUERNAME
        val parameterData1 = ParameterData(
            "test1",
            ReturnTypeData("String", "kotlin.String", null),
            annotations = listOf(QueryAnnotation)
        )
        val parameterData2 = ParameterData(
            "test1",
            ReturnTypeData("String", "kotlin.String", null),
            annotations = listOf(QueryAnnotationEncoded)
        )

        val params = listOf(parameterData1, parameterData2)
        val text = getQueryArgumentText(params)

        Assert.assertEquals(expected, text)
    }

    @Test
    fun returnQueryWitQueryNameAnnotation() {
        val expected = "queries = listOf(DH(\"world\",test1,true), DH(\"\",test1,false))"
        val QueryAnnotation = QueryName()
        val QueryAnnotationEncoded = Query("world", true)
        //QUERNAME
        val parameterData1 = ParameterData(
            "test1",
            ReturnTypeData("String", "kotlin.String", null),
            annotations = listOf(QueryAnnotation)
        )
        val parameterData2 = ParameterData(
            "test1",
            ReturnTypeData("String", "kotlin.String", null),
            annotations = listOf(QueryAnnotationEncoded)
        )

        val params = listOf(parameterData1, parameterData2)
        val text = getQueryArgumentText(params)
        Assert.assertEquals(expected, text)
    }

    @Test
    fun returnQuerysWithQueryAndQueryMapAnnotation() {
        val expected = "queries = listOf(DH(\"world\",test1,false), DH(\"world\",test2,true), DH(\"\",test3,true))"
        val QueryAnnotation = Query("world")
        val QueryAnnotationEncoded = Query("world", true)
        val QueryMapAnnotation = QueryMap(true)


        val parameterData1 = ParameterData(
            "test1",
            ReturnTypeData("String", "kotlin.String", null),
            annotations = listOf(QueryAnnotation)
        )
        val parameterData2 = ParameterData(
            "test2",
            ReturnTypeData("String", "kotlin.String", null),
            annotations = listOf(QueryAnnotationEncoded)
        )
        val parameterData3 = ParameterData(
            "test3",
            ReturnTypeData("String", "kotlin.String", null),
            annotations = listOf(QueryMapAnnotation)
        )

        val params = listOf(parameterData1, parameterData2, parameterData3)
        val text = getQueryArgumentText(params)
        Assert.assertEquals(expected, text)
    }
}