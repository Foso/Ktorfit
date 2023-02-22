package de.jensklingenberg.ktorfit.requestData


import de.jensklingenberg.ktorfit.model.ReturnTypeData

/**
 * Source for the "returnTypeInfo" argument of [de.jensklingenberg.ktorfit.RequestData]
 */

fun getReturnTypeInfoText(returnType: ReturnTypeData): String {
    return "returnTypeInfo = typeInfo<${returnType.name}>()"
}

