package de.jensklingenberg.ktorfit

import KtorfitProcessorProvider
import com.google.common.truth.Truth
import com.tschuchort.compiletesting.KotlinCompilation
import com.tschuchort.compiletesting.SourceFile
import com.tschuchort.compiletesting.kspIncremental
import com.tschuchort.compiletesting.kspSourcesDir
import com.tschuchort.compiletesting.symbolProcessorProviders
import de.jensklingenberg.ktorfit.model.KtorfitError
import org.junit.Assert
import org.junit.Test
import java.io.File

class QueryAnnotationsTest {


    @Test
    fun whenNoQueryAnnotationsFound_KeepQuerysArgumentEmpty() {

        val source = SourceFile.kotlin(
            "CustomCallable.kt", """
      package com.example.api
import de.jensklingenberg.ktorfit.http.GET

interface TestService {

    @GET("posts")
    suspend fun test(): String
    
}
    """
        )


        val expectedFunctionText = "queries ="

        val compilation = KotlinCompilation().apply {
            sources = listOf(source)
            inheritClassPath = true
            symbolProcessorProviders = listOf(KtorfitProcessorProvider())
            kspIncremental = true
        }
        val result = compilation.compile()
        Truth.assertThat(result.exitCode).isEqualTo(KotlinCompilation.ExitCode.OK)

        val generatedSourcesDir = compilation.kspSourcesDir
        val generatedFile = File(
            generatedSourcesDir,
            "/kotlin/com/example/api/_TestServiceImpl.kt"
        )
        Truth.assertThat(generatedFile.exists()).isTrue()
        Truth.assertThat(generatedFile.readText().contains(expectedFunctionText)).isFalse()
    }



    @Test
    fun testQuery() {

        val source = SourceFile.kotlin(
            "CustomCallable.kt", """
      package com.example.api
import de.jensklingenberg.ktorfit.http.GET
import de.jensklingenberg.ktorfit.http.Query

interface TestService {

    @GET("posts")
    suspend fun test(@Query("name") testQuery: String)
    
}
    """
        )


        val expected = "queries = listOf(QueryData(false,testQuery,\"name\",QueryType.QUERY))"

        val compilation = KotlinCompilation().apply {
            sources = listOf(source)
            inheritClassPath = true
            symbolProcessorProviders = listOf(KtorfitProcessorProvider())
            kspIncremental = true
        }
        val result = compilation.compile()
        Truth.assertThat(result.exitCode).isEqualTo(KotlinCompilation.ExitCode.OK)

        val generatedSourcesDir = compilation.kspSourcesDir
        val generatedFile = File(
            generatedSourcesDir,
            "/kotlin/com/example/api/_TestServiceImpl.kt"
        )
        Truth.assertThat(generatedFile.exists()).isTrue()
        Truth.assertThat(generatedFile.readText().contains(expected)).isTrue()
    }

    @Test
    fun testEncodedQuery() {

        val source = SourceFile.kotlin(
            "CustomCallable.kt", """
      package com.example.api
import de.jensklingenberg.ktorfit.http.GET
import de.jensklingenberg.ktorfit.http.Query

interface TestService {

    @GET("posts")
    suspend fun test(@Query("name",true) testQuery: String)
    
}
    """
        )


        val expected = "queries = listOf(QueryData(true,testQuery,\"name\",QueryType.QUERY))"

        val compilation = KotlinCompilation().apply {
            sources = listOf(source)
            inheritClassPath = true
            symbolProcessorProviders = listOf(KtorfitProcessorProvider())
            kspIncremental = true
        }
        val result = compilation.compile()
        Truth.assertThat(result.exitCode).isEqualTo(KotlinCompilation.ExitCode.OK)

        val generatedSourcesDir = compilation.kspSourcesDir
        val generatedFile = File(
            generatedSourcesDir,
            "/kotlin/com/example/api/_TestServiceImpl.kt"
        )
        Truth.assertThat(generatedFile.exists()).isTrue()
        Truth.assertThat(generatedFile.readText().contains(expected)).isTrue()
    }

    @Test
    fun testQueryName() {

        val source = SourceFile.kotlin(
            "CustomCallable.kt", """
      package com.example.api
import de.jensklingenberg.ktorfit.http.GET
import de.jensklingenberg.ktorfit.http.QueryName

interface TestService {

    @GET("posts")
    suspend fun test(@QueryName() testQueryName: String)
    
}
    """
        )


        val expected = "queries = listOf(QueryData(false,testQueryName,\"\",QueryType.QUERYNAME))"

        val compilation = KotlinCompilation().apply {
            sources = listOf(source)
            inheritClassPath = true
            symbolProcessorProviders = listOf(KtorfitProcessorProvider())
            kspIncremental = true
        }
        val result = compilation.compile()
        Truth.assertThat(result.exitCode).isEqualTo(KotlinCompilation.ExitCode.OK)

        val generatedSourcesDir = compilation.kspSourcesDir
        val generatedFile = File(
            generatedSourcesDir,
            "/kotlin/com/example/api/_TestServiceImpl.kt"
        )
        Truth.assertThat(generatedFile.exists()).isTrue()
        Truth.assertThat(generatedFile.readText().contains(expected)).isTrue()
    }


