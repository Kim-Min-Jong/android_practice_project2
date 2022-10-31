package com.fc.githubrepository.data.entity

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "GithubRepository")
data class GithubRepoEntity(
    val name: String,
    @PrimaryKey val fullName: String,
    // nested field(객체 어트리뷰트)
    @Embedded val owner: GithubOwner,
    val description: String?,
    val language: String?,
    val updatedAt: String,
    val stargazersCount: Int
)