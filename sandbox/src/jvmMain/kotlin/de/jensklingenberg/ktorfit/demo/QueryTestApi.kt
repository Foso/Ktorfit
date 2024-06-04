package de.jensklingenberg.ktorfit.demo

import com.example.WebSocket
import com.example.model.People
import de.jensklingenberg.ktorfit.http.*
import ktorfit.Test

interface QueryTestApi {

    @GET("people/{id}/")
    suspend fun testQueryWithEncodedString(
        @Path("id") peopleId: Int,
        @Query("huhu", true) name: String?,
        @Query("huhu2", false) name2: String
    ): People

    @WebSocket
    @GET("people/{id}/")
    suspend fun testQueryWithEncodedInt(@Path("id") peopleId: Int, @Query("huhu", true) name: Int): People

    @GET("people/{id}/")
    suspend fun testQueryWithString(@Path("id") peopleId: Int, @Query("huhu", false) name: String): People

    @GET("people/{id}/")
    suspend fun testQueryWithEncodedArray(@Path("id") peopleId: Int, @Query("huhu", true) name: Array<String?>): People

    @GET("people/{id}/")
    suspend fun testQueryWithList(@Path("id") peopleId: Int, @Query("huhu") name: List<String?>): People

    @GET("people/{id}/")
    suspend fun testQueryWithEncodedList(@Path("id") peopleId: Int, @Query("huhu", true) name: List<String?>): People

    @GET("people/{id}/")
    suspend fun testQueryName(@Path("id") peopleId: Int, @QueryName name: String): People

    @GET("people/{id}/")
    suspend fun testQueryNameList(@Path("id") peopleId: Int, @QueryName(false) name: List<String?>): People

    @GET("people/{id}/")
    suspend fun testQueryEncodedMap(
        @Path("id") peopleId: Int,
        @QueryMap name: Map<String, Test?>?,
        @QueryMap(true) name2: Map<String, Test?>?
    ): People


}