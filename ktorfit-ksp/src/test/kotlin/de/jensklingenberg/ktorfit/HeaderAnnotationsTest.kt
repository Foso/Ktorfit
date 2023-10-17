package de.jensklingenberg.ktorfit

import com.tschuchort.compiletesting.KotlinCompilation
import com.tschuchort.compiletesting.SourceFile
import com.tschuchort.compiletesting.kspSourcesDir
import de.jensklingenberg.ktorfit.model.KtorfitError.Companion.HEADER_MAP_KEYS_MUST_BE_OF_TYPE_STRING
import de.jensklingenberg.ktorfit.model.KtorfitError.Companion.HEADER_MAP_PARAMETER_TYPE_MUST_BE_MAP
import org.junit.Assert.*
import org.junit.Test
import java.io.File

class HeaderAnnotationsTest {

    @Test
    fun whenNoHeaderAnnotationsFound_KeepHeadersArgumentEmpty() {

        val source = SourceFile.kotlin(
            "Source.kt", """
      package com.example.api
import de.jensklingenberg.ktorfit.http.GET
import de.jensklingenberg.ktorfit.http.Path


class Test<T>

interface TestService {
@GET("posts")
suspend fun test(): List<Triple<String,Int?,String>>
}
    """
        )

        val source2 = SourceFile.kotlin(
            "Source.kt", """
      package com.example.api2
    """
        )

        val notExpectedHeadersArgumentText = "headers{"

        val compilation = getCompilation(listOf(source2, source))
        val result = compilation.compile()
        assertEquals(KotlinCompilation.ExitCode.OK, result.exitCode)
        val generatedSourcesDir = compilation.kspSourcesDir
        val generatedFile = File(
            generatedSourcesDir,
            "/kotlin/com/example/api/_TestServiceImpl.kt"
        )
        val actualSource = generatedFile.readText()
        assertFalse(actualSource.contains(notExpectedHeadersArgumentText))
    }


    @Test
    fun whenHeadersAnnotationFound_AddHeader() {

        val source = SourceFile.kotlin(
            "Source.kt", """
      package com.example.api
import de.jensklingenberg.ktorfit.http.GET
import de.jensklingenberg.ktorfit.http.Headers

interface TestService {

@Headers(value = ["x:y","a:b"])
@GET("posts")
suspend fun test(): String
}
    """
        )


        val expectedHeadersArgumentText = "headers{\n" +
                "        append(\"x\", \"y\")\n" +
                "        append(\"a\", \"b\")\n" +
                "        } "

        val compilation = getCompilation(listOf(source))
        val result = compilation.compile()
        assertEquals(KotlinCompilation.ExitCode.OK, result.exitCode)
        val generatedSourcesDir = compilation.kspSourcesDir
        val generatedFile = File(
            generatedSourcesDir,
            "/kotlin/com/example/api/_TestServiceImpl.kt"
        )
        val actualSource = generatedFile.readText()
        assertTrue(actualSource.contains(expectedHeadersArgumentText))
    }

    @Test
    fun whenHeadersHeaderMapAndHeaderAnnotationFound_AddHeader() {

        val source = SourceFile.kotlin(
            "Source.kt", """package com.example.api
import de.jensklingenberg.ktorfit.http.GET
import de.jensklingenberg.ktorfit.http.Headers
import de.jensklingenberg.ktorfit.http.Header
import de.jensklingenberg.ktorfit.http.HeaderMap


interface TestService {
@Headers(value = ["x:y","a:b"])
@GET("posts")
suspend fun test(@Header("testHeader") testParameterNonNullable: String?, @Header("testHeader") testParameterNullable: String?, @HeaderMap("testHeaderMap") testParameter2: Map<String,String>): String
}
    """
        )


        val expectedHeadersArgumentText = "headers{\n" +
                "        testParameter?.let{ append(\"testHeader\", \"$" + "it\") }\n" +
                "        testParameter2?.forEach { append(it.key, \"$" + "{it.value}\") }\n" +
                "        append(\"x\", \"y\")\n" +
                "        append(\"a\", \"b\")\n" +
                "        } "

        val compilation = getCompilation(listOf(source))
        val result = compilation.compile()
        assertEquals(KotlinCompilation.ExitCode.OK, result.exitCode)
        val generatedSourcesDir = compilation.kspSourcesDir
        val generatedFile = File(
            generatedSourcesDir,
            "/kotlin/com/example/api/_TestServiceImpl.kt"
        )

        val actualSource = generatedFile.readText()
        assertTrue(actualSource.contains(expectedHeadersArgumentText))
    }


