package com.fc.githubrepository.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.fc.githubrepository.data.dao.SearchHistoryDao
import com.fc.githubrepository.data.entity.GithubRepoEntity

@Database(entities = [GithubRepoEntity::class], version = 1)
abstract class SimpleGithubDatabase: RoomDatabase() {
    abstract fun repositoryDao(): SearchHistoryDao
}