package de.jensklingenberg.ktorfit.internal

public class QueryData(
    public val key: String,
    public val data: Any?,
    public val encoded: Boolean = false,
    public val type: QueryType
)

public enum class QueryType {
    QUERY, QUERYNAME, QUERYMAP
}

public class FieldData(
    public val key: String,
    public val data: Any?,
    public val encoded: Boolean = false,
    public val type: FieldType
)

public enum class FieldType {
    FIELD, FIELDMAP
}