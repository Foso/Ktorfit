package de.jensklingenberg.ktorfit.requestData

import de.jensklingenberg.ktorfit.hasAnnotation
import de.jensklingenberg.ktorfit.model.MyParam
import de.jensklingenberg.ktorfit.model.annotations.Part
import de.jensklingenberg.ktorfit.model.annotations.PartMap
import de.jensklingenberg.ktorfit.prefixIfNotEmpty
import de.jensklingenberg.ktorfit.surroundIfNotEmpty
import de.jensklingenberg.ktorfit.surroundWith

/**
 * Source for the "parts" argument of [de.jensklingenberg.ktorfit.RequestData]
 */

fun getPartsArgumentNode(params: List<MyParam>): String {
    val paramsWithPartMapAnno = params.filter { it.hasAnnotation<PartMap>() }
    val paramsWithPartAnno = params.filter { it.hasAnnotation<Part>() }

    var partsText = paramsWithPartAnno.joinToString { myParam ->
        val partParamName = myParam.name
        val partKeyName = myParam.annotations.filterIsInstance<Part>().first().value.surroundWith("\"")

        "$partKeyName to $partParamName"
    }.surroundIfNotEmpty("mapOf(", ")")


    partsText += paramsWithPartMapAnno.joinToString("") { myParam ->
        ("+".takeIf { partsText.isNotEmpty() } ?: "") + myParam.name
    }
    return partsText.prefixIfNotEmpty("parts = ")
}