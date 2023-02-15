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
    //Get all Parameter with @Field and add them to a list

    val fieldDataStringList = mutableListOf<String>()

    val fieldDataList = params.filter { it.hasAnnotation<Field>() }.map { parameterData ->
        val field = parameterData.annotations.filterIsInstance<Field>().first()
        val encoded = field.encoded
        val data = parameterData.name
        val fieldKey = field.value.surroundWith("\"")

        "$fieldKey,$data,$encoded"
    }

    fieldDataStringList.addAll(fieldDataList)

    val fieldMapStrings = params.filter { it.hasAnnotation<FieldMap>() }.map { parameterData ->
        val fieldMap = parameterData.findAnnotationOrNull<FieldMap>()!!
        val encoded = fieldMap.encoded
        val data = parameterData.name
        val keyName = "\"\""

        "$keyName,$data,$encoded"
    }

    fieldDataStringList.addAll(fieldMapStrings)

    return fieldDataStringList.joinToString { "DH($it)" }.surroundIfNotEmpty("fields = listOf(", ")")
}