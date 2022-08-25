package de.jensklingenberg.ktorfit.internal

data class HeaderData(val key: String, val value: Any?)

data class PathData(val key:String,val encoded:Boolean,val value: String)