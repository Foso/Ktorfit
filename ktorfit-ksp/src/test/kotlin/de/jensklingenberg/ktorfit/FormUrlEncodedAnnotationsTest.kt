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

class FormUrlEncodedAnnotationsTest {


    @Test
    fun whenFormUrlEncodedUsedWithNonBodyMethod_ThrowCompilationError() {

        val source = SourceFile.kotlin(
            "Source.kt", """
      package com.example.api
import de.jensklingenberg.ktorfit.http.GET
import de.jensklingenberg.ktorfit.http.Body
import de.jensklingenberg.ktorfit.http.FormUrlEncoded
interface TestService {

        @FormUrlEncoded
    @GET("user")
    suspend fun test(@Body id: String): String
    
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
        Assert.assertTrue(result.messages.contains(KtorfitError.FORM_URL_ENCODED_CAN_ONLY_BE_SPECIFIED_ON_HTTP_METHODS_WITH_REQUEST_BODY))
    }



    @Test
    fun whenFormUrlEncodedUsedAddHeader() {

        val source = SourceFile.kotlin(
            "Source.kt", """
      package com.example.api
import de.jensklingenberg.ktorfit.http.POST
import de.jensklingenberg.ktorfit.http.Field
import de.jensklingenberg.ktorfit.http.FormUrlEncoded

interface TestService {
 @FormUrlEncoded
   @POST("user")
    suspend fun test(@Field("id") id: String): String
}
    """
        )

        val expectedBodyDataArgumentText = """public override suspend fun test(id: String): String {
    val requestData = RequestData(method="POST",
        relativeUrl="user",
        headers = listOf(HeaderData("Content-Type","application/x-www-form-urlencoded")),
        fields = listOf(FieldData("id",id,false,FieldType.FIELD)),
        qualifiedRawTypeName="kotlin.String") 

    return client.suspendRequest<String, String>(requestData)
  }"""

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
        Truth.assertThat(generatedFile.readText().contains(expectedBodyDataArgumentText)).isTrue()
    }

    @Test
    fun whenFormUrlEncodedUsedWithMultipart_ThrowCompilationError() {

        val source = SourceFile.kotlin(
            "Source.kt", """
      package com.example.api
import de.jensklingenberg.ktorfit.http.POST
import de.jensklingenberg.ktorfit.http.Field
import de.jensklingenberg.ktorfit.http.FormUrlEncoded
import de.jensklingenberg.ktorfit.http.Multipart

interface TestService {
@Multipart
 @FormUrlEncoded
   @POST("user")
    suspend fun test(@Field("id") id: String): String
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
        Assert.assertTrue(result.messages.contains(KtorfitError.ONLY_ONE_ENCODING_ANNOTATION_IS_ALLOWED))
    }

}

