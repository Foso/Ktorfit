package de.jensklingenberg.ktorfit.model

import com.google.devtools.ksp.symbol.KSType
import com.squareup.kotlinpoet.TypeName

data class ReturnTypeData(
    val name: String,
    val parameterType: KSType,
    val typeName: TypeName? = null
)
