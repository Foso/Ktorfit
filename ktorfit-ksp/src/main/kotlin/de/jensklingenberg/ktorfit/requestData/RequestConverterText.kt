package de.jensklingenberg.ktorfit.requestData

import com.squareup.kotlinpoet.FunSpec
import de.jensklingenberg.ktorfit.model.FunctionData

fun FunSpec.Builder.addRequestConverterText(functionData: FunctionData) = apply {
    val parameterNames = functionData.parameterDataList.map { parameter ->
        val parameterNameUpperCase = parameter.name.replaceFirstChar { parameter.name.first().titlecaseChar() }
        if (parameter.hasRequestTypeAnnotation()) {
            this.addStatement("val requestKClass$parameterNameUpperCase = %T::class", parameter.requestTypeClassName!!)
            this.addStatement("val requestConverter$parameterNameUpperCase = ktorfitClient.ktorfit.requestConverters.firstOrNull {")
            this.addStatement("\tit.supportedType(${parameter.name}::class, requestKClass$parameterNameUpperCase)")
            this.addStatement("} ?: throw IllegalArgumentException(%S)", "No RequestConverter found for parameter '${parameter.name}' in method '${functionData.name}'")
            this.addStatement("val convertedType$parameterNameUpperCase: %T = requestKClass$parameterNameUpperCase.cast(requestConverter$parameterNameUpperCase.convert(${parameter.name}))\n", parameter.requestTypeClassName)
            "convertedType$parameterNameUpperCase"
        } else {
            parameter.name
        }
    }
    this.addStatement("return ${functionData.name}(${parameterNames.joinToString()})")
}