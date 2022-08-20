package de.jensklingenberg.ktorfit.adapter

sealed interface CoreResponseConverter {

    fun supportedType(returnTypeName: String): Boolean

}

