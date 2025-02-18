package de.jensklingenberg.ktorfit

import com.tschuchort.compiletesting.KotlinCompilation
import com.tschuchort.compiletesting.SourceFile
import com.tschuchort.compiletesting.kspSourcesDir
import de.jensklingenberg.ktorfit.model.KtorfitError
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import java.io.File

class QueryAnnotationsTest {
    @Test
    fun whenNotEncodedQueryAnnotationAndEncodedFound() {
        val source =
            SourceFile.kotlin(
                "Source.kt",
                """
      package com.example.api
import de.jensklingenberg.ktorfit.http.GET
import de.jensklingenberg.ktorfit.http.Query

interface TestService {

    @GET("posts")
    suspend fun test(@Query("name") testQuery: String, @Query(encoded = true) testQuery2: Int)
}
    """,
            )

        val expectedQueriesArgumentText =
            "url{\n" +
                "        takeFrom(_ktorfit.baseUrl + \"posts\")\n" +
                "        testQuery?.let{ parameter(\"name\", \"\$it\") }\n" +
                "        testQuery2?.let{ encodedParameters.append(\"testQuery2\", \"\$it\") }\n" +
                "        }"

        val compilation = getCompilation(listOf(source))
        val result = compilation.compile()

        val generatedSourcesDir = compilation.kspSourcesDir
        val generatedFile =
            File(
                generatedSourcesDir,
                "/kotlin/com/example/api/_TestServiceImpl.kt",
            )

        val actualSource = generatedFile.readText()
        assertTrue(actualSource.contains(expectedQueriesArgumentText))
    }

    @Test
    fun whenQueryAnnotationWithListFound() {
        val source =
            SourceFile.kotlin(
                "Source.kt",
                """
      package com.example.api
import de.jensklingenberg.ktorfit.http.GET
import de.jensklingenberg.ktorfit.http.Query

interface TestService {

    @GET("posts")
    suspend fun test(@Query("user",true) testQuery2: List<String>)
}
    """,
            )

        val expectedQueriesArgumentText =
            "url{\n" +
                "        takeFrom(_ktorfit.baseUrl + \"posts\")\n" +
                "        testQuery2?.filterNotNull()?.forEach { encodedParameters.append(\"user\", \"\$it\") }\n" +
                "        }"

        val compilation = getCompilation(listOf(source))
        val result = compilation.compile()

        val generatedSourcesDir = compilation.kspSourcesDir
        val generatedFile =
            File(
                generatedSourcesDir,
                "/kotlin/com/example/api/_TestServiceImpl.kt",
            )

        val actualSource = generatedFile.readText()
        assertTrue(actualSource.contains(expectedQueriesArgumentText))
    }

    @Test
    fun whenQueryNamesFound() {
        val source =
            SourceFile.kotlin(
                "Source.kt",
                """
      package com.example.api
import de.jensklingenberg.ktorfit.http.GET
import de.jensklingenberg.ktorfit.http.QueryName

interface TestService {
    @GET("posts")
    suspend fun test(@QueryName testQueryName: String, @QueryName testQueryName2: String)
}
    """,
            )

        val expectedQueriesArgumentText =
            "url{\n" +
                "        takeFrom(_ktorfit.baseUrl + \"posts\")\n" +
                "        parameters.appendAll(\"\$testQueryName\", emptyList())\n" +
                "        parameters.appendAll(\"\$testQueryName2\", emptyList())\n" +
                "        }"

        val compilation = getCompilation(listOf(source))
        val result = compilation.compile()

        val generatedSourcesDir = compilation.kspSourcesDir
        val generatedFile =
            File(
                generatedSourcesDir,
                "/kotlin/com/example/api/_TestServiceImpl.kt",
            )

        val actualSource = generatedFile.readText()
        assertTrue(actualSource.contains(expectedQueriesArgumentText))
    }

