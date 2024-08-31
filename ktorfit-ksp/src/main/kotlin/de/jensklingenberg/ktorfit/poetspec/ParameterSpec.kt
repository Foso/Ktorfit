package de.jensklingenberg.ktorfit.poetspec

import com.squareup.kotlinpoet.ParameterSpec
import de.jensklingenberg.ktorfit.model.ParameterData

fun ParameterData.parameterSpec(filePath: String): ParameterSpec {
    val parameterType = findTypeName(this.type.parameterType, filePath)
    return ParameterSpec(this.name, parameterType)
}
