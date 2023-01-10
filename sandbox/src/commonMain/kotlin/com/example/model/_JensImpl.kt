package com.example.model

import de.jensklingenberg.ktorfit.Hidden
import de.jensklingenberg.ktorfit.internal.KtorfitClient

class _JensImpl : Jens, Hidden {
    override fun setClient(client: KtorfitClient) {

    }

    override fun print(){
        println("dddd")
    }

}