    @Test
    fun wheen() {

        val source = SourceFile.kotlin(
            "Source.kt", """package com.example.api
import de.jensklingenberg.ktorfit.http.GET
import de.jensklingenberg.ktorfit.http.Headers
import de.jensklingenberg.ktorfit.http.Header
import de.jensklingenberg.ktorfit.http.HeaderMap


interface TestService {
@Headers(value = ["x:y","a:b"])
@GET("posts")
suspend fun test(@Header("testHeader") testParameterNonNullable: List<String?>?, @HeaderMap("testHeaderMap") testParameter2: Map<String,String>): String
}
    """
        )


        val expectedHeadersArgumentText = "headers{\n" +
                "        testParameter?.let{ append(\"testHeader\", \"$" + "it\") }\n" +
                "        testParameter2?.forEach { append(it.key, \"$" + "{it.value}\") }\n" +
                "        append(\"x\", \"y\")\n" +
                "        append(\"a\", \"b\")\n" +
                "        } "

        val compilation = getCompilation(listOf(source))
        val result = compilation.compile()
        assertEquals(KotlinCompilation.ExitCode.OK, result.exitCode)
        val generatedSourcesDir = compilation.kspSourcesDir
        val generatedFile = File(
            generatedSourcesDir,
            "/kotlin/com/example/api/_TestServiceImpl.kt"
        )

        val actualSource = generatedFile.readText()
        assertTrue(actualSource.contains(expectedHeadersArgumentText))
    }


    @Test
    fun whenHeaderAnnotationFound_AddHeader() {

        val source = SourceFile.kotlin(
            "Source.kt", """
      package com.example.api
import de.jensklingenberg.ktorfit.http.GET
import de.jensklingenberg.ktorfit.http.Header

interface TestService {

    @GET("posts")
    suspend fun test(@Header("testHeader") testParameter: String): String
    
}
    """
        )


        val expectedHeadersArgumentText = "headers{\n" +
                "        testParameter?.let{ append(\"testHeader\", \"$" + "it\") }\n" +
                "        }"

        val compilation = getCompilation(listOf(source))
        val result = compilation.compile()
        assertEquals(KotlinCompilation.ExitCode.OK, result.exitCode)
        val generatedSourcesDir = compilation.kspSourcesDir
        val generatedFile = File(
            generatedSourcesDir,
            "/kotlin/com/example/api/_TestServiceImpl.kt"
        )

        val actualSource = generatedFile.readText()
        assertTrue(actualSource.contains(expectedHeadersArgumentText))
    }

    @Test
    fun whenHeaderAnnotationWithListTypeFound_AddHeader() {

        val source = SourceFile.kotlin(
            "Source.kt", """
      package com.example.api
import de.jensklingenberg.ktorfit.http.GET
import de.jensklingenberg.ktorfit.http.Header

interface TestService {

    @GET("posts")
    suspend fun test(@Header("testHeader") testParameter: List<String>): String
    
}
    """
        )


        val expectedHeadersArgumentText = "headers{\n" +
                "        testParameter?.filterNotNull()?.forEach { append(\"testHeader\", \"\$it\") }\n" +
                "        } "

        val compilation = getCompilation(listOf(source))
        val result = compilation.compile()
        assertEquals(KotlinCompilation.ExitCode.OK, result.exitCode)

        val generatedSourcesDir = compilation.kspSourcesDir
        val generatedFile = File(
            generatedSourcesDir,
            "/kotlin/com/example/api/_TestServiceImpl.kt"
        )

        val actualSource = generatedFile.readText()
        assertTrue(actualSource.contains(expectedHeadersArgumentText))
    }

