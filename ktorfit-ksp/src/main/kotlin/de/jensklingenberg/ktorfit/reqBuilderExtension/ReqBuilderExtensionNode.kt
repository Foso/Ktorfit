package de.jensklingenberg.ktorfit.reqBuilderExtension


import KtorfitProcessor
import com.google.devtools.ksp.KspExperimental
import com.google.devtools.ksp.getKotlinClassByName
import de.jensklingenberg.ktorfit.model.FunctionData
import de.jensklingenberg.ktorfit.model.annotations.CustomHttp

/**
 * This will generate the code for the HttpRequestBuilder
 */
@OptIn(KspExperimental::class)
fun getReqBuilderExtensionText(functionData: FunctionData): String {
    val methodAnnotation = functionData.httpMethodAnnotation
    //METHOD
    val httpMethodValue = if (methodAnnotation is CustomHttp) {
        methodAnnotation.customValue
    } else {
        methodAnnotation.httpMethod.keyword
    }
    val method = "this.method = HttpMethod.parse(\"${httpMethodValue}\")"
    val listType =
        KtorfitProcessor.ktorfitResolver.getKotlinClassByName("kotlin.collections.List")?.asStarProjectedType()

    val arrayType = KtorfitProcessor.ktorfitResolver.builtIns.arrayType.starProjection()
    val headers = getHeadersCode(
        functionData.annotations,
        functionData.parameterDataList,
        listType,
        arrayType
    )

    val param = getQueryCode(
        functionData.parameterDataList,
        listType,
        arrayType
    )
    val body = getBodyDataText(functionData.parameterDataList)
    val customReqBuilder = getCustomRequestBuilderText(functionData.parameterDataList)
    val args = listOf(
        method,
        body,
        headers,
        param,
        customReqBuilder
    ).filter { it.isNotEmpty() }
        .joinToString("\n") { it }

    return "val _ext: HttpRequestBuilder.() -> Unit = {\n$args \n}"
}


