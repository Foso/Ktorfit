
import com.tschuchort.compiletesting.KotlinCompilation
import com.tschuchort.compiletesting.SourceFile
import com.tschuchort.compiletesting.kspSourcesDir
import de.jensklingenberg.ktorfit.getCompilation
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import java.io.File

class RequestTypeTest {
    @Test
    fun generate() {
        val source =
            SourceFile.kotlin(
                "Source.kt",
                """
      package com.example.api
import de.jensklingenberg.ktorfit.http.*

interface TestService {
    @GET("posts/{postId}/comments")
    suspend fun test(@RequestType(Int::class) @Path("postId") postId: String, @Query("postId") testQuery: String): String
}
    """,
            )

        val expectedFunctionSource = """val postId: Int = _helper.convertParameterType(postId,postId::class,Int::class)"""

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
        assertTrue(actualSource.contains(expectedFunctionSource))
    }
}
