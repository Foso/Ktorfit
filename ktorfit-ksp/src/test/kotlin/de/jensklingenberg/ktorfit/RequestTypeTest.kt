import com.google.common.truth.Truth
import com.tschuchort.compiletesting.KotlinCompilation
import com.tschuchort.compiletesting.SourceFile
import com.tschuchort.compiletesting.kspSourcesDir
import de.jensklingenberg.ktorfit.getCompilation
import org.junit.Assert
import org.junit.Test
import java.io.File

class RequestTypeTest {

    @Test
    fun generate() {

        val source = SourceFile.kotlin(
            "Source.kt", """
      package com.example.api
import de.jensklingenberg.ktorfit.http.*

interface TestService {

    @GET("posts/{postId}/comments")
    suspend fun test(@RequestType(Int::class) @Path("postId") postId: String): String
    
}
    """
        )

        val expectedFunctionSource = """requestTypeInfo = typeInfo<String>()"""

        val compilation = getCompilation(listOf(source))
        val result = compilation.compile()
        Truth.assertThat(result.exitCode).isEqualTo(KotlinCompilation.ExitCode.OK)

        val generatedSourcesDir = compilation.kspSourcesDir
        val generatedFile = File(
            generatedSourcesDir,
            "/kotlin/com/example/api/_TestServiceImpl.kt"
        )
        val actualSource = generatedFile.readText()
        Assert.assertEquals(true, actualSource.contains(expectedFunctionSource))
    }
}