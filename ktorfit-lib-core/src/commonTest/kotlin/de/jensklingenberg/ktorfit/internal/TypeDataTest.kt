package de.jensklingenberg.ktorfit.internal

import de.jensklingenberg.ktorfit.converter.TypeData
import io.ktor.util.reflect.*
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class TypeDataTest {

    @Test
    fun testTypeDataCreator() {

        val typeData = TypeData.createTypeData("kotlin.Map<kotlin.String?, kotlin.Int?>", typeInfo<Map<String, Int?>>())

        assertEquals("kotlin.Map", typeData.qualifiedName)
        assertTrue(typeData.typeInfo.type == Map::class)
        assertTrue(typeData.typeArgs[0].typeInfo.type == String::class)

    }

    @Test
    fun testTypeDataCreatorWithEmptyQualifiedName() {

        val typeData = TypeData.createTypeData("", typeInfo<Map<String, Int?>>())

        assertEquals("", typeData.qualifiedName)
        assertTrue(typeData.typeInfo.type == Map::class)
        assertTrue(typeData.typeArgs[0].typeInfo.type == String::class)

    }

}