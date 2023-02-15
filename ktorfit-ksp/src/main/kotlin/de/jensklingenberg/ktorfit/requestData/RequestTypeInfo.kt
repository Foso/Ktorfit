package de.jensklingenberg.ktorfit.requestData


import de.jensklingenberg.ktorfit.model.FunctionData

/**
 * Source for the "relativeUrl" argument of [de.jensklingenberg.ktorfit.RequestData]
 */

fun getRequ(function: FunctionData): String {
    val typeWithoutOuterType = function.returnType.name.substringAfter("<").substringBeforeLast(">")

    return "requestTypeInfo=typeInfo<$typeWithoutOuterType>()"
}

