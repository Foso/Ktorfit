package de.jensklingenberg.ktorfit.converter

sealed interface CoreResponseConverter {

    /**
     * @param returnTypeName is the qualified name of the outer type of
     * the return type. e.g. for Flow<String> it will be kotlinx.coroutines.flow.Flow
     */
    fun supportedType(returnTypeName: String): Boolean

}

