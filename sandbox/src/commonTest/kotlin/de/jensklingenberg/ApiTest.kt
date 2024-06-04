package de.jensklingenberg

import de.jensklingenberg.ktorfit.http.GET


interface ApiTest {
    @GET("")
    suspend fun get(): String
}
