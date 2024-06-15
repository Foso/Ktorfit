package de.jensklingenberg.ktorfit

import com.tschuchort.compiletesting.KotlinCompilation
import com.tschuchort.compiletesting.SourceFile
import com.tschuchort.compiletesting.kspSourcesDir
import de.jensklingenberg.ktorfit.model.KtorfitError
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import java.io.File

class FieldAnnotationsTest {
    @Test
    fun whenNoFieldAnnotationsFound_KeepFieldsBuilderEmpty() {
        val source =
            SourceFile.kotlin(
                "Source.kt",
                """
      package com.example.api
import de.jensklingenberg.ktorfit.http.GET

interface TestService {

    @GET("posts")
    suspend fun test(): Map<String,Int>
    
}
    """,
            )

        val expectedFieldsBuilderText = "val _formParameters ="

        val compilation = getCompilation(listOf(source))
        val result = compilation.compile()
        assertEquals(KotlinCompilation.ExitCode.OK, result.exitCode)

        val generatedSourcesDir = compilation.kspSourcesDir
        val generatedFile =
            File(
                generatedSourcesDir,
                "/kotlin/com/example/api/_TestServiceImpl.kt",
            )
        assertTrue(generatedFile.exists())
        assertFalse(generatedFile.readText().contains(expectedFieldsBuilderText))
    }

    @Test
    fun whenFieldUsedWithoutFormEncoding_ThrowCompilationError() {
        val source =
            SourceFile.kotlin(
                "Source.kt",
                """
      package com.example.api
import de.jensklingenberg.ktorfit.http.GET
import de.jensklingenberg.ktorfit.http.Field

interface TestService {
    @GET("posts")
    suspend fun test(@Field("name") testField: String)
}
    """,
            )

        val compilation = getCompilation(listOf(source))
        val result = compilation.compile()
        assertEquals(KotlinCompilation.ExitCode.COMPILATION_ERROR, result.exitCode)
        assertTrue(result.messages.contains(KtorfitError.FIELD_PARAMETERS_CAN_ONLY_BE_USED_WITH_FORM_ENCODING))
    }

    @Test
    fun whenFieldAnnotationFoundAddItToFieldsBuilder() {
        val source =
            SourceFile.kotlin(
                "Source.kt",
                """
      package com.example.api
import de.jensklingenberg.ktorfit.http.POST
import de.jensklingenberg.ktorfit.http.Field
import de.jensklingenberg.ktorfit.http.FormUrlEncoded

interface TestService {
    @FormUrlEncoded
    @POST("posts")
    suspend fun test(@Field("name") testField: String)
}
    """,
            )

        val expectedFieldsBuilderText =
            """val __formParameters = Parameters.build {
        testField?.let{ append("name", "$/{it}") }
        }
        setBody(FormDataContent(__formParameters))
            """.trimMargin().replace("$/", "$")

        val compilation = getCompilation(listOf(source))
        val result = compilation.compile()
        assertEquals(KotlinCompilation.ExitCode.OK, result.exitCode)
        val generatedSourcesDir = compilation.kspSourcesDir
        val generatedFile =
            File(
                generatedSourcesDir,
                "/kotlin/com/example/api/_TestServiceImpl.kt",
            )
        val actualSource = generatedFile.readText()
        assertTrue(actualSource.contains(expectedFieldsBuilderText))
    }

