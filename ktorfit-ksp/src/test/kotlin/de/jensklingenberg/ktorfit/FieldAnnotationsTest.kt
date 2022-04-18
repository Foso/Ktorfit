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

class FieldAnnotationsTest {


    @Test
    fun whenNoFieldAnnotationsFound_KeepFieldsArgumentEmpty() {

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


        val expectedFunctionText = "fields ="

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
    fun whenFieldUsedWithoutFormEncoding_ThrowCompilationError() {

        val source = SourceFile.kotlin(
            "CustomCallable.kt", """
      package com.example.api
import de.jensklingenberg.ktorfit.http.GET
import de.jensklingenberg.ktorfit.http.Field

interface TestService {
    @GET("posts")
    suspend fun test(@Field("name") testField: String)
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
        Assert.assertTrue(result.messages.contains(KtorfitError.FIELD_PARAMETERS_CAN_ONLY_BE_USED_WITH_FORM_ENCODING))
    }


    @Test
    fun whenFieldAnnotationFoundAddItToFieldsArgument() {

        val source = SourceFile.kotlin(
            "CustomCallable.kt", """
      package com.example.api
import de.jensklingenberg.ktorfit.http.POST
import de.jensklingenberg.ktorfit.http.Field
import de.jensklingenberg.ktorfit.http.FormUrlEncoded

interface TestService {
    @FormUrlEncoded
    @POST("posts")
    suspend fun test(@Field("name") testField: String)
}
    """
        )

        val expected = "fields = listOf(FieldData(false,testField,\"name\",FieldType.FIELD))"

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
    fun whenFieldMapAnnotationFoundAddItToFieldsArgument() {

        val source = SourceFile.kotlin(
            "CustomCallable.kt", """
      package com.example.api
import de.jensklingenberg.ktorfit.http.FieldMap
import de.jensklingenberg.ktorfit.http.FormUrlEncoded
import de.jensklingenberg.ktorfit.http.POST

interface TestService {
    @FormUrlEncoded
    @POST("posts")
    suspend fun test(@FieldMap() testFieldMap: Map<String, String>)
    
}
    """
        )


        val expected = "fields = listOf(FieldData(false,testFieldMap,\"\",FieldType.FIELDMAP))"

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
    fun testFunctionWithFieldAndFieldMap() {

        val source = SourceFile.kotlin(
            "CustomCallable.kt", """
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
    """
        )


        val expected =  "fields = listOf(FieldData(false,testField,\"name\",FieldType.FIELD),\n" +
                "            FieldData(false,name,\"\",FieldType.FIELDMAP))"

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
    fun whenFieldMapTypeIsNotMap_ThrowCompilationError() {

        val source = SourceFile.kotlin(
            "CustomCallable.kt", """
      package com.example.api
import de.jensklingenberg.ktorfit.http.POST
import de.jensklingenberg.ktorfit.http.FieldMap
import de.jensklingenberg.ktorfit.http.FormUrlEncoded

interface TestService {

    @FormUrlEncoded
    @POST("posts")
   fun example(@FieldMap() name: String)
    
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
        Assert.assertTrue(result.messages.contains(KtorfitError.FIELD_MAP_PARAMETER_TYPE_MUST_BE_MAP))
    }

    @Test
    fun whenFieldMapKeysIsNotString_ThrowCompilationError() {

        val source = SourceFile.kotlin(
            "CustomCallable.kt", """
      package com.example.api
import de.jensklingenberg.ktorfit.http.POST
import de.jensklingenberg.ktorfit.http.FieldMap
import de.jensklingenberg.ktorfit.http.FormUrlEncoded

interface TestService {

    @FormUrlEncoded
    @POST("posts")
   fun example(@FieldMap name: Map<Int,String>)
    
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
        Assert.assertTrue(result.messages.contains(KtorfitError.FIELD_MAP_KEYS_MUST_BE_OF_TYPE_STRING))
    }



}

