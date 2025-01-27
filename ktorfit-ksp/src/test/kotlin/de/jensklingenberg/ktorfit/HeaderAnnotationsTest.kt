package de.jensklingenberg.ktorfit

import com.tschuchort.compiletesting.SourceFile
import com.tschuchort.compiletesting.kspSourcesDir
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import java.io.File

class HeaderAnnotationsTest {
    @Test
    fun whenNoHeaderAnnotationsFound_KeepHeadersArgumentEmpty() {
        val source =
            SourceFile.kotlin(
                "Source.kt",
                """
      package com.example.api
import de.jensklingenberg.ktorfit.http.GET
import de.jensklingenberg.ktorfit.http.Path

class Test<T>

interface TestService {
@GET("posts")
suspend fun test(): List<Triple<String,Int?,String>>
}
    """,
            )

        val source2 =
            SourceFile.kotlin(
                "Source.kt",
                """
      package com.example.api2
    """,
            )

        val notExpectedHeadersArgumentText = "headers{"

        val compilation = getCompilation(listOf(source2, source))
        val result = compilation.compile()

        val generatedSourcesDir = compilation.kspSourcesDir
        val generatedFile =
            File(
                generatedSourcesDir,
                "/kotlin/com/example/api/_TestServiceImpl.kt",
            )
        val actualSource = generatedFile.readText()
        assertFalse(actualSource.contains(notExpectedHeadersArgumentText))
    }

    @Test
    fun whenHeaderAnnotationWithNonNullableStringFound_AddHeader() {
        val source =
            SourceFile.kotlin(
                "Source.kt",
                """
      package com.example.api
import de.jensklingenberg.ktorfit.http.GET
import de.jensklingenberg.ktorfit.http.Header

interface TestService {
    @GET("posts")
    suspend fun test(@Header("testHeader") testParameter: String): String
}
    """,
            )

        val expectedHeadersArgumentText =
            "headers{\n" +
                    "        append(\"testHeader\", testParameter)\n" +
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
        assertTrue(actualSource.contains(expectedHeadersArgumentText))
    }

    @Test
    fun whenHeaderAnnotationWithNullableStringFound_AddHeader() {
        val source =
            SourceFile.kotlin(
                "Source.kt",
                """
      package com.example.api
import de.jensklingenberg.ktorfit.http.GET
import de.jensklingenberg.ktorfit.http.Header

interface TestService {
    @GET("posts")
    suspend fun test(@Header("testHeader") testParameter: String?): String
}
    """,
            )

        val expectedHeadersArgumentText =
            "headers{\n" +
                    "        testParameter?.let{ append(\"testHeader\", testParameter) }\n" +
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
        assertTrue(actualSource.contains(expectedHeadersArgumentText))
    }

    @Test
    fun whenHeaderAnnotationWithNonNullableIntFound_AddHeader() {
        val source =
            SourceFile.kotlin(
                "Source.kt",
                """
      package com.example.api
import de.jensklingenberg.ktorfit.http.GET
import de.jensklingenberg.ktorfit.http.Header

interface TestService {
    @GET("posts")
    suspend fun test(@Header("testHeader") testParameter: Int): String
}
    """,
            )

        val expectedHeadersArgumentText = """headers{
        append("testHeader", "${"$"}testParameter")
        }"""

        val compilation = getCompilation(listOf(source))
        val result = compilation.compile()

        val generatedSourcesDir = compilation.kspSourcesDir
        val generatedFile =
            File(
                generatedSourcesDir,
                "/kotlin/com/example/api/_TestServiceImpl.kt",
            )

        val actualSource = generatedFile.readText()
        assertTrue(actualSource.contains(expectedHeadersArgumentText))
    }

