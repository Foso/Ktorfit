package de.jensklingenberg.ktorfit.requestData


import de.jensklingenberg.ktorfit.model.FunctionData

/**
 * Source for the "requestTypeInfo" argument of [de.jensklingenberg.ktorfit.RequestData]
 */

fun getRequestTypeInfoText(function: FunctionData): String {
    val typeWithoutOuterType = function.returnType.name.substringAfter("<").substringBeforeLast(">")
    return "requestTypeInfo=typeInfo<$typeWithoutOuterType>()"
}

