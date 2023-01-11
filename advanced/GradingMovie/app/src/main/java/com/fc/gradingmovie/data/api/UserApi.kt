package com.fc.gradingmovie.data.api

import com.fc.gradingmovie.domain.model.User

interface UserApi {
    suspend fun saveUser(user: User): User
}