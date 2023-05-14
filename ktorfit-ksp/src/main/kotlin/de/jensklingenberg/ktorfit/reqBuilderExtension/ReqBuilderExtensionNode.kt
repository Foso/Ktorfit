package de.jensklingenberg.ktorfit.reqBuilderExtension


import com.google.devtools.ksp.KspExperimental
import com.google.devtools.ksp.getKotlinClassByName
import com.google.devtools.ksp.processing.Resolver
import de.jensklingenberg.ktorfit.model.FunctionData

/**
 * This will generate the code for the HttpRequestBuilder
 */
@OptIn(KspExperimental::class)
fun getReqBuilderExtensionText(functionData: FunctionData, resolver: Resolver): String {

    val method = getMethodCode(functionData.httpMethodAnnotation)
    val listType =
        resolver.getKotlinClassByName("kotlin.collections.List")?.asStarProjectedType()!!

    val arrayType = resolver.builtIns.arrayType.starProjection()

    val headers = getHeadersCode(
        functionData.annotations,
        functionData.parameterDataList,
        listType,
        arrayType
    )

    val queryCode = getQueryCode(
        functionData.parameterDataList,
        listType,
        arrayType
    )
    val body = getBodyDataText(functionData.parameterDataList)
    val fields = getFieldArgumentsText(functionData.parameterDataList, listType,
        arrayType)
    val parts = getPartsCode(functionData.parameterDataList,listType,
        arrayType)
    val url = getUrlCode(functionData.parameterDataList,functionData.httpMethodAnnotation, queryCode)
    val customReqBuilder = getCustomRequestBuilderText(functionData.parameterDataList)
    val args = listOf(
        method,
        url,
        body,
        headers,
        fields,
        parts,
        customReqBuilder
    ).filter { it.isNotEmpty() }
        .joinToString("\n") { it }

    return "val _ext: HttpRequestBuilder.() -> Unit = {\n$args \n}"
}

