package de.jensklingenberg.ktorfit.requestData


import de.jensklingenberg.ktorfit.model.ParameterData
import de.jensklingenberg.ktorfit.model.annotations.Part
import de.jensklingenberg.ktorfit.model.annotations.PartMap
import de.jensklingenberg.ktorfit.utils.prefixIfNotEmpty
import de.jensklingenberg.ktorfit.utils.surroundIfNotEmpty
import de.jensklingenberg.ktorfit.utils.surroundWith

/**
 * Source for the "parts" argument of [de.jensklingenberg.ktorfit.RequestData]
 */

fun getPartsArgumentText(params: List<ParameterData>): String {
    val paramsWithPartMapAnno = params.filter { it.hasAnnotation<PartMap>() }
    val paramsWithPartAnno = params.filter { it.hasAnnotation<Part>() }
    val partStrings = mutableListOf<String>()
    val partsText = paramsWithPartAnno.joinToString { myParam ->
        val partParamName = myParam.name
        val partKeyName = myParam.annotations.filterIsInstance<Part>().first().value.surroundWith("\"")

        "$partKeyName to $partParamName"
    }.surroundIfNotEmpty("mapOf(", ")")
    partStrings.add(partsText)

    paramsWithPartMapAnno.forEach { myParam ->
        partStrings.add(myParam.name)
    }

    return partStrings.filter { it.isNotEmpty() }.joinToString("+") { it }.prefixIfNotEmpty("parts = ")
}