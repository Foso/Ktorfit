package com.example.model

@kotlinx.serialization.Serializable

data class People(
    val films: List<String?>? = null,
    val homeworld: String? = null,
    val gender: String? = null,
    val skinColor: String? = null,
    val edited: String? = null,
    val created: String? = null,
    val mass: String? = null,
    //val vehicles: List<Any?>? = null,
    val url: String? = null,
    val hairColor: String? = null,
    val birthYear: String? = null,
    val eyeColor: String? = null,
    //val species: List<Specie?>? = null,
    //val starships: List<Any?>? = null,
    val name: String? = null,
    val height: String? = null
)

