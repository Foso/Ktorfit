package com.example.ktorfittest

import de.jensklingenberg.ktorfit.KtorfitService
import de.jensklingenberg.ktorfit.internal.KtorfitClient
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

interface LocalTest{


    fun hey()
}


class _LocalTestImpl : LocalTest, KtorfitService{
    override fun setClient(client: KtorfitClient) {
        println("ddd")
    }

   override fun hey(){
        println("dja sdfaf")
    }

}

val starWarsApi3 = ktorfit.create<LocalTest>()

//Run with jsNodeRun
fun main() {
    println("ddd   d")
    GlobalScope.launch {
        println("ddd   d")

        starWarsApi3.hey()

        delay(3000)

    }
}