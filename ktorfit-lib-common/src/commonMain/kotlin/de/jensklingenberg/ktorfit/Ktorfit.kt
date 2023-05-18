package de.jensklingenberg.ktorfit

import de.jensklingenberg.ktorfit.Strings.Companion.BASE_URL_REQUIRED
import de.jensklingenberg.ktorfit.Strings.Companion.ENABLE_GRADLE_PLUGIN
import de.jensklingenberg.ktorfit.Strings.Companion.EXPECTED_URL_SCHEME
import de.jensklingenberg.ktorfit.converter.DefaultSuspendConverter
import de.jensklingenberg.ktorfit.converter.SuspendResponseConverter
import de.jensklingenberg.ktorfit.converter.request.CoreResponseConverter
import de.jensklingenberg.ktorfit.converter.request.RequestConverter
import de.jensklingenberg.ktorfit.converter.request.ResponseConverter
import de.jensklingenberg.ktorfit.internal.DefaultKtorfitService
import de.jensklingenberg.ktorfit.internal.KtorfitClient
import de.jensklingenberg.ktorfit.internal.KtorfitService
import de.jensklingenberg.ktorfit.internal.TypeData
import io.ktor.client.*
import io.ktor.client.engine.*


/**
 * Main class for Ktorfit, create the class than use the [create<T>()] function.
 */
public class Ktorfit private constructor(
    public val baseUrl: String,
    public val httpClient: HttpClient = HttpClient(),
    public val responseConverters: Set<ResponseConverter>,
    public val suspendResponseConverters: Set<SuspendResponseConverter>,
    public val requestConverters: Set<RequestConverter>
) {

    /**
     * Returns the next ResponseConverter after [skipPast] that can handle [type]
     * or null if no one found
     */
    public fun nextResponseConverter(skipPast: ResponseConverter?, type: TypeData): ResponseConverter? {
        val start = responseConverters.indexOf(skipPast) + 1
        (start until responseConverters.size).forEach {
            val converter = responseConverters.toList()[it]
            if(converter.supportedType(type, false)){
                return converter
            }
        }
        return null
    }

    /**
     * Returns the next SuspendResponseConverter after [skipPast] that can handle [type]
     * or null if no one found
     */
    public fun nextSuspendResponseConverter(skipPast: SuspendResponseConverter?, type: TypeData): SuspendResponseConverter? {
        val start = suspendResponseConverters.indexOf(skipPast) + 1
        (start until suspendResponseConverters.size).forEach {
            val converter = suspendResponseConverters.toList()[it]
            if(converter.supportedType(type, true)){
                return converter
            }
        }
        return null
    }

    /**
     * This will return an implementation of [T] if [T] is an interface
     * with Ktorfit annotations.
     * @param ktorfitService Please keep the default parameter, it will be replaced
     * by the compiler plugin
     */
    public fun <T> create(ktorfitService: KtorfitService = DefaultKtorfitService()): T {
        if (ktorfitService is DefaultKtorfitService) {
            throw IllegalArgumentException(ENABLE_GRADLE_PLUGIN)
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
    public class Builder {
        private var _baseUrl: String = ""
        private var _httpClient = HttpClient()
        private var _responseConverter: MutableSet<ResponseConverter> = mutableSetOf()
        private var _suspendResponseConverter: MutableSet<SuspendResponseConverter> = mutableSetOf()
        private var _requestConverter: MutableSet<RequestConverter> = mutableSetOf()

        /**
         * That will be used for every request with object
         * @param url base url for every request
         * @param checkUrl if true, it checks if [url] ends with / and starts with http/https
         */
        public fun baseUrl(url: String, checkUrl: Boolean = true): Builder = apply {
            if (checkUrl && url.isEmpty()) {
                throw IllegalStateException(BASE_URL_REQUIRED)
            }

            if (checkUrl && !url.endsWith("/")) {
                throw IllegalStateException("Base URL needs to end with /")
            }
            if (checkUrl && !url.startsWith("http") && !url.startsWith("https")) {
                throw IllegalStateException(EXPECTED_URL_SCHEME)
            }
            this._baseUrl = url
        }

        /**
         * Client that will be used for every request with object
         */
        public fun httpClient(client: HttpClient): Builder = apply {
            this._httpClient = client
        }

        /**
         * Build HttpClient by just passing an engine
         */
        public fun httpClient(engine: HttpClientEngine): Builder = apply {
            this._httpClient = HttpClient(engine)
        }

        /**
         * Build HttpClient by just passing an engine factory
         */
        public fun <T : HttpClientEngineConfig> httpClient(engineFactory: HttpClientEngineFactory<T>): Builder = apply {
            this._httpClient = HttpClient(engineFactory)
        }

        /**
         * Client-Builder that will be used for every request with object
         */
        public fun httpClient(config: HttpClientConfig<*>.() -> Unit): Builder = apply {
            this._httpClient = HttpClient(this._httpClient.engine, config)
        }

        /**
         * Client-Builder with engine that will be used for every request with object
         */
        public fun httpClient(engine: HttpClientEngine, config: HttpClientConfig<*>.() -> Unit): Builder = apply {
            this._httpClient = HttpClient(engine, config)
        }

        /**
         * Client-Builder with engine factory that will be used for every request with object
         */
        public fun <T : HttpClientEngineConfig> httpClient(
            engineFactory: HttpClientEngineFactory<T>,
            config: HttpClientConfig<T>.() -> Unit
        ): Builder = apply {
            this._httpClient = HttpClient(engineFactory, config)
        }

        /**
         * Use this to add [ResponseConverter] or [SuspendResponseConverter] for unsupported return types of requests
         */
        public fun responseConverter(vararg converters: CoreResponseConverter): Builder = apply {
            converters.forEach { converter ->
                if (converter is ResponseConverter) {
                    this._responseConverter.add(converter)
                }
                if (converter is SuspendResponseConverter) {
                    this._suspendResponseConverter.add(converter)
                }
            }
        }

        public fun requestConverter(vararg converters: RequestConverter): Builder = apply {
            this._requestConverter.addAll(converters)
        }


        /**
         * Apply changes to builder and get the Ktorfit instance without the need of calling [build] afterwards.
         */
        public fun build(builder: Builder.() -> Unit): Ktorfit = this.apply(builder).build()

        /**
         * Creates an instance of Ktorfit with specified baseUrl and HttpClient.
         */
        public fun build(): Ktorfit {
            return Ktorfit(_baseUrl, _httpClient, _responseConverter, _suspendResponseConverter.also { it.add(
                DefaultSuspendConverter()
            ) }, requestConverters = _requestConverter)
        }
    }
}

/**
 * Create a Ktorfit instance using Kotlin-DSL.
 */
public fun ktorfit(builder: Ktorfit.Builder.() -> Unit): Ktorfit = Ktorfit.Builder().apply(builder).build()

/**
 * Creates a Ktorfit Builder instance using Kotlin-DSL.
 */
public fun ktorfitBuilder(builder: Ktorfit.Builder.() -> Unit): Ktorfit.Builder = Ktorfit.Builder().apply(builder)

@Deprecated("Use the non-Extension function")
public
        /**
         * This will return an implementation of [T] if [T] is an interface
         * with Ktorfit annotations.
         * @param ktorfitService Please keep the default parameter, it will be replaced
         * by the compiler plugin
         */
fun <T> Ktorfit.create(ktorfitService: KtorfitService = DefaultKtorfitService()): T {
    return this.create(ktorfitService)
}

