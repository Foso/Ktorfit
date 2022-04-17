package de.jensklingenberg.ktorfit.requestData

import de.jensklingenberg.ktorfit.hasAnnotation
import de.jensklingenberg.ktorfit.model.MyParam
import de.jensklingenberg.ktorfit.model.annotations.Body

/**
 * Source for the "bodyData" argument of [de.jensklingenberg.ktorfit.RequestData]
 */
class BodyDataArgumentNode(private val params: List<MyParam>)  {

    override fun toString(): String {
        var bodyText = ""
        params.firstOrNull { it.hasAnnotation<Body>() }?.let {
            bodyText = "bodyData = " + it.name
        }
        return bodyText
    }
}