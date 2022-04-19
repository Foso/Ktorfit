package de.jensklingenberg.ktorfit

import KtorfitProcessorProvider
import com.google.common.truth.Truth.assertThat
import com.tschuchort.compiletesting.KotlinCompilation
import com.tschuchort.compiletesting.KotlinCompilation.ExitCode
import com.tschuchort.compiletesting.SourceFile
import com.tschuchort.compiletesting.kspIncremental
import com.tschuchort.compiletesting.kspSourcesDir
import com.tschuchort.compiletesting.symbolProcessorProviders
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.Parameterized
import java.io.File

class TestCompilationErrrors() {


  @Test
  fun whenMoreThan1HttpMethodFound_throwError() {
    val source = SourceFile.kotlin("Source.kt", """
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

interface GithubService {

    @GET("user/followers")
    @POST("repos/foso/experimental/issues")
    suspend fun test(): String
    
}
    """)

    val compilation = KotlinCompilation().apply {
      sources = listOf(source)
      inheritClassPath = true
      symbolProcessorProviders = listOf(KtorfitProcessorProvider())
      kspIncremental = true
    }
    val result = compilation.compile()
    assertThat(result.exitCode).isEqualTo(ExitCode.COMPILATION_ERROR)
      Assert.assertTrue(result.messages.contains("Ktorfit: Only one HTTP method is allowed. Found: GET, POST at test"))
  }

}