package com.eleks.domain.repository

import com.eleks.domain.model.UserModel
import kotlinx.coroutines.flow.Flow

interface UserRepository {

    fun getUserFlow() : Flow<UserModel?>

    fun signInSuccess()

    fun signOutSuccess()
}
