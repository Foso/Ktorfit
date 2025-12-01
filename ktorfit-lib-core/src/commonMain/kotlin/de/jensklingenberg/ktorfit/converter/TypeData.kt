package de.jensklingenberg.ktorfit.converter

import io.ktor.util.reflect.TypeInfo
import io.ktor.util.reflect.platformType
import kotlin.reflect.KClass

/**
 * This class contains information about the requested response type
 * e.g. for Map<String, Int>
 * @param qualifiedName will contain kotlin.collections.Map
 * @param typeArgs contains the type arguments as TypeData (String, Int)
 * @param isNullable is true if the type is nullable
 * @param typeInfo the typeInfo of the return type, that can be used to
 * convert the HTTPResponse to the given type
 */
public data class TypeData(
    public val qualifiedName: String,
    public val typeArgs: List<TypeData> = emptyList(),
    public val typeInfo: TypeInfo,
    public val isNullable: Boolean = typeInfo.kotlinType?.isMarkedNullable ?: false,
) {
    public companion object {
        public fun createTypeData(
            qualifiedTypename: String = "",
            typeInfo: TypeInfo,
        ): TypeData {
            val typeArgument = qualifiedTypename.substringAfter("<").substringBeforeLast(">")
            val split = typeArgument.split(",")
            val args =
                typeInfo.kotlinType
                    ?.arguments
                    ?.mapIndexed { index, kTypeProjection ->
                        val cleaned = split.getOrNull(index)?.trim() ?: ""

                        val modelKType = kTypeProjection.type
                        val modelClass = (modelKType?.classifier as? KClass<*>?)

                        if (modelClass == null) {
                            return@mapIndexed null
                        } else {
                            // Before replacing the deprecated code check issue #887
                            createTypeData(cleaned, TypeInfo(modelClass, modelKType.platformType, modelKType))
                        }
                    }?.filterNotNull()
                    .orEmpty()

            return TypeData(qualifiedTypename.substringBefore("<"), args, typeInfo = typeInfo)
        }
    }
}
