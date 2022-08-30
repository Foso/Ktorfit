package de.jensklingenberg.ktorfit.internal

class QueryData(
    val key: String,
    val data: Any?,
    val encoded: Boolean = false,
    val type: QueryType
)

enum class QueryType {
    QUERY, QUERYNAME, QUERYMAP
}

class FieldData(
    val key: String,
    val data: Any?,
    val encoded: Boolean = false,
    val type: FieldType
)

enum class FieldType {
    FIELD, FIELDMAP
}