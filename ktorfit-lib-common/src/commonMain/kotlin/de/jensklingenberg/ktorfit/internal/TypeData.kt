package de.jensklingenberg.ktorfit.internal

import io.ktor.util.reflect.*

public data class TypeData(
    public val qualifiedName: String,
    public val typeArgs: List<TypeData> = emptyList(),
    public val isNullable: Boolean = qualifiedName.endsWith("?"),
    val typeInfo: TypeInfo,
)