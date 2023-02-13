package de.jensklingenberg.ktorfit.internal

public data class HeaderData(val key: String, val value: Any?)

public data class PathData(val key: String, val value: String, val encoded: Boolean)