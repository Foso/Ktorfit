package de.jensklingenberg.ktorfit.converter

import de.jensklingenberg.ktorfit.Ktorfit
import io.ktor.client.statement.*
import kotlin.reflect.KClass

public interface Converter<F, T> {

    /**
     * Converter that transform the HTTPResponse within a non-suspend request
     * e.g. fun getPost(): Call<Post>
     * This is only needed for the return type of a non-suspend request, for every other case use [SuspendResponseConverter]
     * @since 1.4.0
     */
    public interface ResponseConverter<F : HttpResponse, T> : Converter<HttpResponse, T> {

        /**
         * @param getResponse A suspend function that returns the HttpResponse to be converted.
         * @return the converted [HttpResponse]
         */
        public fun convert(getResponse: suspend () -> HttpResponse): T
    }

    /**
     * Converter that transform the HTTPResponse within a suspend request
     * e.g. suspend fun getPost(): Post
     * @since 1.4.0
     */
    public interface SuspendResponseConverter<F : HttpResponse, T> : Converter<HttpResponse, T> {

        /**
         *
         * @return the converted [HttpResponse]
         */
        @Deprecated("Use convert(result: KtorfitResult)")
        public suspend fun convert(response: HttpResponse): T

        public suspend fun convert(result: KtorfitResult): T {
            return when (result) {
                is KtorfitResult.Failure -> {
                    throw result.throwable
                }

                is KtorfitResult.Success -> {
                    convert(result.response)
                }
            }
        }
    }

    public interface RequestParameterConverter : Converter<Any, Any> {

        /**
         * Convert given [data]
         * @return the converted [data]
         */
        public fun convert(data: Any): Any
    }

    /**
     * This will return the upper bound type or null if that index does not exist
     *
     * Example: Response<String> will return String as TypeData with getUpperBoundType(0,type)
     */
    public fun getUpperBoundType(index: Int, type: TypeData): TypeData? {
        return type.typeArgs[index]
    }

    public interface Factory {

        /**
         * Return a [ResponseConverter] that can handle [typeData] or else null
         */
        public fun responseConverter(typeData: TypeData, ktorfit: Ktorfit): ResponseConverter<HttpResponse, *>? {
            return null
        }

        /**
         * Return a [RequestParameterConverter] that can handle [parameterType] and [requestType] or else null
         */
        public fun requestParameterConverter(
            parameterType: KClass<*>,
            requestType: KClass<*>
        ): RequestParameterConverter? {
            return null
        }

        /**
         * Return a [SuspendResponseConverter] that can handle [typeData] or else null
         */
        public fun suspendResponseConverter(
            typeData: TypeData,
            ktorfit: Ktorfit
        ): SuspendResponseConverter<HttpResponse, *>? {
            return null
        }
    }
}

