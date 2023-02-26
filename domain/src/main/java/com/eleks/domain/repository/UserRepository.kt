package com.eleks.domain.repository

import com.eleks.domain.model.UserModel

interface UserRepository {

    fun getCurrentUser(): UserModel?

    fun signInSuccess()

    fun signOutSuccess()
}
