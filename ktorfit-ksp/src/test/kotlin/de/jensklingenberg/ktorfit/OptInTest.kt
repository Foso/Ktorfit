package de.jensklingenberg.ktorfit

import com.tschuchort.compiletesting.SourceFile
import com.tschuchort.compiletesting.kspSourcesDir
import org.junit.Assert.assertTrue
import org.junit.Test
import java.io.File

class OptInTest {
    @Test
    fun `when OptIn annotation is used add it to the implementation class`() {
        val source =
            SourceFile.kotlin(
                "Source.kt",
                """package com.example.api
import de.jensklingenberg.ktorfit.http.GET
import de.jensklingenberg.ktorfit.http.Headers
import de.jensklingenberg.ktorfit.http.Header
import de.jensklingenberg.ktorfit.http.HeaderMap
import org.jetbrains.kotlin.compiler.plugin.ExperimentalCompilerApi

@OptIn(ExperimentalCompilerApi::class)
interface TestService {
    @Headers(value = ["x:y","a:b"])
    @GET("posts")
    @OptIn(ExperimentalCompilerApi::class)
    suspend fun test(@Header("testHeader") testParameterNonNullable: String?, @Header("testHeader") testParameterNullable: String?, @HeaderMap("testHeaderMap") testParameter2: Map<String,String>): String
}
    """,
            )

        val expectedHeadersArgumentText =
            """@OptIn(ExperimentalCompilerApi::class, InternalKtorfitApi::class)
public class _TestServiceImpl(
  private val _ktorfit: Ktorfit,
) : TestService {
  private val _helper: KtorfitConverterHelper = KtorfitConverterHelper(_ktorfit)

  @OptIn(ExperimentalCompilerApi::class)
  override suspend fun test("""

        val compilation = getCompilation(listOf(source))

        val generatedSourcesDir = compilation.apply { compile() }.kspSourcesDir
        val generatedFile =
            File(
                generatedSourcesDir,
                "/kotlin/com/example/api/_TestServiceImpl.kt",
            )

        val actualSource = generatedFile.readText()
        assertTrue(actualSource.contains(expectedHeadersArgumentText))
    }

    @Test
    fun `when OptIn annotation are add to the implementation class do not repeat marker classes`() {
        val source =
            SourceFile.kotlin(
                "Source.kt",
                """package com.example.api
import de.jensklingenberg.ktorfit.http.GET
import de.jensklingenberg.ktorfit.http.Headers
import de.jensklingenberg.ktorfit.http.Header
import de.jensklingenberg.ktorfit.http.HeaderMap
import org.jetbrains.kotlin.compiler.plugin.ExperimentalCompilerApi

@OptIn(ExperimentalCompilerApi::class)
interface TestService {
    @GET("posts")
    @OptIn(ExperimentalCompilerApi::class)
    suspend fun test1()
    
    @GET("posts")
    @OptIn(ExperimentalCompilerApi::class)
    suspend fun test2()
}
    """,
            )

        val expectedHeadersArgumentText =
            """@OptIn(ExperimentalCompilerApi::class, InternalKtorfitApi::class)
public class _TestServiceImpl(
  private val _ktorfit: Ktorfit,
) : TestService {"""

        val compilation = getCompilation(listOf(source))

        val generatedSourcesDir = compilation.apply { compile() }.kspSourcesDir
        val generatedFile =
            File(
                generatedSourcesDir,
                "/kotlin/com/example/api/_TestServiceImpl.kt",
            )

        val actualSource = generatedFile.readText()
        assertTrue(actualSource.contains(expectedHeadersArgumentText))
    }
}
