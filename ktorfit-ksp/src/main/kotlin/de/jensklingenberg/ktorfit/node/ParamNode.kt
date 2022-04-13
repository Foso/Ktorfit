package de.jensklingenberg.ktorfit.node

import de.jensklingenberg.ktorfit.model.MyParam

/**
 * Generate the source code for the function parameters
 */
class ParamNode(private val myParam: MyParam) : MyNode() {

    override fun toString(): String {
        return myParam.name + ":" + myParam.type.name
    }
}