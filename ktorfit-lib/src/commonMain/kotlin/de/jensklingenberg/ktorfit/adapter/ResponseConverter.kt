package de.jensklingenberg.ktorfit.adapter

import io.ktor.client.statement.*
import io.ktor.util.reflect.*

interface ResponseConverter {

    fun supportedType(returnTypeName: String): Boolean

    fun <PRequest : Any> wrapResponse(
        returnTypeName: String,
        requestFunction: suspend () -> Pair<TypeInfo, HttpResponse>
    ): Any

}
