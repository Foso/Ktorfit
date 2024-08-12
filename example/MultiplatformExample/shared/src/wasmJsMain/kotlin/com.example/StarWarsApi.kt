package com.exampleimport
import de.jensklingenberg.ktorfit.http.GET
import de.jensklingenberg.ktorfit.http.Path
import kotlinx.coroutines.flow.Flow

interface WasmStarWarsApi {
    @GET("people/{id}/")
    suspend fun getPersonByIdResponse(
        @Path("id") peopleId: Int
    ): String

    @GET("people/{id}/")
    fun getPersonByIdFlowResponse(
        @Path("id") peopleId: Int
    ): Flow<String>
}
