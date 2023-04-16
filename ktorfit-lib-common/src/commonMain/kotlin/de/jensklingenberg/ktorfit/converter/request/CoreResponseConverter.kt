package de.jensklingenberg.ktorfit.converter.request

import de.jensklingenberg.ktorfit.internal.TypeData

public interface CoreResponseConverter {

    /**
     * Check if this converter supports the return type
     * @param typeData is the typeData of the outer type of
     * the return type. e.g. for Flow<String> it will be kotlinx.coroutines.flow.Flow
     */
    public fun supportedType(typeData: TypeData, isSuspend: Boolean): Boolean

}

