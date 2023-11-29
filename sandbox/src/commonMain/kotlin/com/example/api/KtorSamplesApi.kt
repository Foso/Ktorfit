package com.example.api

import com.example.api.Query.NotWorking
import de.jensklingenberg.ktorfit.http.*
import de.jensklingenberg.ktorfit.http.Headers
import io.ktor.client.request.forms.*
import io.ktor.http.*
import io.ktor.http.content.*
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

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
        @Field("username", true) headers: String?,
        @Field email: String,
        @Field("password") password: String,
        @Field("confirmation") confirmation: String,
        @Field("names") names: List<String>

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
    suspend fun uploadFile(@Part("description") description: String, @Part("list") file: List<PartData>,@PartMap() map : Map<String,PartData>): String

    @POST("upload")
    suspend fun upload(@Body map: MultiPartFormDataContent)
}


data class Query(
    val working: Working
) {
    data class Working(
         val data: String
    )


    data class NotWorking(
         val reponse: String
    )
}


interface API {
    data class JensTest(val names: List<String>)

    @Headers(
        "Content-Type: application/json", "Accept: application/json"
    )
    @POST("example/request")
    suspend fun query(@Body query: Query): List<NotWorking> // not sure if non-list works, haven't tested
}

class KtorfitTest {
    @Serializable
    data class TestData(
        @SerialName("code")
        val code: Int,
    )
}

interface ITest {
    @GET("test")
    fun test(): KtorfitTest.TestData
}