    @Test
    fun whenFieldAnnotationWitDefaultValueFoundAddItToFieldsBuilder() {
        val source =
            SourceFile.kotlin(
                "Source.kt",
                """
      package com.example.api
import de.jensklingenberg.ktorfit.http.POST
import de.jensklingenberg.ktorfit.http.Field
import de.jensklingenberg.ktorfit.http.FormUrlEncoded

interface TestService {
    @FormUrlEncoded
    @POST("posts")
    suspend fun test(@Field name: String)
}
    """,
            )

        val expectedFieldsBuilderText =
            """val __formParameters = Parameters.build {
        name?.let{ append("name", "$/{it}") }
        }
        setBody(FormDataContent(__formParameters))
            """.trimMargin().replace("$/", "$")

        val compilation = getCompilation(listOf(source))
        val result = compilation.compile()
        assertEquals(KotlinCompilation.ExitCode.OK, result.exitCode)
        val generatedSourcesDir = compilation.kspSourcesDir
        val generatedFile =
            File(
                generatedSourcesDir,
                "/kotlin/com/example/api/_TestServiceImpl.kt",
            )
        val actualSource = generatedFile.readText()
        assertTrue(actualSource.contains(expectedFieldsBuilderText))
    }

    @Test
    fun whenFieldAnnotationWithListFoundAddItToFieldsBuilder() {
        val source =
            SourceFile.kotlin(
                "Source.kt",
                """
      package com.example.api
import de.jensklingenberg.ktorfit.http.POST
import de.jensklingenberg.ktorfit.http.Field
import de.jensklingenberg.ktorfit.http.FormUrlEncoded

interface TestService {
    @FormUrlEncoded
    @POST("posts")
    suspend fun test(@Field("name") testField: List<String>)
}
    """,
            )

        val expectedFieldsBuilderText =
            """val __formParameters = Parameters.build {
        testField?.filterNotNull()?.forEach { append("name", "$/it") }
        }
        setBody(FormDataContent(__formParameters))
            """.trimMargin().replace("$/", "$")

        val compilation = getCompilation(listOf(source))
        val result = compilation.compile()
        assertEquals(KotlinCompilation.ExitCode.OK, result.exitCode)
        val generatedSourcesDir = compilation.kspSourcesDir
        val generatedFile =
            File(
                generatedSourcesDir,
                "/kotlin/com/example/api/_TestServiceImpl.kt",
            )
        val actualSource = generatedFile.readText()
        assertTrue(actualSource.contains(expectedFieldsBuilderText))
    }

    @Test
    fun whenFieldAnnotationWithArrayFoundAddItToFieldsBuilder() {
        val source =
            SourceFile.kotlin(
                "Source.kt",
                """
      package com.example.api
import de.jensklingenberg.ktorfit.http.POST
import de.jensklingenberg.ktorfit.http.Field
import de.jensklingenberg.ktorfit.http.FormUrlEncoded

interface TestService {
    @FormUrlEncoded
    @POST("posts")
    suspend fun test(@Field("name") testField: Array<String>)
}
    """,
            )

        val expectedFieldsBuilderText =
            """val __formParameters = Parameters.build {
        testField?.filterNotNull()?.forEach { append("name", "$/it") }
        }
        setBody(FormDataContent(__formParameters))
            """.trimMargin().replace("$/", "$")

        val compilation = getCompilation(listOf(source))
        val result = compilation.compile()
        assertEquals(KotlinCompilation.ExitCode.OK, result.exitCode)
        val generatedSourcesDir = compilation.kspSourcesDir
        val generatedFile =
            File(
                generatedSourcesDir,
                "/kotlin/com/example/api/_TestServiceImpl.kt",
            )
        val actualSource = generatedFile.readText()
        assertTrue(actualSource.contains(expectedFieldsBuilderText))
    }

