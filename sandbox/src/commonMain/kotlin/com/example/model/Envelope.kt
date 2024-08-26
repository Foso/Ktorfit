package com.example.model

@kotlinx.serialization.Serializable
data class Envelope(
    val success: Boolean,
    val user: User
)

@kotlinx.serialization.Serializable
data class User(
    val id: Int,
    val name: String
)
