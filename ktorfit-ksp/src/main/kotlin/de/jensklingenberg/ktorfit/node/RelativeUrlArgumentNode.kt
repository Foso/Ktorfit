package de.jensklingenberg.ktorfit.node

import de.jensklingenberg.ktorfit.findAnnotationOrNull
import de.jensklingenberg.ktorfit.hasAnnotation
import de.jensklingenberg.ktorfit.model.MyFunction
import de.jensklingenberg.ktorfit.model.annotations.Path
import de.jensklingenberg.ktorfit.model.annotations.Url

/**
 * Source for the "relativeUrl" argument of [de.jensklingenberg.ktorfit.RequestData]
 */
class RelativeUrlArgumentNode(private val myFunction: MyFunction) : MyNode() {

    override fun toString(): String {
        val methodAnnotation = myFunction.httpMethodAnnotation

        var urlPath = ""

        if (methodAnnotation.path.isNotEmpty()) {
            //url="posts"
            urlPath = methodAnnotation.path
        } else {
            myFunction.params.firstOrNull { it.hasAnnotation<Url>() }?.let {
                //url=$foo
                urlPath = "\${" + it.name + "}"
            }
        }
        /**
         * Replace all values with curly braces in url path to corresponding annotated parameter names
         */
        myFunction.params.filter { it.hasAnnotation<Path>() }.forEach { myParam ->
            val paramName = myParam.name
            val pathAnnotation = myParam.findAnnotationOrNull<Path>()
            val pathPath = pathAnnotation?.value ?: ""
            val pathEncoded = pathAnnotation?.encoded ?: false

            val newPathValue = if (!pathEncoded) {
                "\${client.encode($paramName)}"
            } else {
                "\${$paramName}"
            }

            urlPath = urlPath.replace("{${pathPath}}", newPathValue)
        }


        return "relativeUrl=\"$urlPath\""
    }
}