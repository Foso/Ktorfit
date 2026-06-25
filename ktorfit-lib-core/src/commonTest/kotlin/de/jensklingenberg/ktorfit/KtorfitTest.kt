package de.jensklingenberg.ktorfit

import de.jensklingenberg.ktorfit.converter.Converter
import de.jensklingenberg.ktorfit.converter.KtorfitResult
import de.jensklingenberg.ktorfit.converter.TypeData
import de.jensklingenberg.ktorfit.converter.builtin.DefaultSuspendResponseConverterFactory
import de.jensklingenberg.ktorfit.internal.ClassProvider
import de.jensklingenberg.ktorfit.internal.InternalKtorfitApi
import de.jensklingenberg.ktorfit.optins.KtorfitFactoryRegistry
import io.ktor.client.request.HttpRequestData
import io.ktor.client.statement.HttpResponse
import io.ktor.util.reflect.typeInfo
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertTrue

@OptIn(InternalKtorfitApi::class, ExperimentalFactoryRegistry::class)
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

    // --- createApi<T>() tests ---
    // Use distinct interfaces to avoid global registry pollution between tests.

    interface RegisteredSuccessApi

    interface NotRegisteredApi

    interface KtorfitAwareApi

    @Test
    fun `createUsingRegistry returns registered implementation`() {
        val ktorfit = buildKtorfit()
        val expectedInstance = object : RegisteredSuccessApi {}
        val classProvider =
            object : ClassProvider<RegisteredSuccessApi> {
                override fun create(_ktorfit: Ktorfit): RegisteredSuccessApi = expectedInstance
            }
        KtorfitFactoryRegistry.register(RegisteredSuccessApi::class, classProvider)

        val api = ktorfit.createUsingRegistry<RegisteredSuccessApi>()

        assertEquals(expectedInstance, api)
    }

    @Test
    fun `createUsingRegistry passes Ktorfit instance to ClassProvider`() {
        val ktorfit = buildKtorfit()
        var capturedKtorfit: Ktorfit? = null
        val expectedInstance = object : KtorfitAwareApi {}
        val classProvider =
            object : ClassProvider<KtorfitAwareApi> {
                override fun create(_ktorfit: Ktorfit): KtorfitAwareApi {
                    capturedKtorfit = _ktorfit
                    return expectedInstance
                }
            }
        KtorfitFactoryRegistry.register(KtorfitAwareApi::class, classProvider)

        ktorfit.createUsingRegistry<KtorfitAwareApi>()

        assertEquals(ktorfit, capturedKtorfit)
    }

    @Test
    fun `createUsingRegistry throws when no factory registered`() {
        val ktorfit = buildKtorfit()
        val exception =
            assertFailsWith<IllegalArgumentException> {
                ktorfit.createUsingRegistry<NotRegisteredApi>()
            }
        assertTrue(exception.message?.contains("No Ktorfit API registered") ?: false)
    }
}

@OptIn(InternalKtorfitApi::class)
private fun buildKtorfit(): Ktorfit {
    val engine =
        object : TestEngine() {
            override fun getRequestData(data: HttpRequestData) {
            }
        }
    return Ktorfit
        .Builder()
        .httpClient(engine)
        .baseUrl("http://test.de/")
        .build()
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
        override fun convert(getResponse: suspend () -> HttpResponse): Any = ""
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
