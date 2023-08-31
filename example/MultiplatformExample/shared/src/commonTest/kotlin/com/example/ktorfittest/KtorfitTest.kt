package com.example.ktorfittest

import kotlin.test.Test

class KtorfitTest {
    @Test
    fun test() {
        println("test")
        val starWarsApi = ktorfit.create<TestApi>()
        // Compile ERROR 👇👇
        // > Task :shared:compileTestKotlinJvm FAILED
        // e: java.lang.IllegalStateException: _TestApiImpl() not found, did you apply the Ksp Ktorfit plugin?
    }
}