package de.jensklingenberg.ktorfit.internal

import de.jensklingenberg.ktorfit.converter.TypeData2
import io.ktor.util.reflect.typeInfo
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class TypeDataTest {
    @Test
    fun testTypeDataCreator() {
        val typeData2 = TypeData2.createTypeData("kotlin.Map<kotlin.String?, kotlin.Int?>", typeInfo<Map<String, Int?>>())

        assertEquals("kotlin.Map", typeData2.qualifiedName)
        assertTrue(typeData2.typeInfo.type == Map::class)
        assertTrue(typeData2.typeArgs[0].typeInfo.type == String::class)
    }

    @Test
    fun testTypeDataCreatorWithEmptyQualifiedName() {
        val typeData2 = TypeData2.createTypeData("", typeInfo<Map<String, Int?>>())

        assertEquals("", typeData2.qualifiedName)
        assertTrue(typeData2.typeInfo.type == Map::class)
        assertTrue(typeData2.typeArgs[0].typeInfo.type == String::class)
    }
}
