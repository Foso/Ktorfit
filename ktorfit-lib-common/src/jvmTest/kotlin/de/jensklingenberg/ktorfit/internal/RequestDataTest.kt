package de.jensklingenberg.ktorfit.internal

import de.jensklingenberg.ktorfit.internal.RequestData
import io.ktor.util.reflect.*
import org.junit.Assert
import org.junit.Test

class RequestDataTest {

    @Test
    fun testTypeDataCreator() {

        val typeData =  RequestData({},"kotlin.Map<kotlin.String?, kotlin.Int?>", typeInfo<Map<String, Int?>>()).getTypeData()

        Assert.assertEquals("kotlin.Map", typeData.qualifiedName)
        Assert.assertTrue(typeData.typeInfo.type == Map::class)
        Assert.assertTrue(typeData.typeArgs[0].typeInfo.type == String::class)

    }

}