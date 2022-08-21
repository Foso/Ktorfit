package de.jensklingenberg.ktorfit.converter

sealed interface CoreResponseConverter {

    /**
     * Check if this converter supports the return type
     * @param returnTypeName is the qualified name of the outer type of
     * the return type. e.g. for Flow<String> it will be kotlinx.coroutines.flow.Flow
     * @return if type is supported
     */
    fun supportedType(returnTypeName: String): Boolean

}

