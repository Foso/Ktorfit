package de.jensklingenberg.ktorfit

import de.jensklingenberg.ktorfit.converter.Converter
import de.jensklingenberg.ktorfit.converter.KtorfitResult
import de.jensklingenberg.ktorfit.converter.TypeData2
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
                TypeData2("kotlin.String", emptyList(), isNullable = true, typeInfo = typeInfo<String>()),
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
                TypeData2("kotlin.String", emptyList(), isNullable = true, typeInfo = typeInfo<String>()),
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
            ktorfit.nextResponseConverter(null, TypeData2("kotlin.String", emptyList(), isNullable = true, typeInfo = typeInfo<String>()))
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
            ktorfit.nextResponseConverter(null, TypeData2("kotlin.String", emptyList(), isNullable = true, typeInfo = typeInfo<String>()))
        assertTrue(nextConverter is DefaultSuspendResponseConverterFactory.DefaultResponseConverter)
    }
}

private class TestConverterFactory : Converter.Factory {
    class SuspendConverter(
        val typeData2: TypeData2
    ) : Converter.SuspendResponseConverter<HttpResponse, Any> {
        override suspend fun convert(result: KtorfitResult): Any {
            when (result) {
                is KtorfitResult.Success -> {
                    return result.response.call.body(typeData2.typeInfo)
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
        typeData2: TypeData2,
        ktorfit: Ktorfit,
    ): Converter.SuspendResponseConverter<HttpResponse, *>? {
        return if (typeData2.qualifiedName == "kotlin.String") {
            return SuspendConverter(typeData2)
        } else {
            null
        }
    }

    override fun responseConverter(
        typeData2: TypeData2,
        ktorfit: Ktorfit,
    ): Converter.ResponseConverter<HttpResponse, *>? {
        return if (typeData2.qualifiedName == "kotlin.String") {
            return ResponseConverter()
        } else {
            null
        }
    }
}
