package de.jensklingenberg.ktorfit.converter.request

import de.jensklingenberg.ktorfit.Ktorfit
import de.jensklingenberg.ktorfit.internal.TypeData
import io.ktor.client.statement.*
import io.ktor.util.reflect.*

/**
 * Implement this to support wrapping for custom types
 *  e.g. fun test() : MyCustomType<String>
 */
@Deprecated("Use Converter.ResponseConverter")
internal interface ResponseConverter : CoreResponseConverter {

    /**
     * @param typeData is the qualified name of the outer type of
     * @param requestFunction a suspend function that will return a typeInfo of Ktor's requested type and the [HttpResponse]
     * the return type. e.g. for Flow<String> it will be kotlinx.coroutines.flow.Flow
     * @param ktorfit the Ktorfit object that is used for this request
     * @return the wrapped response
     */
    public fun <RequestType : Any?> wrapResponse(
        typeData: TypeData,
        requestFunction: suspend () -> Pair<TypeInfo, HttpResponse?>,
        ktorfit: Ktorfit
    ): Any

}
