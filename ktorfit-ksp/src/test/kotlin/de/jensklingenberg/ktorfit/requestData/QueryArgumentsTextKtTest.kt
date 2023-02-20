package de.jensklingenberg.ktorfit.requestData

import de.jensklingenberg.ktorfit.model.ParameterData
import de.jensklingenberg.ktorfit.model.ReturnTypeData
import de.jensklingenberg.ktorfit.model.annotations.Query
import de.jensklingenberg.ktorfit.model.annotations.QueryMap
import org.junit.Assert
import org.junit.Test

class QueryArgumentsTextKtTest{

    @Test
    fun testWithoutQueryAnnotation() {
        val parameterData = ParameterData("test1", ReturnTypeData("String","kotlin.String"))
        val params = listOf(parameterData)
        val text = getQueryArgumentText(params)
        Assert.assertEquals("",text)
    }

    @Test
    fun returnQueryWitQueryAnnotation() {
        val QueryAnnotation = Query("world")
        val QueryAnnotationEncoded = Query("world",true)

        val parameterData1 = ParameterData("test1", ReturnTypeData("String","kotlin.String"), annotations = listOf(QueryAnnotation))
        val parameterData2 = ParameterData("test1", ReturnTypeData("String","kotlin.String"), annotations = listOf(QueryAnnotationEncoded))

        val params = listOf(parameterData1,parameterData2)
        val text = getQueryArgumentText(params)
        Assert.assertEquals("queries = listOf(DH(\"world\",test1,false,\"QueryType.QUERY\"), DH(\"world\",test1,true,\"QueryType.QUERY\"))",text)
    }

    @Test
    fun returnQuerysWithQueryAndQueryMapAnnotation() {
        val QueryAnnotation = Query("world")
        val QueryAnnotationEncoded = Query("world",true)
        val QueryMapAnnotation = QueryMap(true)


        val parameterData1 = ParameterData("test1", ReturnTypeData("String","kotlin.String"), annotations = listOf(QueryAnnotation))
        val parameterData2 = ParameterData("test2", ReturnTypeData("String","kotlin.String"), annotations = listOf(QueryAnnotationEncoded))
        val parameterData3 = ParameterData("test3", ReturnTypeData("String","kotlin.String"), annotations = listOf(QueryMapAnnotation))

        val params = listOf(parameterData1,parameterData2,parameterData3)
        val text = getQueryArgumentText(params)
        Assert.assertEquals("queries = listOf(DH(\"world\",test1,false,\"QueryType.QUERY\"), DH(\"world\",test2,true,\"QueryType.QUERY\"), DH(\"\",test3,true,\"QueryType.QUERYMAP\"))",text)
    }
}