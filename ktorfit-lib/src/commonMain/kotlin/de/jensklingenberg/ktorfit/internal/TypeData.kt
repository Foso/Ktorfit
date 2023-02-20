package de.jensklingenberg.ktorfit.internal

public class TypeData(public val qualifiedName: String, public val typeArgs: List<TypeData> = emptyList(), public val isNullable : Boolean = qualifiedName.endsWith("?"))