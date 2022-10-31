package com.fc.githubrepository.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.fc.githubrepository.data.entity.GithubRepoEntity

@Dao
interface SearchHistoryDao {

    @Insert
    suspend fun insert(repo: GithubRepoEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(repoList: List<GithubRepoEntity>)

    @Query("SELECT * FROM githubrepository")
    suspend fun getHistory(): List<GithubRepoEntity>

    @Query("SELECT * FROM githubrepository WHERE fullName = :fullName")
    suspend fun getRepository(fullName: String): GithubRepoEntity?

    @Query("DELETE FROM githubrepository WHERE fullName = :fullName")
    suspend fun remove(fullName: String)

    @Query("DELETE FROM githubrepository")
    suspend fun clearAll()

}