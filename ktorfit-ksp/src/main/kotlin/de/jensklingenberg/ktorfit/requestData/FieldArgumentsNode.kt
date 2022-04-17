package de.jensklingenberg.ktorfit.requestData

import de.jensklingenberg.ktorfit.findAnnotationOrNull
import de.jensklingenberg.ktorfit.hasAnnotation
import de.jensklingenberg.ktorfit.model.MyParam
import de.jensklingenberg.ktorfit.model.annotations.Field
import de.jensklingenberg.ktorfit.model.annotations.FieldMap
import de.jensklingenberg.ktorfit.surroundIfNotEmpty
import de.jensklingenberg.ktorfit.surroundWith

/**
 * Source for the "fields" argument of [de.jensklingenberg.ktorfit.RequestData]
 */
private class FieldArgumentsNode(private val params: List<MyParam>)  {

    override fun toString(): String {
        //Get all Parameter with @Field and add them to a map

        val myFieldStrings = mutableListOf<String>()

        val fieldStrings = params.filter { it.hasAnnotation<Field>() }.map { myParam ->
            val query = myParam.annotations.filterIsInstance<Field>().first()
            val encoded = query.encoded
            val data = myParam.name
            val queryKey = query.value.surroundWith("\"")
            val type = "FieldType.FIELD"

            "FieldData($encoded,$data,$queryKey,$type)"
        }

        myFieldStrings.addAll(fieldStrings)

        val fieldMapStrings = params.filter { it.hasAnnotation<FieldMap>() }.map { myParam ->
            val queryMap = myParam.findAnnotationOrNull<FieldMap>()!!
            val encoded = queryMap.encoded
            val data = myParam.name
            val keyName = "\"\""
            val type = "FieldType.FIELDMAP"

            "FieldData($encoded,$data,$keyName,$type)"
        }

        myFieldStrings.addAll(fieldMapStrings)

        return myFieldStrings.joinToString { it }.surroundIfNotEmpty("fields = listOf(", ")")
    }
}

fun getFieldArgumentsText(params: List<MyParam>)  = FieldArgumentsNode(params).toString()