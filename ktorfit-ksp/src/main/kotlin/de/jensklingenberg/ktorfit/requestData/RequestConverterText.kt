package de.jensklingenberg.ktorfit.requestData

import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.ksp.toClassName
import de.jensklingenberg.ktorfit.model.FunctionData
import de.jensklingenberg.ktorfit.model.annotations.RequestType

fun FunSpec.Builder.addRequestConverterText(functionData: FunctionData) = apply {
    val parameterNames = functionData.parameterDataList.map { parameter ->
        val parameterNameUpperCase = parameter.name.replaceFirstChar { parameter.name.first().titlecaseChar() }
        val requestTypeClassName =
            parameter.annotations.filterIsInstance<RequestType>().first().requestType.toClassName()
        if (parameter.hasAnnotation<RequestType>()) {
            this.addStatement("val requestKClass$parameterNameUpperCase = %T::class", requestTypeClassName)
            this.addStatement("val requestConverter$parameterNameUpperCase = ktorfitClient.ktorfit.requestConverters.firstOrNull {")
            this.addStatement("\tit.supportedType(${parameter.name}::class, requestKClass$parameterNameUpperCase)")
            this.addStatement(
                "} ?: throw IllegalArgumentException(%S)",
                "No RequestConverter found for parameter '${parameter.name}' in method '${functionData.name}'"
            )
            this.addStatement(
                "val convertedType$parameterNameUpperCase: %T = requestKClass$parameterNameUpperCase.cast(requestConverter$parameterNameUpperCase.convert(${parameter.name}))\n",
                requestTypeClassName
            )
            "convertedType$parameterNameUpperCase"
        } else {
            parameter.name
        }
    }
    this.addStatement("return ${functionData.name}(${parameterNames.joinToString()})")
}