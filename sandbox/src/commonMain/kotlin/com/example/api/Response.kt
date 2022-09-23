package com.example.api

sealed class Response<T> {
    data class Success<T>(val data: T) : Response<T>()
    class Error(val ex:Throwable) : Response<Nothing>()

    companion object {
        fun <T> success(data: T) = Success(data)
        fun error(ex: Throwable) = Error(ex)
    }
}
