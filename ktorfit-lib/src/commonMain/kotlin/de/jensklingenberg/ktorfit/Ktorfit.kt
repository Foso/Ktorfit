package de.jensklingenberg.ktorfit

import de.jensklingenberg.ktorfit.Strings.Companion.EXPECTED_URL_SCHEME
import de.jensklingenberg.ktorfit.converter.SuspendResponseConverter
import de.jensklingenberg.ktorfit.converter.request.CoreResponseConverter
import de.jensklingenberg.ktorfit.converter.request.RequestConverter
import de.jensklingenberg.ktorfit.converter.request.ResponseConverter
import de.jensklingenberg.ktorfit.internal.KtorfitClient
import io.ktor.client.*
import io.ktor.client.engine.*


/**
 * Main class for Ktorfit, create the class than use the [create<T>()] function.
 */
class Ktorfit private constructor(
    val baseUrl: String,
    val httpClient: HttpClient = HttpClient(),
    val responseConverters: Set<ResponseConverter>,
    val suspendResponseConverters: Set<SuspendResponseConverter>,
    val requestConverters: Set<RequestConverter>
) {

    fun <T> create(ktorfitService: KtorfitService = DefaultKtorfitService()): T {
        if(ktorfitService is DefaultKtorfitService){
            throw IllegalArgumentException("You need to enable the Ktorfit Gradle Plugin")
        }
        ktorfitService.setClient(KtorfitClient(this))
        return ktorfitService as T
    }


    /**
     * Builder class for Ktorfit.
     *
     * @see baseUrl
     * @see httpClient
     */
    class Builder {
        private var _baseUrl: String = ""
        private var _httpClient = HttpClient()
        private var _responseConverter: MutableSet<ResponseConverter> = mutableSetOf()
        private var _suspendResponseConverter: MutableSet<SuspendResponseConverter> = mutableSetOf()
        private var _requestConverter: MutableSet<RequestConverter> = mutableSetOf()

        /**
         * That will be used for every request with object
         */
        fun baseUrl(url: String) = apply {
            if (url.isEmpty()) {
                throw IllegalStateException("Base URL required")
            }

            if (!url.endsWith("/")) {
                throw IllegalStateException("Base URL needs to end with /")
            }
            if (!url.startsWith("http") && !url.startsWith("https")) {
                throw IllegalStateException(EXPECTED_URL_SCHEME)
            }
            this._baseUrl = url
        }

        /**
         * Client that will be used for every request with object
         */
        fun httpClient(client: HttpClient) = apply {
            this._httpClient = client
        }

        /**
         * Build HttpClient by just passing an engine
         */
        fun httpClient(engine: HttpClientEngine) = apply {
            this._httpClient = HttpClient(engine)
        }

        /**
         * Build HttpClient by just passing an engine factory
         */
        fun <T : HttpClientEngineConfig> httpClient(engineFactory: HttpClientEngineFactory<T>) = apply {
            this._httpClient = HttpClient(engineFactory)
        }

        /**
         * Client-Builder that will be used for every request with object
         */
        fun httpClient(config: HttpClientConfig<*>.() -> Unit) = apply {
            this._httpClient = HttpClient(this._httpClient.engine, config)
        }

        /**
         * Client-Builder with engine that will be used for every request with object
         */
        fun httpClient(engine: HttpClientEngine, config: HttpClientConfig<*>.() -> Unit) = apply {
            this._httpClient = HttpClient(engine, config)
        }

        /**
         * Client-Builder with engine factory that will be used for every request with object
         */
        fun <T : HttpClientEngineConfig> httpClient(
            engineFactory: HttpClientEngineFactory<T>,
            config: HttpClientConfig<T>.() -> Unit
        ) = apply {
            this._httpClient = HttpClient(engineFactory, config)
        }

        /**
         * Use this to add [ResponseConverter] or [SuspendResponseConverter] for unsupported return types of requests
         */
        fun responseConverter(vararg converters: CoreResponseConverter) = apply {
            converters.forEach { converter ->
                if (converter is ResponseConverter) {
                    this._responseConverter.add(converter)
                }
                if (converter is SuspendResponseConverter) {
                    this._suspendResponseConverter.add(converter)
                }
            }
        }

        fun requestConverter(vararg converters: RequestConverter) = apply {
            this._requestConverter.addAll(converters)
        }


        /**
         * Apply changes to builder and get the Ktorfit instance without the need of calling [build] afterwards.
         */
        fun build(builder: Builder.() -> Unit) = this.apply(builder).build()

        /**
         * Creates an instance of Ktorfit with specified baseUrl and HttpClient.
         */
        fun build(): Ktorfit {
            return Ktorfit(_baseUrl, _httpClient, _responseConverter, _suspendResponseConverter, _requestConverter)
        }
    }
}

/**
 * Create a Ktorfit instance using Kotlin-DSL.
 */
fun ktorfit(builder: Ktorfit.Builder.() -> Unit) = Ktorfit.Builder().apply(builder).build()

/**
 * Creates a Ktorfit Builder instance using Kotlin-DSL.
 */
fun ktorfitBuilder(builder: Ktorfit.Builder.() -> Unit) = Ktorfit.Builder().apply(builder)

@Deprecated("Use the non-Extension function")
fun <T> Ktorfit.create(ktorfitService: KtorfitService = DefaultKtorfitService()): T {
    return this.create(ktorfitService)
}

interface KtorfitService {
    fun setClient(client: KtorfitClient)
}