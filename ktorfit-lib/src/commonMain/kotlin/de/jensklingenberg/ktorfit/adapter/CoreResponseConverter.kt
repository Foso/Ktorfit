package de.jensklingenberg.ktorfit.adapter

interface CoreResponseConverter {

    fun supportedType(returnTypeName: String): Boolean

}

