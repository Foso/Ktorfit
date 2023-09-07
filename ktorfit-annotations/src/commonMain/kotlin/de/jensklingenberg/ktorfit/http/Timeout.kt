package de.jensklingenberg.ktorfit.http

/**
 *  Add timeout to a request
 *  Notice: your project's ktor client must install HttpTimeout Plugins to enable timeout function.
 * @param requestTimeout Specifies a request timeout in milliseconds. The request timeout is the time period required to process an HTTP call: from sending a request to receiving a response.
 * @param connectTimeout Specifies a connection timeout in milliseconds. The connection timeout is the time period in which a client should establish a connection with a server.
 * @param socketTimeout Specifies a socket timeout (read and write) in milliseconds. The socket timeout is the maximum time of inactivity between two data packets when exchanging data with a server.
 */
@Target(AnnotationTarget.FUNCTION)
annotation class Timeout(
    val requestTimeout: Long = Long.MAX_VALUE,
    val connectTimeout: Long = Long.MAX_VALUE,
    val socketTimeout: Long = Long.MAX_VALUE
)