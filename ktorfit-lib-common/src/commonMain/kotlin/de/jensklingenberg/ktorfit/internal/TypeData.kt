package de.jensklingenberg.ktorfit.internal

import io.ktor.util.reflect.*
import kotlin.reflect.KClass

/**
 * This class contains information about the requested response type
 * e.g for Map<String, Int>
 * @param qualifiedName will contain kotlin.collections.Map
 * @param typeArgs contains the type arguments as TypeData (String, Int)
 * @param isNullable is true if the type is nullable
 * @param typeInfo the typeInfo of the return type, that can be used to
 * convert the HTTPResponse to the given type
 */
public data class TypeData(
    public val qualifiedName: String,
    public val typeArgs: List<TypeData> = emptyList(),
    public val isNullable: Boolean = qualifiedName.endsWith("?"),
    public val typeInfo: TypeInfo,
) {
    public companion object {
        public fun createTypeData(qualifiedTypename: String, typeInfo: TypeInfo): TypeData {

            val typeArgument = qualifiedTypename.substringAfter("<").substringBeforeLast(">")
            val spilt = typeArgument.split(",")
            val args = mutableListOf<TypeData>()
            typeInfo.kotlinType?.arguments?.forEachIndexed { index, kTypeProjection ->
                val cleaned = spilt[index].trim()

                val modelKType = kTypeProjection.type
                val modelClass = (modelKType?.classifier as? KClass<*>?)!!

                args.add(createTypeData(cleaned, TypeInfo(modelClass, modelKType.platformType, modelKType)))
            }

            return TypeData(qualifiedTypename.substringBefore("<"), args, typeInfo = typeInfo)
        }
    }
}