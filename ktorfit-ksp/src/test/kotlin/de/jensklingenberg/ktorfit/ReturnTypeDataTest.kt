package de.jensklingenberg.ktorfit

import com.tschuchort.compiletesting.SourceFile
import com.tschuchort.compiletesting.kspSourcesDir
import org.junit.Assert.assertTrue
import org.junit.Test
import java.io.File

class ReturnTypeDataTest {
    @Test
    fun testFunctionWithBody() {
        val source =
            SourceFile.kotlin(
                "Source.kt",
                """
      package com.example.api
import de.jensklingenberg.ktorfit.http.POST
import de.jensklingenberg.ktorfit.http.Body
import de.jensklingenberg.ktorfit.http.FormUrlEncoded
import de.jensklingenberg.ktorfit.core.TypeConverter
import de.jensklingenberg.ktorfit.core.TypeConverters
import io.ktor.client.call.body
import io.ktor.client.statement.HttpResponse
import kotlinx.coroutines.flow.Flow
import io.ktor.client.statement.HttpResponse

@TypeConverters(MyConv::class)
interface TestService {
    @POST("user")
    fun test(@Body id: String): Flow<User>
}

class User

class MyConv {
    
    suspend fun toUser2(httpResponse: io.ktor.client.statement.HttpResponse): String {
        return httpResponse.body<User>()
    }

    fun  toUser(getResponse: suspend () -> HttpResponse, typeInfo: io.ktor.util.reflect.TypeInfo): Flow<User> {
        return flow {
            val response = getResponse()
            val user = toUser2(response)
            emit(user)
        }
    }

}
    """,
            )

        val expectedBodyDataArgumentText =
            """val _ext: HttpRequestBuilder.() -> Unit = {
        this.method = HttpMethod.parse("POST")
        url{
        takeFrom(_ktorfit.baseUrl + "user")
        }
        setBody(id) 
        }"""



        val source2 =
            SourceFile.kotlin(
                "Ktorfit.kt",
                """
                    package io.ktor.client.statement

                    class HttpResponse""")


        val source3 =           SourceFile.kotlin(
                "User.kt",
                """
                    package io.ktor.util.reflect

                    class TypeInfo""")


        val compilation = getCompilation(listOf(source2,source,source3))
        val result = compilation.compile()

        val generatedSourcesDir = compilation.kspSourcesDir
        val generatedFile =
            File(
                generatedSourcesDir,
                "/kotlin/com/example/api/_TestServiceImpl.kt",
            )
        assertTrue(generatedFile.exists())

        val actualSource = generatedFile.readText()
        print(actualSource)
        assertTrue(actualSource.contains(expectedBodyDataArgumentText))
    }
}
