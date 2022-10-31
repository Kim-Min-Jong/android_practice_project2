package com.fc.githubrepository.data.response

import com.fc.githubrepository.data.entity.GithubRepoEntity

data class GithubRepositoryResponse(
    val totalCount: Int,
    val githubRepoList: List<GithubRepoEntity>,
    val items: List<GithubRepoEntity>
)
