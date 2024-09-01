package com.example.model

import kotlinx.serialization.Serializable

@Serializable
data class Post(
    val userId: Int,
    val id: Int,
    val title: String,
    val body: String
)

@Serializable
data class Comment(
    val postId: Int,
    val id: Int,
    val name: String,
    val body: String,
    val email: String
)
