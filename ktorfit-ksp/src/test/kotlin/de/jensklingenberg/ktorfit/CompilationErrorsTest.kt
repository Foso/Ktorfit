package de.jensklingenberg.ktorfit

import com.tschuchort.compiletesting.KotlinCompilation
import com.tschuchort.compiletesting.SourceFile
import de.jensklingenberg.ktorfit.model.KtorfitError
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class CompilationErrorsTest {

    @Test
    fun whenJavaInterface_ThrowCompilationError() {

        val source = SourceFile.java(
            "Source.java", """
                package test;
      import de.jensklingenberg.ktorfit.http.GET;

      public interface TestService {
        @GET("posts")
        public String test();
      }
            """.trimIndent()
        )

        val compilation = getCompilation(listOf(source))
        val result = compilation.compile()
        assertEquals(KotlinCompilation.ExitCode.COMPILATION_ERROR, result.exitCode)
        assertTrue(result.messages.contains(KtorfitError.JAVA_INTERFACES_ARE_NOT_SUPPORTED))
    }

    @Test
    fun whenInterfaceWithoutPackage_ThrowCompilationError() {

        val source = SourceFile.kotlin(
            "Source.kt", """

import com.example.model.github.GithubFollowerResponseItem
import com.example.model.github.Issuedata
import com.example.model.github.TestReeeItem
import de.jensklingenberg.ktorfit.http.Body
import de.jensklingenberg.ktorfit.http.GET
import de.jensklingenberg.ktorfit.http.Header
import de.jensklingenberg.ktorfit.http.Headers
import de.jensklingenberg.ktorfit.http.POST
import de.jensklingenberg.ktorfit.http.Path
import kotlinx.coroutines.flow.Flow

interface GithubService {

    @GET("user/followers")
    @POST("repos/foso/experimental/issues")
    suspend fun test(): String
    
}
    """
        )

        val compilation = getCompilation(listOf(source))

        val result = compilation.compile()
        assertEquals(KotlinCompilation.ExitCode.COMPILATION_ERROR, result.exitCode)
        assertTrue(result.messages.contains(KtorfitError.INTERFACE_NEEDS_TO_HAVE_A_PACKAGE))
    }


    @Test
    fun whenInterfaceWithGenerics_ThrowCompilationError() {

        val source = SourceFile.kotlin(
            "Source.kt", """
  package com.example.api
import com.example.model.github.GithubFollowerResponseItem
import com.example.model.github.Issuedata
import com.example.model.github.TestReeeItem
import de.jensklingenberg.ktorfit.http.Body
import de.jensklingenberg.ktorfit.http.GET
import de.jensklingenberg.ktorfit.http.Header
import de.jensklingenberg.ktorfit.http.Headers
import de.jensklingenberg.ktorfit.http.POST
import de.jensklingenberg.ktorfit.http.Path
import kotlinx.coroutines.flow.Flow

interface GithubService<T> {

    @GET("user/followers")
    @POST("repos/foso/experimental/issues")
    suspend fun test(): String
    
}
    """
        )

        val compilation = getCompilation(listOf(source))

        val result = compilation.compile()
        assertEquals(KotlinCompilation.ExitCode.COMPILATION_ERROR, result.exitCode)
        assertTrue(result.messages.contains(KtorfitError.TYPE_PARAMETERS_ARE_UNSUPPORTED_ON))
    }

}