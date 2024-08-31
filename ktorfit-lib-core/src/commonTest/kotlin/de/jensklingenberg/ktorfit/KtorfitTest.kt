package de.jensklingenberg.ktorfit

import de.jensklingenberg.ktorfit.converter.Converter
import de.jensklingenberg.ktorfit.converter.KtorfitResult
import de.jensklingenberg.ktorfit.converter.TypeData
import de.jensklingenberg.ktorfit.converter.builtin.DefaultSuspendResponseConverterFactory
import io.ktor.client.request.HttpRequestData
import io.ktor.client.statement.HttpResponse
import io.ktor.util.reflect.typeInfo
import kotlin.test.Test
import kotlin.test.assertTrue

class KtorfitTest {
    @Test
    fun whenSuspendResponseConverterForStringAdded_FindIt() {
        val engine =
            object : TestEngine() {
                override fun getRequestData(data: HttpRequestData) {
                }
            }

        val ktorfit =
            Ktorfit
                .Builder()
                .httpClient(engine)
                .baseUrl("http://test.de/")
                .converterFactories(TestConverterFactory())
                .build()

        val nextConverter =
            ktorfit.nextSuspendResponseConverter(
                null,
                TypeData("kotlin.String", emptyList(), isNullable = true, typeInfo = typeInfo<String>()),
            )
        assertTrue(nextConverter is TestConverterFactory.SuspendConverter)
    }

    @Test
    fun whenNoSuspendResponseConverterForStringAdded_FindDefaultConverter() {
        val engine =
            object : TestEngine() {
                override fun getRequestData(data: HttpRequestData) {
                }
            }

        val ktorfit =
            Ktorfit
                .Builder()
                .httpClient(engine)
                .baseUrl("http://test.de/")
                .build()

        val nextConverter =
            ktorfit.nextSuspendResponseConverter(
                null,
                TypeData("kotlin.String", emptyList(), isNullable = true, typeInfo = typeInfo<String>()),
            )
        assertTrue(nextConverter is DefaultSuspendResponseConverterFactory.DefaultSuspendResponseConverter)
    }

    @Test
    fun whenResponseConverterForStringAdded_FindIt() {
        val engine =
            object : TestEngine() {
                override fun getRequestData(data: HttpRequestData) {
                }
            }

        val ktorfit =
            Ktorfit
                .Builder()
                .httpClient(engine)
                .baseUrl("http://test.de/")
                .converterFactories(TestConverterFactory())
                .build()

        val nextConverter =
            ktorfit.nextResponseConverter(null, TypeData("kotlin.String", emptyList(), isNullable = true, typeInfo = typeInfo<String>()))
        assertTrue((nextConverter) is TestConverterFactory.ResponseConverter)
    }

    @Test
    fun whenNoResponseConverterForStringAdded_ReturnNull() {
        val engine =
            object : TestEngine() {
                override fun getRequestData(data: HttpRequestData) {
                }
            }

        val ktorfit =
            Ktorfit
                .Builder()
                .httpClient(engine)
                .baseUrl("http://test.de/")
                .build()

        val nextConverter =
            ktorfit.nextResponseConverter(null, TypeData("kotlin.String", emptyList(), isNullable = true, typeInfo = typeInfo<String>()))
        assertTrue(nextConverter is DefaultSuspendResponseConverterFactory.DefaultResponseConverter)
    }
}

private class TestConverterFactory : Converter.Factory {
    class SuspendConverter(
        val typeData: TypeData
    ) : Converter.SuspendResponseConverter<HttpResponse, Any> {
        override suspend fun convert(result: KtorfitResult): Any {
            when (result) {
                is KtorfitResult.Success -> {
                    return result.response.call.body(typeData.typeInfo)
                }
                is KtorfitResult.Failure -> {
                    throw result.throwable
                }
            }
        }
    }

    class ResponseConverter : Converter.ResponseConverter<HttpResponse, Any> {
        override fun convert(getKtorfitResponse: suspend () -> HttpResponse): Any = ""
    }

    override fun suspendResponseConverter(
        typeData: TypeData,
        ktorfit: Ktorfit,
    ): Converter.SuspendResponseConverter<HttpResponse, *>? {
        return if (typeData.qualifiedName == "kotlin.String") {
            return SuspendConverter(typeData)
        } else {
            null
        }
    }

    override fun responseConverter(
        typeData: TypeData,
        ktorfit: Ktorfit,
    ): Converter.ResponseConverter<HttpResponse, *>? {
        return if (typeData.qualifiedName == "kotlin.String") {
            return ResponseConverter()
        } else {
            null
        }
    }
}
