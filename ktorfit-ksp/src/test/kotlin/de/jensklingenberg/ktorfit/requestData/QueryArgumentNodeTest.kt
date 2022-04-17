package de.jensklingenberg.ktorfit.requestData

import de.jensklingenberg.ktorfit.model.MyParam
import de.jensklingenberg.ktorfit.model.MyType
import org.junit.Assert
import org.junit.Test

class QueryArgumentNodeTest {

    val testPathParam = MyParam(
        "test",
        MyType("String", "String"),
        annotations = listOf(de.jensklingenberg.ktorfit.model.annotations.Query("postId"))
    )

    val testPathParam2 = MyParam(
        "test2",
        MyType("Map<String,String>", "Map<String,String>"),
        annotations = listOf(de.jensklingenberg.ktorfit.model.annotations.QueryName(true))
    )

    val testPathParam3 = MyParam(
        "test2",
        MyType("Map<String,String>", "Map<String,String>"),
        annotations = listOf(de.jensklingenberg.ktorfit.model.annotations.QueryMap(true))
    )

    @Test
    fun justGET() {


        val expected = """relativeUrl="posts""""

        
    }



}

