package com.fc.githubrepository.utility

import com.fc.githubrepository.data.entity.GithubRepoEntity
import com.fc.githubrepository.data.response.GithubRepositoryResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface GithubApiService {
    @GET("search/repositories")
    suspend fun searchRepositories(@Query("q") query: String): Response<GithubRepositoryResponse>

    @GET("repos/{owner}/{name}")
    suspend fun getRepository(
        @Path("owner") ownerLogin: String,
        @Path("name") repoName: String
    ): Response<GithubRepoEntity>
}