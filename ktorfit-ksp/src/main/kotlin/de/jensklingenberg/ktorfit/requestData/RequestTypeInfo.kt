package de.jensklingenberg.ktorfit.requestData


import de.jensklingenberg.ktorfit.model.ReturnTypeData

/**
 * Source for the "requestTypeInfo" argument of [de.jensklingenberg.ktorfit.RequestData]
 */

fun getRequestTypeInfoText(returnType: ReturnTypeData): String {
    val typeWithoutOuterType = returnType.innerTypeName
    return "requestTypeInfo=typeInfo<$typeWithoutOuterType>()"
}

