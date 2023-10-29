package de.jensklingenberg.ktorfit.model

import com.google.devtools.ksp.symbol.KSType


data class ReturnTypeData(
    val name: String,
    val qualifiedName: String,
    val parameterType: KSType?
) {
    val isNullable: Boolean = name.endsWith("?")
    val innerTypeName = name.substringAfter("<").substringBeforeLast(">")
}

