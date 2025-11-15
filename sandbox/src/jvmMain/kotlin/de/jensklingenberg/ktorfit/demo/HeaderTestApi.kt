package de.jensklingenberg.ktorfit.demo

import com.example.model.People
import de.jensklingenberg.ktorfit.http.GET
import de.jensklingenberg.ktorfit.http.Header
import de.jensklingenberg.ktorfit.http.HeaderMap
import de.jensklingenberg.ktorfit.http.Headers
import de.jensklingenberg.ktorfit.http.Path

interface HeaderTestApi {
    @GET("people/{id}/")
    suspend fun multipleHeader(
        @Path("id") peopleId: Int,
        @Header("huhu") name: Array<String?>,
        @Header("hey") name2: String
    ): People

    @GET("people/{id}/")
    suspend fun <T> testHeaderWithArray(
        @Path("id") peopleId: Int,
        @Header("huhu") name: Array<String?>
    ): People

    @GET("people/{id}/")
    suspend fun testHeaderWithList(
        @Path("id") peopleId: Int,
        @Header("huhu") name: List<String?>
    ): People

    @Headers("Accept2: application/json", "Accept: application/json2")
    @GET("people/{id}/")
    suspend fun testHeaders(
        @Path("id") peopleId: Int
    ): People

    @GET("people/{id}/")
    suspend fun testHeaderMap(
        @Path("id") peopleId: Int,
        @HeaderMap() name: Map<String, Int>?
    ): People
}