    @Test
    fun whenHeaderAnnotationWithArrayTypeFound_AddHeader() {

        val source = SourceFile.kotlin(
            "Source.kt", """
      package com.example.api
import de.jensklingenberg.ktorfit.http.GET
import de.jensklingenberg.ktorfit.http.Header

interface TestService {

    @GET("posts")
    suspend fun test(@Header("testHeader") testParameter: Array<String>): String
    
}
    """
        )


        val expectedHeadersArgumentText = "headers{\n" +
                "        testParameter?.filterNotNull()?.forEach { append(\"testHeader\", \"\$it\") }\n" +
                "        }"

        val compilation = getCompilation(listOf(source))
        val result = compilation.compile()
        assertEquals(KotlinCompilation.ExitCode.OK, result.exitCode)

        val generatedSourcesDir = compilation.kspSourcesDir
        val generatedFile = File(
            generatedSourcesDir,
            "/kotlin/com/example/api/_TestServiceImpl.kt"
        )

        val actualSource = generatedFile.readText()
        assertTrue(actualSource.contains(expectedHeadersArgumentText))
    }

    @Test
    fun whenHeaderMapAnnotationFound_AddHeader() {

        val source = SourceFile.kotlin(
            "Source.kt", """
      package com.example.api
import de.jensklingenberg.ktorfit.http.GET
import de.jensklingenberg.ktorfit.http.HeaderMap

interface TestService {

    @GET("posts")
    suspend fun test(@HeaderMap("testHeader") testParameter: Map<String,String>): String
    
}
    """
        )


        val expectedHeadersArgumentText = "headers{\n" +
                "        testParameter?.forEach { append(it.key, \"\${it.value}\") }\n" +
                "        }"

        val compilation = getCompilation(listOf(source))
        val result = compilation.compile()
        assertEquals(KotlinCompilation.ExitCode.OK, result.exitCode)
        val generatedSourcesDir = compilation.kspSourcesDir
        val generatedFile = File(
            generatedSourcesDir,
            "/kotlin/com/example/api/_TestServiceImpl.kt"
        )

        val actualSource = generatedFile.readText()
        assertTrue(actualSource.contains(expectedHeadersArgumentText))
    }

    @Test
    fun whenHeaderMapTypeIsNotMap_ThrowCompilationError() {

        val source = SourceFile.kotlin(
            "Source.kt", """
      package com.example.api
import de.jensklingenberg.ktorfit.http.GET
import de.jensklingenberg.ktorfit.http.HeaderMap

interface TestService {

    @GET("posts")
    suspend fun test(@HeaderMap("testHeader") testParameter: String): String
    
}
    """
        )

        val compilation = getCompilation(listOf(source))
        val result = compilation.compile()
        assertEquals(KotlinCompilation.ExitCode.COMPILATION_ERROR, result.exitCode)

        assertTrue(result.messages.contains(HEADER_MAP_PARAMETER_TYPE_MUST_BE_MAP))
    }

    @Test
    fun whenHeaderMapKeysIsNotString_ThrowCompilationError() {

        val source = SourceFile.kotlin(
            "Source.kt", """
      package com.example.api
import de.jensklingenberg.ktorfit.http.GET
import de.jensklingenberg.ktorfit.http.HeaderMap

interface TestService {

    @GET("posts")
    suspend fun test(@HeaderMap("testHeader") testParameter: Map<Int,String>): String
    
}
    """
        )

        val compilation = getCompilation(listOf(source))
        val result = compilation.compile()
        assertEquals(KotlinCompilation.ExitCode.COMPILATION_ERROR, result.exitCode)
        assertTrue(result.messages.contains(HEADER_MAP_KEYS_MUST_BE_OF_TYPE_STRING))
    }


}

