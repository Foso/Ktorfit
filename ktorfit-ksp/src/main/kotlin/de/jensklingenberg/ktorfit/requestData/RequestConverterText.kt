package de.jensklingenberg.ktorfit.requestData

import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.ksp.toClassName
import de.jensklingenberg.ktorfit.model.FunctionData
import de.jensklingenberg.ktorfit.model.annotations.RequestType

fun FunSpec.Builder.addRequestConverterText(functionData: FunctionData) = apply {
    if (functionData.parameterDataList.any { it.hasAnnotation<RequestType>() }) {
        functionData.parameterDataList.map { parameter ->
            val requestTypeClassName =
                parameter.annotations.filterIsInstance<RequestType>().first().requestType.toClassName()
            if (parameter.hasAnnotation<RequestType>()) {
                this.addStatement(
                    "val %L: %T = ktorfitClient.convertRequestType(%L,%L::class,%T::class)",
                    parameter.name,
                    requestTypeClassName,
                    parameter.name,
                    parameter.name,
                    requestTypeClassName
                )
            }
            parameter.name
        }
    }
}