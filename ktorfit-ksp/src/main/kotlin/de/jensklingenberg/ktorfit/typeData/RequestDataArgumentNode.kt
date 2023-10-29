package de.jensklingenberg.ktorfit.typeData


import de.jensklingenberg.ktorfit.model.ReturnTypeData
import de.jensklingenberg.ktorfit.model.typeDataClass
import de.jensklingenberg.ktorfit.utils.removeWhiteSpaces

fun getTypeDataArgumentText(returnTypeData: ReturnTypeData): String {

    val returnTypeName = "qualifiedTypename = \"${returnTypeData.qualifiedName.removeWhiteSpaces()}\""
    val returnTypeInfo = getReturnTypeInfoText(returnTypeData.name)
    val args = listOf(
        returnTypeName,
        returnTypeInfo
    ).filter { it.isNotEmpty() }.joinToString(",\n") { it }

    return "val ${typeDataClass.objectName} = ${typeDataClass.name}.createTypeData($args) \n"
}