    @Test
    fun whenFieldMapAnnotationFoundAddItToFieldsBuilder() {
        val source =
            SourceFile.kotlin(
                "Source.kt",
                """
      package com.example.api
import de.jensklingenberg.ktorfit.http.FieldMap
import de.jensklingenberg.ktorfit.http.FormUrlEncoded
import de.jensklingenberg.ktorfit.http.POST

interface TestService {
    @FormUrlEncoded
    @POST("posts")
    suspend fun test(@FieldMap() testFieldMap: Map<String, String>)
    
}
    """,
            )

        val expectedFieldsBuilderText =
            "val __formParameters = Parameters.build {\n" +
                "        testFieldMap?.forEach { entry -> entry.value?.let{ append(entry.key, \"$" + "{entry.value}\") } }\n" +
                "        }"

        val compilation = getCompilation(listOf(source))
        val result = compilation.compile()
        assertEquals(KotlinCompilation.ExitCode.OK, result.exitCode)
        val generatedSourcesDir = compilation.kspSourcesDir
        val generatedFile =
            File(
                generatedSourcesDir,
                "/kotlin/com/example/api/_TestServiceImpl.kt",
            )
        assertTrue(generatedFile.exists())
        val actualSource = generatedFile.readText()
        assertTrue(actualSource.contains(expectedFieldsBuilderText))
    }

    @Test
    fun testFunctionWithFieldAndFieldMap() {
        val source =
            SourceFile.kotlin(
                "Source.kt",
                """
      package com.example.api
import de.jensklingenberg.ktorfit.http.POST
import de.jensklingenberg.ktorfit.http.FieldMap
import de.jensklingenberg.ktorfit.http.Field
import de.jensklingenberg.ktorfit.http.FormUrlEncoded

interface TestService {

    @FormUrlEncoded
    @POST("posts")
   fun example(@Field("name") testField: String, @FieldMap() name: Map<String, String>)
    
}
    """,
            )

        val expectedFieldsBuilderText =
            """val __formParameters = Parameters.build {
        testField?.let{ append("name", "$/{it}") }
        name?.forEach { entry -> entry.value?.let{ append(entry.key, "$/{entry.value}") } }
        }
        setBody(FormDataContent(__formParameters))
            """.trimMargin().replace("$/", "$")

        val compilation = getCompilation(listOf(source))
        val result = compilation.compile()
        assertEquals(KotlinCompilation.ExitCode.OK, result.exitCode)
        val generatedSourcesDir = compilation.kspSourcesDir
        val generatedFile =
            File(
                generatedSourcesDir,
                "/kotlin/com/example/api/_TestServiceImpl.kt",
            )
        val actualSource = generatedFile.readText()
        assertTrue(actualSource.contains(expectedFieldsBuilderText))
    }

    @Test
    fun whenFieldMapTypeIsNotMap_ThrowCompilationError() {
        val source =
            SourceFile.kotlin(
                "Source.kt",
                """
      package com.example.api
import de.jensklingenberg.ktorfit.http.POST
import de.jensklingenberg.ktorfit.http.FieldMap
import de.jensklingenberg.ktorfit.http.FormUrlEncoded

interface TestService {

    @FormUrlEncoded
    @POST("posts")
   fun example(@FieldMap() name: String)
    
}
    """,
            )

        val compilation = getCompilation(listOf(source))

        val result = compilation.compile()
        assertEquals(KotlinCompilation.ExitCode.COMPILATION_ERROR, result.exitCode)
        assertTrue(result.messages.contains(KtorfitError.FIELD_MAP_PARAMETER_TYPE_MUST_BE_MAP))
    }

    @Test
    fun whenFieldMapKeysIsNotString_ThrowCompilationError() {
        val source =
            SourceFile.kotlin(
                "Source.kt",
                """
      package com.example.api
import de.jensklingenberg.ktorfit.http.POST
import de.jensklingenberg.ktorfit.http.FieldMap
import de.jensklingenberg.ktorfit.http.FormUrlEncoded

interface TestService {

    @FormUrlEncoded
    @POST("posts")
   fun example(@FieldMap name: Map<Int,String>)
    
}
    """,
            )

        val compilation = getCompilation(listOf(source))

        val result = compilation.compile()
        assertEquals(KotlinCompilation.ExitCode.COMPILATION_ERROR, result.exitCode)
        assertTrue(result.messages.contains(KtorfitError.FIELD_MAP_KEYS_MUST_BE_OF_TYPE_STRING))
    }
}
