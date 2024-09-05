package de.jensklingenberg.ktorfit.poetspec

import com.squareup.kotlinpoet.ParameterSpec
import com.squareup.kotlinpoet.TypeVariableName
import de.jensklingenberg.ktorfit.model.ParameterData

fun ParameterData.parameterSpec(filePath: String): ParameterSpec {
    val parameterType =
        if (this.isGeneric) {
            TypeVariableName(this.type.name)
        } else {
            this.type.parameterType.findTypeName(filePath)
        }
    return ParameterSpec(this.name, parameterType)
}
