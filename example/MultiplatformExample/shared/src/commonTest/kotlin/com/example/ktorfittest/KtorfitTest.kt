package com.example.ktorfittest

import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlin.test.Test
import kotlin.test.assertEquals

class KtorfitTest {
    @Test
    fun test() {
        GlobalScope.launch {
            ktorfit.create<TestApi>()
                .getPersonByIdResponse(3)
                .collect {
                    assertEquals(it.name, "R2-D2")
                }
        }
    }
}