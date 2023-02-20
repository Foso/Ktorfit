package de.jensklingenberg.ktorfit.model


data class ReturnTypeData(val name: String, val qualifiedName: String){
    val isNullable : Boolean = qualifiedName.endsWith("?")
    val innerTypeName = name.substringAfter("<").substringBeforeLast(">")
}

