package de.jensklingenberg.ktorfit.requestData


import de.jensklingenberg.ktorfit.model.FunctionData
import de.jensklingenberg.ktorfit.model.typeDataClass
import de.jensklingenberg.ktorfit.utils.removeWhiteSpaces

fun getTypeDataArgumentText(functionData: FunctionData): String {

    val returnTypeName = "qualifiedTypename = \"${functionData.returnType.qualifiedName.removeWhiteSpaces()}\""
    val returnTypeInfo = getReturnTypeInfoText(functionData.returnType.name)
    val args = listOf(
        returnTypeName,
        returnTypeInfo
    ).filter { it.isNotEmpty() }.joinToString(",\n") { it }

    return "val ${typeDataClass.objectName} = ${typeDataClass.name}.createTypeData($args) \n"
}
