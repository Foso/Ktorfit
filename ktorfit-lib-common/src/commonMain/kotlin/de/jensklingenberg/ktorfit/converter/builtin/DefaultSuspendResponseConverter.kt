package de.jensklingenberg.ktorfit.converter.builtin

import de.jensklingenberg.ktorfit.Ktorfit
import de.jensklingenberg.ktorfit.converter.SuspendResponseConverter
import de.jensklingenberg.ktorfit.internal.TypeData
import io.ktor.client.statement.*
import io.ktor.util.reflect.*

/**
 * Will be used when no other suspend converter was found
 */
internal class DefaultSuspendResponseConverter : SuspendResponseConverter {
    override suspend fun <RequestType> wrapSuspendResponse(
        typeData: TypeData,
        requestFunction: suspend () -> Pair<TypeInfo, HttpResponse>,
        ktorfit: Ktorfit
    ): Any {
        val (info, response) = requestFunction()
        return try {
            response.call.body(typeData.typeInfo)
        } catch (exception: Exception) {
            throw exception
        }

    }

    override fun supportedType(typeData: TypeData, isSuspend: Boolean): Boolean {
        return true
    }

}