    @Test
    fun whenHeaderAnnotationWithNullableIntFound_AddHeader() {
        val source =
            SourceFile.kotlin(
                "Source.kt",
                """
      package com.example.api
import de.jensklingenberg.ktorfit.http.GET
import de.jensklingenberg.ktorfit.http.Header

interface TestService {
    @GET("posts")
    suspend fun test(@Header("testHeader") testParameter: Int?): String
}
    """,
            )

        val expectedHeadersArgumentText = """headers{
        testParameter?.let{ append("testHeader", "${"$"}testParameter") }
        }"""

        val compilation = getCompilation(listOf(source))
        val result = compilation.compile()

        val generatedSourcesDir = compilation.kspSourcesDir
        val generatedFile =
            File(
                generatedSourcesDir,
                "/kotlin/com/example/api/_TestServiceImpl.kt",
            )

        val actualSource = generatedFile.readText()
        assertTrue(actualSource.contains(expectedHeadersArgumentText))
    }

    @Test
    fun whenHeaderAnnotationWithListTypeFound_AddHeader() {
        val source =
            SourceFile.kotlin(
                "Source.kt",
                """
      package com.example.api
import de.jensklingenberg.ktorfit.http.GET
import de.jensklingenberg.ktorfit.http.Header

interface TestService {
    @GET("posts")
    suspend fun test(@Header("testHeader") testParameter: List<Int>): String
}
    """,
            )

        val expectedHeadersArgumentText =
            "headers{\n" +
                    "        testParameter.forEach{ append(\"testHeader\", \"\$it\")}\n" +
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
        assertTrue(actualSource.contains(expectedHeadersArgumentText))
    }

    @Test
    fun whenHeaderAnnotationWithNullableListTypeFound_AddHeader() {
        val source =
            SourceFile.kotlin(
                "Source.kt",
                """
      package com.example.api
import de.jensklingenberg.ktorfit.http.GET
import de.jensklingenberg.ktorfit.http.Header

interface TestService {
    @GET("posts")
    suspend fun test(@Header("testHeader") testParameter: List<Int>?): String
}
    """,
            )

        val expectedHeadersArgumentText =
            "headers{\n" +
                    "        testParameter?.forEach{ append(\"testHeader\", \"\$it\")}\n" +
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
        assertTrue(actualSource.contains(expectedHeadersArgumentText))
    }

    @Test
    fun whenHeaderAnnotationWithNullableListTypeAndNullableIntFound_AddHeader() {
        val source =
            SourceFile.kotlin(
                "Source.kt",
                """
      package com.example.api
import de.jensklingenberg.ktorfit.http.GET
import de.jensklingenberg.ktorfit.http.Header

interface TestService {
    @GET("posts")
    suspend fun test(@Header("testHeader") testParameter: List<Int?>?): String
}
    """,
            )

        val expectedHeadersArgumentText =
            "headers{\n" +
                    "        testParameter?.filterNotNull()?.forEach{ append(\"testHeader\", \"\$it\")}\n" +
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
        assertTrue(actualSource.contains(expectedHeadersArgumentText))
    }

    @Test
    fun whenHeaderAnnotationWithArrayTypeFound_AddHeader() {
        val source =
            SourceFile.kotlin(
                "Source.kt",
                """
      package com.example.api
import de.jensklingenberg.ktorfit.http.GET
import de.jensklingenberg.ktorfit.http.Header

interface TestService {

    @GET("posts")
    suspend fun test(@Header("testHeader") testParameter: Array<String>): String
    
}
    """,
            )

        val expectedHeadersArgumentText = """headers{
        testParameter.forEach{ append("testHeader", it)}
        }"""

        val compilation = getCompilation(listOf(source))
        val result = compilation.compile()


        val generatedSourcesDir = compilation.kspSourcesDir
        val generatedFile =
            File(
                generatedSourcesDir,
                "/kotlin/com/example/api/_TestServiceImpl.kt",
            )

        val actualSource = generatedFile.readText()
        assertTrue(actualSource.contains(expectedHeadersArgumentText))
    }
}
