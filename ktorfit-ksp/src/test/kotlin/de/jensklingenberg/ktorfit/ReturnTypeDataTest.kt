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

@TypeConverters(MyConv::class)
interface TestService {
    @POST("user")
    fun test(@Body id: String): Flow<User>
}

class User

class MyConv {


    @TypeConverter
    suspend fun toUser2(httpResponse: io.ktor.client.statement.HttpResponse): String {
        return httpResponse.body<User>()
    }

    @TypeConverter
    fun toUser(getResponse: suspend () -> HttpResponse): Flow<Any> {
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

        val compilation = getCompilation(listOf(source))
        val result = compilation.compile()

        val generatedSourcesDir = compilation.kspSourcesDir
        val generatedFile =
            File(
                generatedSourcesDir,
                "/kotlin/com/example/api/_TestServiceImpl.kt",
            )
        assertTrue(generatedFile.exists())

        val actualSource = generatedFile.readText()
        assertTrue(actualSource.contains(expectedBodyDataArgumentText))
    }
}
