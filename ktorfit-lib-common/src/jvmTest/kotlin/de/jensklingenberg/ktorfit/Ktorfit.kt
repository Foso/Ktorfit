package de.jensklingenberg.ktorfit

import de.jensklingenberg.ktorfit.converter.DefaultSuspendConverter
import de.jensklingenberg.ktorfit.converter.SuspendResponseConverter
import de.jensklingenberg.ktorfit.converter.request.ResponseConverter
import de.jensklingenberg.ktorfit.internal.TypeData
import io.ktor.client.statement.*
import io.ktor.util.reflect.*
import org.junit.Assert
import org.junit.Test


private class TestSuspendResponseConverter : SuspendResponseConverter {
    override suspend fun <RequestType> wrapSuspendResponse(
        typeData: TypeData,
        requestFunction: suspend () -> Pair<TypeInfo, HttpResponse>,
        ktorfit: Ktorfit
    ): Any {
        val (info, response) = requestFunction()
        return response.call.body(info)
    }

    override fun supportedType(typeData: TypeData, isSuspend: Boolean): Boolean {
        return typeData.qualifiedName == "kotlin.String"
    }

}

private class TestResponseConverter : ResponseConverter {

    override fun <RequestType> wrapResponse(
        typeData: TypeData,
        requestFunction: suspend () -> Pair<TypeInfo, HttpResponse?>,
        ktorfit: Ktorfit
    ): Any {
        return ""
    }

    override fun supportedType(typeData: TypeData, isSuspend: Boolean): Boolean {
        return typeData.qualifiedName == "kotlin.String"
    }

}

class KtorfitTest {

    @Test
    fun whenSuspendResponseConverterForStringAdded_FindIt() {

        val ktorfit =
            Ktorfit.Builder().baseUrl("http://test.de/").responseConverter(TestSuspendResponseConverter())
                .build()

        val nextConverter =
            ktorfit.nextSuspendResponseConverter(null, TypeData("kotlin.String", emptyList(), true, typeInfo<String>()))
        Assert.assertTrue(nextConverter is TestSuspendResponseConverter)

    }

    @Test
    fun whenNoSuspendResponseConverterForStringAdded_FindDefaultConverter() {

        val ktorfit =
            Ktorfit.Builder().baseUrl("http://test.de/")
                .build()

        val nextConverter =
            ktorfit.nextSuspendResponseConverter(null, TypeData("kotlin.String", emptyList(), true, typeInfo<String>()))
        Assert.assertTrue(nextConverter is DefaultSuspendConverter)

    }

    @Test
    fun whenResponseConverterForStringAdded_FindIt() {

        val ktorfit =
            Ktorfit.Builder().baseUrl("http://test.de/").responseConverter(TestResponseConverter())
                .build()

        val nextConverter =
            ktorfit.nextResponseConverter(null, TypeData("kotlin.String", emptyList(), true, typeInfo<String>()))
        Assert.assertTrue(nextConverter is TestResponseConverter)

    }

    @Test
    fun whenNoResponseConverterForStringAdded_ReturnNull() {

        val ktorfit =
            Ktorfit.Builder().baseUrl("http://test.de/")
                .build()

        val nextConverter =
            ktorfit.nextResponseConverter(null, TypeData("kotlin.String", emptyList(), true, typeInfo<String>()))
        Assert.assertEquals(null, nextConverter)

    }


}