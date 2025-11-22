package de.jensklingenberg.androidonlyexample

import kotlinx.serialization.Serializable

@Serializable
data class GitHubUser(
    val login: String? = null,
    val id: Long? = null,
    val avatar_url: String? = null,
    val html_url: String? = null,
    val name: String? = null,
    val company: String? = null,
    val blog: String? = null,
    val location: String? = null,
    val email: String? = null,
    val bio: String? = null
)

