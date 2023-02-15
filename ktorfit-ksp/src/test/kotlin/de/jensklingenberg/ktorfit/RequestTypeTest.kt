import com.google.common.truth.Truth
import com.tschuchort.compiletesting.*
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


        val expectedFunctionSource = """private suspend fun test(postId: Int): String {
    val requestData = RequestData(method="GET",
        relativeUrl="posts/{postId}/comments",
        returnTypeData = TypeData("kotlin.String"),
        paths = listOf(DH("postId","äpostId",false))) 

    return ktorfitClient.suspendRequest<String, String>(requestData)!!
  }

  public override suspend fun test(postId: String): String {
    val requestKClassPostId = Int::class
    val requestConverterPostId = ktorfitClient.ktorfit.requestConverters.firstOrNull {
    	it.supportedType(postId::class, requestKClassPostId)
    } ?: throw
        IllegalArgumentException("No RequestConverter found for parameter 'postId' in method 'test'")
    val convertedTypePostId: Int = requestKClassPostId.cast(requestConverterPostId.convert(postId))

    return test(convertedTypePostId)
?""".replace("ä","$")

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
        Truth.assertThat(generatedFile.readText().contains(expectedFunctionSource)).isFalse()
    }
}