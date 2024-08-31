package com.example.model.github

data class TestReee(
    val testReee: List<TestReeeItem?>? = null
)

@kotlinx.serialization.Serializable
data class Author(
    val date: String? = null,
    val name: String? = null,
    val email: String? = null,
    val gistsUrl: String? = null,
    val reposUrl: String? = null,
    val followingUrl: String? = null,
    val starredUrl: String? = null,
    val login: String? = null,
    val followersUrl: String? = null,
    val type: String? = null,
    val url: String? = null,
    val subscriptionsUrl: String? = null,
    val receivedEventsUrl: String? = null,
    val avatarUrl: String? = null,
    val eventsUrl: String? = null,
    val htmlUrl: String? = null,
    val siteAdmin: Boolean? = null,
    val id: Int? = null,
    val gravatarId: String? = null,
    val nodeId: String? = null,
    val organizationsUrl: String? = null
)

@kotlinx.serialization.Serializable
data class Tree(
    val sha: String? = null,
    val url: String? = null
)

@kotlinx.serialization.Serializable
data class TestReeeItem(
    val committer: Committer? = null,
    val author: Author? = null,
    val htmlUrl: String? = null,
    val commit: Commit? = null,
    val commentsUrl: String? = null,
    val sha: String? = null,
    val url: String? = null,
    val nodeId: String? = null,
    val parents: List<ParentsItem?>? = null
)

@kotlinx.serialization.Serializable
data class Verification(
    val reason: String? = null,
    val signature: String? = null,
    val payload: String? = null,
    val verified: Boolean? = null
)

@kotlinx.serialization.Serializable
data class Committer(
    val date: String? = null,
    val name: String? = null,
    val email: String? = null,
    val gistsUrl: String? = null,
    val reposUrl: String? = null,
    val followingUrl: String? = null,
    val starredUrl: String? = null,
    val login: String? = null,
    val followersUrl: String? = null,
    val type: String? = null,
    val url: String? = null,
    val subscriptionsUrl: String? = null,
    val receivedEventsUrl: String? = null,
    val avatarUrl: String? = null,
    val eventsUrl: String? = null,
    val htmlUrl: String? = null,
    val siteAdmin: Boolean? = null,
    val id: Int? = null,
    val gravatarId: String? = null,
    val nodeId: String? = null,
    val organizationsUrl: String? = null
)

@kotlinx.serialization.Serializable
data class ParentsItem(
    val htmlUrl: String? = null,
    val sha: String? = null,
    val url: String? = null
)

@kotlinx.serialization.Serializable
data class Commit(
    val commentCount: Int? = null,
    val committer: Committer? = null,
    val author: Author? = null,
    val tree: Tree? = null,
    val message: String? = null,
    val url: String? = null,
    val verification: Verification? = null
)
