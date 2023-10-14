package de.jensklingenberg.ktorfit.reqBuilderExtension

import de.jensklingenberg.ktorfit.model.annotations.*
import de.jensklingenberg.ktorfit.utils.surroundIfNotEmpty


/**
 * This will generate the source for timeout{...} that will be used in the HttpRequestBuilder extension
 */
fun getTimeoutCode(
    functionAnnotations: List<FunctionAnnotation>,
): String {
    val timeout = functionAnnotations
        .filterIsInstance<Timeout>()
        .firstOrNull() ?: return ""

    val requestTimeout = timeout
        .requestTimeout
        .let { if (it != Long.MAX_VALUE) "requestTimeoutMillis = $it\n" else ""  }
    val connectTimeout = timeout
        .connectTimeout
        .let { if (it != Long.MAX_VALUE) "connectTimeoutMillis = $it\n" else ""  }
    val socketTimeout = timeout
        .socketTimeout
        .let { if (it != Long.MAX_VALUE) "socketTimeoutMillis = $it\n" else ""  }
    return "$requestTimeout$connectTimeout$socketTimeout".surroundIfNotEmpty(
        "timeout {\n",
        "}"
    )
}