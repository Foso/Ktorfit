package de.jensklingenberg.ktorfit.demo

sealed class Response<T> {
    data class Success<T>(val data: T) : Response<T>()
    object Error : Response<Nothing>()

    companion object {
        fun <T> success(data: T) = Response.Success(data)
        fun error(ex: Throwable) = Response.Error
    }
}