    @Test
    fun whenNotEncodedQueryMapAnnotationAndEncodedFound() {
        val source =
            SourceFile.kotlin(
                "Source.kt",
                contents = """package com.example.api
            import de.jensklingenberg.ktorfit.http.GET
            import de.jensklingenberg.ktorfit.http.QueryMap
            
            interface TestService {
            
            @GET("posts")
            suspend fun test(@QueryMap testQueryMap: Map<String, String>,@QueryMap(true) testQueryMap2: Map<String, Int?>)
                
            }
                """,
            )

        val expectedQueriesArgumentText =
            "url{\n" +
                "        takeFrom(_ktorfit.baseUrl + \"posts\")\n" +
                "        testQueryMap?.forEach { entry -> entry.value?.let{ parameter(entry.key, \"\${entry.value}\") } }\n" +
                "        testQueryMap2?.forEach { entry -> entry.value?.let{ encodedParameters.append(entry.key, \"\${entry.value}\") } }\n" +
                "        }"

        val compilation = getCompilation(listOf(source))
        val result = compilation.compile()

        val generatedSourcesDir = compilation.kspSourcesDir
        val generatedFile =
            File(
                generatedSourcesDir,
                "/kotlin/com/example/api/_TestServiceImpl.kt",
            )

        val actualSource = generatedFile.readText()
        assertTrue(actualSource.contains(expectedQueriesArgumentText))
    }

    @Test
    fun testFunctionWithQueryAndQueryNameAndQueryMap() {
        val source =
            SourceFile.kotlin(
                "Source.kt",
                """package com.example.api
import de.jensklingenberg.ktorfit.http.GET
import de.jensklingenberg.ktorfit.http.QueryMap
import de.jensklingenberg.ktorfit.http.Query
import de.jensklingenberg.ktorfit.http.QueryName

interface TestService {
@GET("posts")
fun example(@Query("name") testQuery: String, @QueryName testQueryName: String, @QueryMap(true) name: Map<String, String>) 
}
    """,
            )

        val expectedQueriesArgumentText =
            "url{\n" +
                "        takeFrom(_ktorfit.baseUrl + \"posts\")\n" +
                "        testQuery?.let{ parameter(\"name\", \"\$it\") }\n" +
                "        parameters.appendAll(\"\$testQueryName\", emptyList())\n" +
                "        name?.forEach { entry -> entry.value?.let{ encodedParameters.append(entry.key, \"\${entry.value}\") } }\n" +
                "        }"

        val compilation = getCompilation(listOf(source))
        val result = compilation.compile()

        val generatedSourcesDir = compilation.kspSourcesDir
        val generatedFile =
            File(
                generatedSourcesDir,
                "/kotlin/com/example/api/_TestServiceImpl.kt",
            )

        val actualSource = generatedFile.readText()
        assertTrue(actualSource.contains(expectedQueriesArgumentText))
    }

    @Test
    fun whenQueryMapTypeIsNotMap_ThrowCompilationError() {
        val source =
            SourceFile.kotlin(
                "Source.kt",
                """
      package com.example.api
import de.jensklingenberg.ktorfit.http.GET
import de.jensklingenberg.ktorfit.http.QueryMap

interface TestService {

    @GET("posts")
    suspend fun test(@QueryMap testQueryMap: String)
    
}
    """,
            )

        val compilation = getCompilation(listOf(source))

        val result = compilation.compile()

        assertTrue(result.messages.contains(KtorfitError.QUERY_MAP_PARAMETER_TYPE_MUST_BE_MAP))
    }

    @Test
    fun whenQueryMapKeysIsNotString_ThrowCompilationError() {
        val source =
            SourceFile.kotlin(
                "Source.kt",
                """
      package com.example.api
import de.jensklingenberg.ktorfit.http.GET
import de.jensklingenberg.ktorfit.http.QueryMap

interface TestService {

    @GET("posts")
    suspend fun test(@QueryMap() testQueryMap: Map<Int, String>)
    
}
    """,
            )

        val compilation = getCompilation(listOf(source))

        val result = compilation.compile()
        assertEquals(KotlinCompilation.ExitCode.COMPILATION_ERROR, result.exitCode)
        assertTrue(result.messages.contains(KtorfitError.QUERY_MAP_KEYS_MUST_BE_OF_TYPE_STRING))
    }
}
