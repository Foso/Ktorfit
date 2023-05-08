package de.jensklingenberg.ktorfit

import com.google.common.truth.Truth
import com.tschuchort.compiletesting.KotlinCompilation
import com.tschuchort.compiletesting.SourceFile
import com.tschuchort.compiletesting.kspSourcesDir
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


        val compilation = getCompilation(listOf(source))

        val result = compilation.compile()
        Truth.assertThat(result.exitCode).isEqualTo(KotlinCompilation.ExitCode.COMPILATION_ERROR)
        Assert.assertTrue(result.messages.contains(KtorfitError.FORM_URL_ENCODED_CAN_ONLY_BE_SPECIFIED_ON_HTTP_METHODS_WITH_REQUEST_BODY))
    }

    @Test
    fun whenFormUrlEncodedUsedWithNoFieldAnnotation_ThrowCompilationError() {

        val source = SourceFile.kotlin(
            "Source.kt", """
      package com.example.api
import de.jensklingenberg.ktorfit.http.POST
import de.jensklingenberg.ktorfit.http.Body
import de.jensklingenberg.ktorfit.http.FormUrlEncoded
interface TestService {

        @FormUrlEncoded
    @POST("user")
    suspend fun test(@Body id: String): String
    
}
    """
        )


        val compilation = getCompilation(listOf(source))

        val result = compilation.compile()
        Truth.assertThat(result.exitCode).isEqualTo(KotlinCompilation.ExitCode.COMPILATION_ERROR)
        Assert.assertTrue(result.messages.contains(KtorfitError.FORM_ENCODED_METHOD_MUST_CONTAIN_AT_LEAST_ONE_FIELD_OR_FIELD_MAP))
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
    suspend fun test(@Field("id") id: String): String?
}
    """
        )

        val expectedHeaderCode = """headers{
        append("Content-Type", "application/x-www-form-urlencoded")
        } """

        val compilation = getCompilation(listOf(source))
        val result = compilation.compile()
        Truth.assertThat(result.exitCode).isEqualTo(KotlinCompilation.ExitCode.OK)

        val generatedSourcesDir = compilation.kspSourcesDir
        val generatedFile = File(
            generatedSourcesDir,
            "/kotlin/com/example/api/_TestServiceImpl.kt"
        )
        Assert.assertEquals(true, generatedFile.exists())
        Assert.assertEquals(true, generatedFile.readText().contains(expectedHeaderCode))
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


        val compilation = getCompilation(listOf(source))

        val result = compilation.compile()
        Truth.assertThat(result.exitCode).isEqualTo(KotlinCompilation.ExitCode.COMPILATION_ERROR)
        Assert.assertTrue(result.messages.contains(KtorfitError.ONLY_ONE_ENCODING_ANNOTATION_IS_ALLOWED))
    }

}

