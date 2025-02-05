package de.jensklingenberg.ktorfit

import io.ktor.client.request.HttpRequestBuilder
import io.ktor.util.AttributeKey

public val annotationsAttributeKey: AttributeKey<List<Any>> = AttributeKey("__ktorfit_attribute_annotations")

public val HttpRequestBuilder.annotations: List<Any>
    get() = attributes.getOrNull(annotationsAttributeKey) ?: emptyList()
