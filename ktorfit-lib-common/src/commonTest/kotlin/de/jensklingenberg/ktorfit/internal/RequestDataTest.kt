package de.jensklingenberg.ktorfit.internal

import io.ktor.util.reflect.*
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class RequestDataTest {

    @Test
    fun testTypeDataCreator() {

        val typeData =  RequestData({},"kotlin.Map<kotlin.String?, kotlin.Int?>", typeInfo<Map<String, Int?>>()).getTypeData()

        assertEquals("kotlin.Map", typeData.qualifiedName)
        assertTrue(typeData.typeInfo.type == Map::class)
        assertTrue(typeData.typeArgs[0].typeInfo.type == String::class)

    }

}