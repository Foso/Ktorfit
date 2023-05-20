package de.jensklingenberg.ktorfit

import de.jensklingenberg.ktorfit.converter.Converter
import de.jensklingenberg.ktorfit.converter.SuspendResponseConverter
import de.jensklingenberg.ktorfit.converter.builtin.DefaultSuspendResponseConverterFactory
import de.jensklingenberg.ktorfit.converter.request.ResponseConverter
import de.jensklingenberg.ktorfit.internal.RequestData
import de.jensklingenberg.ktorfit.internal.TypeData
import io.ktor.client.statement.*
import io.ktor.util.reflect.*
import org.junit.Assert
import org.junit.Test

private class TestConverterFactory : Converter.Factory{

    class SuspendConverter(val typeData: TypeData) : Converter.SuspendResponseConverter<HttpResponse, Any>{
        override suspend fun convert(response: HttpResponse): Any {
            return response.call.body(typeData.typeInfo)
        }
    }


    override fun suspendResponseConverter(
        typeData: TypeData,
        ktorfit: Ktorfit
    ): Converter.SuspendResponseConverter<HttpResponse, *>? {
        return if(typeData.qualifiedName == "kotlin.String"){
            return SuspendConverter(typeData)
        }else{
            null
        }
    }


}


class KtorfitTest {

    @Test
    fun whenSuspendResponseConverterForStringAdded_FindIt() {

        val ktorfit =
            Ktorfit.Builder().baseUrl("http://test.de/").converterFactories(TestConverterFactory())
                .build()

        val nextConverter =
            ktorfit.nextSuspendResponseConverter(null, TypeData("kotlin.String", emptyList(), true, typeInfo<String>()))
        Assert.assertTrue(nextConverter is TestConverterFactory.SuspendConverter)

    }

    @Test
    fun whenNoSuspendResponseConverterForStringAdded_FindDefaultConverter() {

        val ktorfit =
            Ktorfit.Builder().baseUrl("http://test.de/")
                .build()

        val nextConverter =
            ktorfit.nextSuspendResponseConverter(null, TypeData("kotlin.String", emptyList(), true, typeInfo<String>()))
        Assert.assertTrue(nextConverter is DefaultSuspendResponseConverterFactory.DefaultSuspendResponseConverter)

    }

    @Test
    fun whenResponseConverterForStringAdded_FindIt() {

        val ktorfit =
            Ktorfit.Builder().baseUrl("http://test.de/").converterFactories(TestConverterFactory())
                .build()

        val nextConverter =
            ktorfit.nextResponseConverter(null, TypeData("kotlin.String", emptyList(), true, typeInfo<String>()))
       // Assert.assertTrue(nextConverter is DefaultSuspendResponseConverterFactory.DefaultSuspendResponseConverter)

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

    @Test
    fun testTypeDataCreator() {

        val typeData =  RequestData({},"kotlin.Map<kotlin.String?, kotlin.Int?>",typeInfo<Map<String, Int?>>()).getTypeData()

        Assert.assertEquals("kotlin.Map", typeData.qualifiedName)
        Assert.assertTrue(typeData.typeInfo.type == Map::class)
        Assert.assertTrue(typeData.typeArgs[0].typeInfo.type == String::class)

    }


}