package com.example.ktorfittest

import de.jensklingenberg.ktorfit.http.GET
import de.jensklingenberg.ktorfit.http.Path

interface Test {
    @GET("people/{id}")
    suspend fun getPeopleById(
        @Path("id") id: Int
    ): Person
}