    @Test
    fun testQueryMap() {

        val source = SourceFile.kotlin(
            "CustomCallable.kt", """
      package com.example.api
import de.jensklingenberg.ktorfit.http.GET
import de.jensklingenberg.ktorfit.http.QueryMap

interface TestService {

    @GET("posts")
    suspend fun test(@QueryMap() testQueryMap: Map<String, String>)
    
}
    """
        )


        val expected = "queries = listOf(QueryData(false,testQueryMap,\"\",QueryType.QUERYMAP))"

        val compilation = KotlinCompilation().apply {
            sources = listOf(source)
            inheritClassPath = true
            symbolProcessorProviders = listOf(KtorfitProcessorProvider())
            kspIncremental = true
        }
        val result = compilation.compile()
        Truth.assertThat(result.exitCode).isEqualTo(KotlinCompilation.ExitCode.OK)

        val generatedSourcesDir = compilation.kspSourcesDir
        val generatedFile = File(
            generatedSourcesDir,
            "/kotlin/com/example/api/_TestServiceImpl.kt"
        )
        Truth.assertThat(generatedFile.exists()).isTrue()
        Truth.assertThat(generatedFile.readText().contains(expected)).isTrue()
    }


    @Test
    fun testFunctionWithQueryAndQueryNameAndQueryMap() {

        val source = SourceFile.kotlin(
            "CustomCallable.kt", """
      package com.example.api
import de.jensklingenberg.ktorfit.http.GET
import de.jensklingenberg.ktorfit.http.QueryMap
import de.jensklingenberg.ktorfit.http.Query
import de.jensklingenberg.ktorfit.http.QueryName

interface TestService {

   @GET("posts")
   fun example(@Query("name") testQuery: String, @QueryName() testQueryName: String, @QueryMap() name: Map<String, String>)
    
}
    """
        )


        val expected =  "queries = listOf(QueryData(false,testQuery,\"name\",QueryType.QUERY),\n" +
                "            QueryData(false,testQueryName,\"\",QueryType.QUERYNAME),\n" +
                "            QueryData(false,name,\"\",QueryType.QUERYMAP)),"

        val compilation = KotlinCompilation().apply {
            sources = listOf(source)
            inheritClassPath = true
            symbolProcessorProviders = listOf(KtorfitProcessorProvider())
            kspIncremental = true
        }
        val result = compilation.compile()
        Truth.assertThat(result.exitCode).isEqualTo(KotlinCompilation.ExitCode.OK)

        val generatedSourcesDir = compilation.kspSourcesDir
        val generatedFile = File(
            generatedSourcesDir,
            "/kotlin/com/example/api/_TestServiceImpl.kt"
        )
        Truth.assertThat(generatedFile.exists()).isTrue()
        Truth.assertThat(generatedFile.readText().contains(expected)).isTrue()
    }

    @Test
    fun whenQueryMapTypeIsNotMap_ThrowCompilationError() {

        val source = SourceFile.kotlin(
            "CustomCallable.kt", """
      package com.example.api
import de.jensklingenberg.ktorfit.http.GET
import de.jensklingenberg.ktorfit.http.QueryMap

interface TestService {

    @GET("posts")
    suspend fun test(@QueryMap() testQueryMap: String)
    
}
    """
        )

        val compilation = KotlinCompilation().apply {
            sources = listOf(source)
            inheritClassPath = true
            symbolProcessorProviders = listOf(KtorfitProcessorProvider())
            kspIncremental = true
        }

        val result = compilation.compile()
        Truth.assertThat(result.exitCode).isEqualTo(KotlinCompilation.ExitCode.COMPILATION_ERROR)
        Assert.assertTrue(result.messages.contains(KtorfitError.QUERY_MAP_PARAMETER_TYPE_MUST_BE_MAP))
    }

    @Test
    fun whenQueryMapKeysIsNotString_ThrowCompilationError() {

        val source = SourceFile.kotlin(
            "CustomCallable.kt", """
      package com.example.api
import de.jensklingenberg.ktorfit.http.GET
import de.jensklingenberg.ktorfit.http.QueryMap

interface TestService {

    @GET("posts")
    suspend fun test(@QueryMap() testQueryMap: Map<Int, String>)
    
}
    """
        )

        val compilation = KotlinCompilation().apply {
            sources = listOf(source)
            inheritClassPath = true
            symbolProcessorProviders = listOf(KtorfitProcessorProvider())
            kspIncremental = true
        }

        val result = compilation.compile()
        Truth.assertThat(result.exitCode).isEqualTo(KotlinCompilation.ExitCode.COMPILATION_ERROR)
        Assert.assertTrue(result.messages.contains(KtorfitError.QUERY_MAP_PARAMETER_TYPE_MUST_BE_MAP))
    }

    @Test
    fun whenQueryMapNullable_ThrowCompilationError() {

        val source = SourceFile.kotlin(
            "CustomCallable.kt", """
      package com.example.api
import de.jensklingenberg.ktorfit.http.GET
import de.jensklingenberg.ktorfit.http.QueryMap

interface TestService {

    @GET("posts")
    suspend fun test(@QueryMap() testQueryMap: Map<String,String>?)
    
}
    """
        )

        val compilation = KotlinCompilation().apply {
            sources = listOf(source)
            inheritClassPath = true
            symbolProcessorProviders = listOf(KtorfitProcessorProvider())
            kspIncremental = true
        }

        val result = compilation.compile()
        Truth.assertThat(result.exitCode).isEqualTo(KotlinCompilation.ExitCode.COMPILATION_ERROR)
        Assert.assertTrue(result.messages.contains(KtorfitError.QUERY_MAP_PARAMETER_TYPE_MAY_NOT_BE_NULLABLE))
    }

}

