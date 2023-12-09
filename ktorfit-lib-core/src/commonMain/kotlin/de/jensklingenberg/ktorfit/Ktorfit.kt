package de.jensklingenberg.ktorfit

import de.jensklingenberg.ktorfit.Strings.Companion.BASE_URL_NEEDS_TO_END_WITH
import de.jensklingenberg.ktorfit.Strings.Companion.BASE_URL_REQUIRED
import de.jensklingenberg.ktorfit.Strings.Companion.ENABLE_GRADLE_PLUGIN
import de.jensklingenberg.ktorfit.Strings.Companion.EXPECTED_URL_SCHEME
import de.jensklingenberg.ktorfit.converter.Converter
import de.jensklingenberg.ktorfit.converter.TypeData
import de.jensklingenberg.ktorfit.internal.*
import io.ktor.client.*
import io.ktor.client.engine.*
import io.ktor.client.statement.*
import kotlin.reflect.KClass


/**
 * Main class for Ktorfit, create the class than use the [create<T>()] function.
 */
public class Ktorfit private constructor(
    public val baseUrl: String,
    public val httpClient: HttpClient = HttpClient(),
    private val converterFactories: List<Converter.Factory>
) {

    /**
     * Returns the next response converter from the list of converter factories,
     * starting from the specified current factory and matching the given type.
     * @param currentFactory The current converter factory.
     * @param type The type data to match.
     * @return The next response converter, or null if not found.
     */
    public fun nextResponseConverter(
        currentFactory: Converter.Factory?,
        type: TypeData
    ): Converter.ResponseConverter<HttpResponse, *>? {
        val start = converterFactories.indexOf(currentFactory) + 1
        return converterFactories
            .subList(start, converterFactories.size)
            .firstNotNullOfOrNull { it.responseConverter(type, this) }
    }

    /**
     * Returns the next [Converter.SuspendResponseConverter] from the list of converter factories,
     * starting from the specified current factory and matching the given type.
     * @param currentFactory The current converter factory.
     * @param type The type data to match.
     * @return The next [Converter.SuspendResponseConverter], or null if not found.
     */
    public fun nextSuspendResponseConverter(
        currentFactory: Converter.Factory?,
        type: TypeData
    ): Converter.SuspendResponseConverter<HttpResponse, *>? {
        val start = converterFactories.indexOf(currentFactory) + 1
        return converterFactories
            .subList(start, converterFactories.size)
            .firstNotNullOfOrNull { it.suspendResponseConverter(type, this) }
    }

    /**
     * Returns the next [RequestParameterConverter] after [currentFactory] that can handle [parameterType] and [requestType]
     * or null if no one found
     */
    internal fun nextRequestParameterConverter(
        currentFactory: Converter.Factory?,
        parameterType: KClass<*>,
        requestType: KClass<*>
    ): Converter.RequestParameterConverter? {
        val start = converterFactories.indexOf(currentFactory) + 1
        return converterFactories
            .subList(start, converterFactories.size)
            .firstNotNullOfOrNull { it.requestParameterConverter(parameterType, requestType) }
    }

    /**
     * This will return an implementation of [T] if [T] is an interface
     * with Ktorfit annotations.
     * @param data Please keep the default parameter, it will be replaced
     * by the compiler plugin
     */
    public fun <T> create(data: T? = null): T {
        if (data == null) {
            throw IllegalArgumentException(ENABLE_GRADLE_PLUGIN)
        }
        return data
    }


    /**
     * Builder class for Ktorfit.
     *
     * @see baseUrl
     * @see httpClient
     */
    public class Builder {
        private var _baseUrl: String = ""
        private var _httpClient: HttpClient? = null
        private var _factories: MutableSet<Converter.Factory> = mutableSetOf()

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
                throw IllegalStateException(BASE_URL_NEEDS_TO_END_WITH)
            }
            if (checkUrl && !url.startsWith("http") && !url.startsWith("https")) {
                throw IllegalStateException(EXPECTED_URL_SCHEME)
            }
            this._baseUrl = url
        }

        /**
         * Client that will be used for every request with object
         * @param client The HTTP client to be used.
         * @return The updated Builder instance.
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
            this._httpClient = HttpClient(config)
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
         * Add [Converter.Factory] with converters for unsupported return types of requests.
         * The converters coming from the factories will be used before the added [CoreResponseConverter]s
         */
        public fun converterFactories(vararg converters: Converter.Factory): Builder = apply {
            this._factories.addAll(converters)
        }

        /**
         * Apply changes to builder and get the Ktorfit instance without the need of calling [build] afterwards.
         */
        public fun build(builder: Builder.() -> Unit): Ktorfit = this.apply(builder).build()

        /**
         * Creates an instance of Ktorfit with specified baseUrl and HttpClient.
         */
        public fun build(): Ktorfit {
            return Ktorfit(
                baseUrl = _baseUrl,
                httpClient = _httpClient ?: HttpClient(),
                converterFactories = _factories.toList()
            )
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


