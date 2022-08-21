package de.jensklingenberg.ktorfit.converter

import io.ktor.client.statement.*
import io.ktor.util.reflect.*

interface ResponseConverter : CoreResponseConverter {

    fun <PRequest : Any> wrapResponse(
        returnTypeName: String,
        requestFunction: suspend () -> Pair<TypeInfo, HttpResponse>
    ): Any

}
