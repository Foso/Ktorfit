package de.jensklingenberg.ktorfit.requestData


import de.jensklingenberg.ktorfit.model.ParameterData
import de.jensklingenberg.ktorfit.model.annotations.Field
import de.jensklingenberg.ktorfit.model.annotations.FieldMap
import de.jensklingenberg.ktorfit.utils.surroundIfNotEmpty
import de.jensklingenberg.ktorfit.utils.surroundWith

/**
 * Source for the "fields" argument of [de.jensklingenberg.ktorfit.RequestData]
 */

fun getFieldArgumentsText(params: List<ParameterData>): String {
    //Get all Parameter with @Field and add them to a map

    val fieldDataStringList = mutableListOf<String>()

    val fieldDataList = params.filter { it.hasAnnotation<Field>() }.map { parameterData ->
        val query = parameterData.annotations.filterIsInstance<Field>().first()
        val encoded = query.encoded
        val data = parameterData.name
        val queryKey = query.value.surroundWith("\"")

        "$queryKey,$data,$encoded"
    }

    fieldDataStringList.addAll(fieldDataList)

    val fieldMapStrings = params.filter { it.hasAnnotation<FieldMap>() }.map { parameterData ->
        val queryMap = parameterData.findAnnotationOrNull<FieldMap>()!!
        val encoded = queryMap.encoded
        val data = parameterData.name
        val keyName = "\"\""

        "$keyName,$data,$encoded"
    }

    fieldDataStringList.addAll(fieldMapStrings)

    return fieldDataStringList.joinToString { "DH($it)" }.surroundIfNotEmpty("fields = listOf(", ")")
}