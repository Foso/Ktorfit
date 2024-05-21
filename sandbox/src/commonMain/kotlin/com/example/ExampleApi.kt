package com.example

import com.example.model.People
import de.jensklingenberg.ktorfit.http.GET

interface ExampleApi  {
    @GET("/test")
    suspend fun exampleGet(): People
}