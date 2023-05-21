package de.jensklingenberg.ktorfit.converter

import de.jensklingenberg.ktorfit.Ktorfit
import de.jensklingenberg.ktorfit.converter.request.CoreResponseConverter
import de.jensklingenberg.ktorfit.internal.TypeData
import io.ktor.client.statement.*
import io.ktor.util.reflect.*

/**
 * Implement this to support wrapping for custom types
 *  e.g. fun test() : MyCustomType<String>
 *    suspend  fun test() : MyCustomType<String>
 */
@Deprecated("Use Converter.SuspendResponseConverter")
public interface SuspendResponseConverter : CoreResponseConverter {

    /**
     * @param typeData contains information about the type that is being requested
     * @param requestFunction a suspend function that will return a typeInfo of Ktor's requested type and the [HttpResponse]
     * @return the converted response
     */
    public suspend fun <RequestType : Any?> wrapSuspendResponse(
        typeData: TypeData,
        requestFunction: suspend () -> Pair<TypeInfo, HttpResponse>,
        ktorfit: Ktorfit
    ): Any

}