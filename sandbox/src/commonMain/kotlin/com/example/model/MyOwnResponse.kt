package com.example.model

import kotlinx.serialization.Serializable

@Serializable sealed class MyOwnResponse<T> {
    data class Success<T>(val data: T) : MyOwnResponse<T>()
    class Error(val ex:Throwable) : MyOwnResponse<Nothing>()

    companion object {
        fun <T> success(data: T) = Success(data)
        fun error(ex: Throwable) = Error(ex)
    }
}