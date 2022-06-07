package de.jensklingenberg.androidonlyexample

import de.jensklingenberg.ktorfit.http.GET
import de.jensklingenberg.ktorfit.http.Path

interface GitHubService {
    @GET("repos/{user}/{repo}/releases/latest")
    suspend fun getLatestRelease(
        @Path("user") user: String,
        @Path("repo") repo: String,
    ): String
}