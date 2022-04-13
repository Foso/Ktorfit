package de.jensklingenberg.ktorfit.internal

class QueryData(
    val encoded: Boolean = false,
    val data: Any?,
    val queryKey: String,
    val type: QueryType
)

enum class QueryType {
    QUERY, QUERYNAME, QUERYMAP
}

class FieldData(
    val encoded: Boolean = false,
    val data: Any?,
    val queryKey: String,
    val type: FieldType
)

enum class FieldType {
    FIELD, FIELDMAP
}