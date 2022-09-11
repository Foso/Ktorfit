package com.example.api

import com.example.model.github.GithubFollowerResponseItem
import com.example.model.github.Issuedata
import com.example.model.github.TestReeeItem
import de.jensklingenberg.ktorfit.http.*
import kotlinx.coroutines.flow.Flow

interface GithubService {

    companion object {
        const val baseUrl = "https://api.github.com/"
    }

    @Headers(
        "Accept: application/vnd.github.v3+json",
        "Authorization: token ghp_abcdefgh",
        "Content-Type: application/json"
    )
    @POST("repos/foso/experimental/issues")
    suspend fun createIssue(@Body body: Issuedata, @Header("Acci") headi: String?): String

    @Headers(
        "Accept: application/vnd.github.v3+json",
        "Authorization: token ghp_abcdefgh",
        "Content-Type: application/json"
    )
    @GET("user/followers")
    fun getFollowers(): Flow<List<GithubFollowerResponseItem>>

    @Headers(
        "Accept: application/vnd.github.v3+json",
        "Authorization: token ghp_abcdefgh",
        "Content-Type: application/json"
    )
    @GET("repos/{owner}/{repo}/commits")
    fun listCommits(@Path("owner") owner: String, @Path("repo") repo: String): Flow<List<TestReeeItem>>

}