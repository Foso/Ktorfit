package de.jensklingenberg.ktorfit

import io.ktor.client.request.HttpRequest
import io.ktor.client.request.HttpRequestBuilder
import io.ktor.util.AttributeKey

public val annotationsAttributeKey: AttributeKey<List<Any>> = AttributeKey("__ktorfit_attribute_annotations")

public val HttpRequest.annotations: List<Any>
    inline get() = attributes.getOrNull(annotationsAttributeKey) ?: emptyList()

public val HttpRequestBuilder.annotations: List<Any>
    inline get() = attributes.getOrNull(annotationsAttributeKey) ?: emptyList()
