package de.jensklingenberg.ktorfit.converter

import io.ktor.util.reflect.TypeInfo
import io.ktor.util.reflect.platformType
import kotlin.reflect.KClass
import kotlin.reflect.KTypeProjection

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
                    ?.filter {
                        it.type != null
                    }?.mapIndexed { index, kTypeProjection ->
                        val cleaned = split.getOrNull(index)?.trim() ?: ""

                        val modelKType = kTypeProjection.type
                        val modelClass = modelKType?.classifier as? KClass<*>? ?: return@mapIndexed null

                        createTypeData(cleaned, TypeInfo(modelClass, modelKType.platformType, modelKType))
                    }?.filterNotNull()
                    .orEmpty()

            return TypeData(qualifiedTypename.substringBefore("<"), args, typeInfo = typeInfo)
        }
    }
}

public data class TypeData2(
    public val qualifiedName: String,
    public val typeInfo: TypeInfo,
    public val isNullable: Boolean = typeInfo.kotlinType?.isMarkedNullable ?: false,
) {
    public companion object {
        public fun createTypeData(
            qualifiedTypename: String = "",
            typeInfo: TypeInfo,
        ): TypeData2 = TypeData2(qualifiedTypename.substringBefore("<"), typeInfo = typeInfo)
    }
}

public fun KTypeProjection.asTypeInfo(): TypeInfo? {
    listOf("d").firstOrNull()
    val modelKType = this.type
    val modelClass = modelKType?.classifier as? KClass<*>?
    return if (modelClass != null) {
        TypeInfo(modelClass, modelKType.platformType, modelKType)
    } else {
        null
    }
}
