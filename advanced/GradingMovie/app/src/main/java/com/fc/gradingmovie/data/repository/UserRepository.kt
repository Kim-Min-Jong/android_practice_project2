package com.fc.gradingmovie.data.repository

import com.fc.gradingmovie.domain.model.User

interface UserRepository {

    suspend fun getUser(): User?

    suspend fun saveUser(user: User)
}