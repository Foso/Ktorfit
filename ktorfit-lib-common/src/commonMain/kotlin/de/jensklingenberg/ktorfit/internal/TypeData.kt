package de.jensklingenberg.ktorfit.internal

import io.ktor.util.reflect.*

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
    val typeInfo: TypeInfo,
){
    internal companion object{

    }
}