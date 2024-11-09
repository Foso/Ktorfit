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

    // client-submit-form
    @POST("signup")
    @FormUrlEncoded
    suspend fun signup(
        @FieldMap fieldMap: Map<String, String>?,
        @Field("username", encoded = true) email: List<String>
    ): String


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
    data class JensTest(
        val names: List<String>
    )

    @Headers(
        "Content-Type: application/json",
        "Accept: application/json"
    )
    @POST("example/request")
    suspend fun query(
        @Body query: Query
    ): List<NotWorking> // not sure if non-list works, haven't tested
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
