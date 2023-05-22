package de.jensklingenberg.ktorfit

import de.jensklingenberg.ktorfit.converter.Converter
import de.jensklingenberg.ktorfit.converter.builtin.DefaultSuspendResponseConverterFactory
import de.jensklingenberg.ktorfit.internal.TypeData
import io.ktor.client.statement.*
import io.ktor.util.reflect.*
import org.junit.Assert
import org.junit.Ignore
import org.junit.Test

private class TestConverterFactory : Converter.Factory {

    class SuspendConverter(val typeData: TypeData) : Converter.SuspendResponseConverter<HttpResponse, Any> {
        override suspend fun convert(response: HttpResponse): Any {
            return response.call.body(typeData.typeInfo)
        }
    }

    class ResponseConverter : Converter.ResponseConverter<HttpResponse, Any> {

        override fun convert(getResponse: suspend () -> HttpResponse): Any {
            return ""
        }
    }

    override fun suspendResponseConverter(
        typeData: TypeData,
        ktorfit: Ktorfit
    ): Converter.SuspendResponseConverter<HttpResponse, *>? {
        return if (typeData.qualifiedName == "kotlin.String") {
            return SuspendConverter(typeData)
        } else {
            null
        }
    }

    override fun responseConverter(
        typeData: TypeData,
        ktorfit: Ktorfit
    ): Converter.ResponseConverter<HttpResponse, *>? {
        return if (typeData.qualifiedName == "kotlin.String") {
            return ResponseConverter()
        } else {
            null
        }
    }

}


class KtorfitTest {

    @Test
    fun whenSuspendResponseConverterForStringAdded_FindIt() {

        val ktorfit =
            Ktorfit.Builder()
                .baseUrl("http://test.de/")
                .converterFactories(TestConverterFactory())
                .build()

        val nextConverter =
            ktorfit.nextSuspendResponseConverter(null, TypeData("kotlin.String", emptyList(), true, typeInfo<String>()))
        Assert.assertTrue(nextConverter is TestConverterFactory.SuspendConverter)

    }

    @Ignore("Will be activate when old converters are removed")
    @Test
    fun whenNoSuspendResponseConverterForStringAdded_FindDefaultConverter() {

        val ktorfit =
            Ktorfit
                .Builder()
                .baseUrl("http://test.de/")
                .build()

        val nextConverter =
            ktorfit.nextSuspendResponseConverter(null, TypeData("kotlin.String", emptyList(), true, typeInfo<String>()))
        Assert.assertTrue(nextConverter is DefaultSuspendResponseConverterFactory.DefaultSuspendResponseConverter)

    }

    @Test
    fun whenResponseConverterForStringAdded_FindIt() {

        val ktorfit =
            Ktorfit.Builder()
                .baseUrl("http://test.de/")
                .converterFactories(TestConverterFactory())
                .build()

        val nextConverter =
            ktorfit.nextResponseConverter(null, TypeData("kotlin.String", emptyList(), true, typeInfo<String>()))
        Assert.assertTrue((nextConverter) is TestConverterFactory.ResponseConverter)

    }

    @Test
    fun whenNoResponseConverterForStringAdded_ReturnNull() {

        val ktorfit =
            Ktorfit.Builder()
                .baseUrl("http://test.de/")
                .build()

        val nextConverter =
            ktorfit.nextResponseConverter(null, TypeData("kotlin.String", emptyList(), true, typeInfo<String>()))
        Assert.assertEquals(null, nextConverter)

    }


}