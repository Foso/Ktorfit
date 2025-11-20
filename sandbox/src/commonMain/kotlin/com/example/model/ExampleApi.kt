package com.example.model

import com.example.api.MyConv
import de.jensklingenberg.ktorfit.core.TypeConverters
import de.jensklingenberg.ktorfit.http.GET
import kotlinx.coroutines.flow.Flow

@TypeConverters(MyConv::class)
interface ExampleApi {

    @GET("example.json")
    suspend fun getUser(): MyOwnResponse<User>

    @GET("example.json")
    suspend fun getUserResponse(): MyOwnResponse<User>

    @GET("example.json")
    suspend fun getUs(): User

    @GET("example.json")
    fun getUsFlow(): Flow<User>
}
