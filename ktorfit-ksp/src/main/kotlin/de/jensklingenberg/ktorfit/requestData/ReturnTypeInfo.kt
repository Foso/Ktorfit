package de.jensklingenberg.ktorfit.requestData


import de.jensklingenberg.ktorfit.model.FunctionData

/**
 * Source for the "relativeUrl" argument of [de.jensklingenberg.ktorfit.RequestData]
 */

fun getReturnTypeInfoText(function: FunctionData): String {
    val typeWithoutOuterType = function.returnType.name
    return "returnTypeInfo = typeInfo<$typeWithoutOuterType>()"
}

