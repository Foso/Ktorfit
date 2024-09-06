package de.jensklingenberg.ktorfit.poetspec

import com.squareup.kotlinpoet.ParameterSpec
import de.jensklingenberg.ktorfit.model.ParameterData

fun ParameterData.parameterSpec(): ParameterSpec {
    val parameterType = this.type.typeName ?: throw IllegalStateException("Type ${this.name} not found")
    return ParameterSpec(this.name, parameterType)
}
