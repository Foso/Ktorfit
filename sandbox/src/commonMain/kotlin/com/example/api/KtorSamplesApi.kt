package com.example.api

import de.jensklingenberg.ktorfit.http.*
import io.ktor.client.request.forms.*
import io.ktor.http.*
import io.ktor.http.content.*

interface KtorSamplesApi {

    companion object {
        const val baseUrl = "http://localhost:8080/"
    }

    var test: String?

    val test1: String

    @POST("signup")
    suspend fun sendReg(@Body param: Parameters): String

    //client-submit-form
    @POST("signup")
    @FormUrlEncoded
    suspend fun signup(
        @Field("username") username: String?,
        @Field("email") email: String,
        @Field("password") password: String,
        @Field("confirmation") confirmation: String
    ): String


    //client-submit-form
    @POST("signup")
    @FormUrlEncoded
    suspend fun signup(
        @FieldMap fieldMap: Map<String, String>?,
        @Field("username", encoded = true) email: List<String>
    ): String

    @Multipart
    @POST("upload")
    suspend fun uploadFile(@Part("description") description: String, @Part("") file: List<PartData>): String

    @POST("upload")
    suspend fun upload(@Body map: MultiPartFormDataContent)
}
