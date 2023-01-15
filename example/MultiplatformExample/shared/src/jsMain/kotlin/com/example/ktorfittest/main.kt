package com.example.ktorfittest

import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

//Run with jsNodeRun
fun main() {
    GlobalScope.launch {


        println("Launch")


        starWarsApi2.getPeopleByIdFlowResponse(3,null).collect {
            println("JS getPeopleByIdFlowResponse:" + it.name)
        }

        delay(3000)

    }
}