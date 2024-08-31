package com.example.model

@kotlinx.serialization.Serializable
data class Specie(
    val films: List<String?>? = null,
    val skinColors: String? = null,
    val homeworld: String? = null,
    val edited: String? = null,
    val created: String? = null,
    val eyeColors: String? = null,
    val language: String? = null,
    val classification: String? = null,
    val people: List<String?>? = null,
    val url: String? = null,
    val hairColors: String? = null,
    val averageHeight: String? = null,
    val name: String? = null,
    val designation: String? = null,
    val averageLifespan: String? = null
)
