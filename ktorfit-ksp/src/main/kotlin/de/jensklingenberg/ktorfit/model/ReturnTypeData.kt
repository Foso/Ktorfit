package de.jensklingenberg.ktorfit.model

import com.google.devtools.ksp.symbol.KSTypeReference


data class ReturnTypeData(val name: String, val qualifiedName: String, val parameterType: KSTypeReference?){
    val isNullable : Boolean = qualifiedName.endsWith("?")
    val innerTypeName = name.substringAfter("<").substringBeforeLast(">")
}

