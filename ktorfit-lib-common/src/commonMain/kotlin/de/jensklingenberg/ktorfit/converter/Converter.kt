package de.jensklingenberg.ktorfit.converter

import de.jensklingenberg.ktorfit.Ktorfit
import de.jensklingenberg.ktorfit.internal.TypeData
import io.ktor.client.statement.*

public interface Converter<F, T> {

    /**
     * Converter that transform the HTTPResponse within a non-suspend request
     * e.g. fun getPost(): Call<Post>
     * Here a converter for Call is needed because Ktor relies on Coroutines for doing requests
     */
    public interface ResponseConverter<F : HttpResponse, T> : Converter<HttpResponse, T> {
        public fun convert(getResponse: suspend () -> HttpResponse): T
    }

    /**
     * Converter that transform the HTTPResponse within a suspend request
     * e.g. suspend fun getPost(): Post
     */
    public interface SuspendResponseConverter<F : HttpResponse, T> : Converter<HttpResponse, T> {
        public suspend fun convert(response: HttpResponse): T
    }

    public fun getUpperBoundType(index: Int, type: TypeData): TypeData? {
        return type.typeArgs[index]
    }

    public interface Factory {

        public fun responseConverter(typeData: TypeData, ktorfit: Ktorfit): ResponseConverter<HttpResponse, *>? {
            return null
        }

        public fun suspendResponseConverter(
            typeData: TypeData,
            ktorfit: Ktorfit
        ): SuspendResponseConverter<HttpResponse, *>? {
            return null
        }
    }
}

