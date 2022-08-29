package de.jensklingenberg.ktorfit.requestData


import de.jensklingenberg.ktorfit.model.ParameterData
import de.jensklingenberg.ktorfit.model.annotations.Query
import de.jensklingenberg.ktorfit.model.annotations.QueryMap
import de.jensklingenberg.ktorfit.model.annotations.QueryName
import de.jensklingenberg.ktorfit.surroundIfNotEmpty

/**
 * Source for the "queries" argument of [de.jensklingenberg.ktorfit.RequestData]
 */

fun getQueryArgumentText(params: List<ParameterData>): String {
    //Get all Parameter with @Query and add them to a list

    val myQueryStrings = mutableListOf<String>()

    val queryStrings = params.filter { it.hasAnnotation<Query>() }.map { myParam ->
        val query = myParam.annotations.filterIsInstance<Query>().first()
        val encoded = query.encoded
        val data = myParam.name
        val queryKey = "\"${query.value}\""
        val type = "QueryType.QUERY"

        "QueryData($encoded,$queryKey,$data,$type)"
    }

    myQueryStrings.addAll(queryStrings)


    val queryNamesStrings = params.filter { it.hasAnnotation<QueryName>() }.map { myParam ->
        val queryName = myParam.annotations.filterIsInstance<QueryName>().first()
        val encoded = queryName.encoded
        val data = myParam.name
        val queryKey = "\"\""
        val type = "QueryType.QUERYNAME"

        "QueryData($encoded,$queryKey,$data,$type)"
    }

    myQueryStrings.addAll(queryNamesStrings)

    val queryMapStrings = params.filter { it.hasAnnotation<QueryMap>() }.map { myParam ->
        val queryMap = myParam.findAnnotationOrNull<QueryMap>()!!
        val encoded = queryMap.encoded
        val data = myParam.name
        val queryKey = "\"\""
        val type = "QueryType.QUERYMAP"

        "QueryData($encoded,$queryKey,$data,$type)"
    }

    myQueryStrings.addAll(queryMapStrings)

    return myQueryStrings.joinToString { it }.surroundIfNotEmpty("queries = listOf(", ")")
}