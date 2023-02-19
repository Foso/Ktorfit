import com.google.common.truth.Truth
import com.tschuchort.compiletesting.*
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

        val expectedFunctionSource = """public override suspend fun test(postId: String): String {
    val postId: Int = ktorfitClient.convertParameterType(postId,postId::class,Int::class)
    val requestData = RequestData(method="GET",
        relativeUrl="posts/{postId}/comments",
        returnTypeData = TypeData("kotlin.String"),
        paths = listOf(DH("postId",postId,false)),
        requestTypeInfo=typeInfo<String>(),
        returnTypeInfo = typeInfo<String>()) 

    return ktorfitClient.suspendRequest<String, String>(requestData)!!
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
        Assert.assertEquals(true,generatedFile.exists())
        Assert.assertEquals(true,generatedFile.readText().contains(expectedFunctionSource))
    }
}