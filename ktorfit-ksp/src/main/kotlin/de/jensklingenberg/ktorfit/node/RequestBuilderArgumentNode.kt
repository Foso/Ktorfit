package de.jensklingenberg.ktorfit.node

import de.jensklingenberg.ktorfit.model.MyParam

/**
 * Source for the "requestBuilder" argument of [de.jensklingenberg.ktorfit.RequestData]
 */
class RequestBuilderArgumentNode(private val params: List<MyParam>) : MyNode() {

    override fun toString(): String {
        return (params.find { it.hasRequestBuilderAnno }?.let {
            "requestBuilder = " + it.name
        } ?: "")
    }
}