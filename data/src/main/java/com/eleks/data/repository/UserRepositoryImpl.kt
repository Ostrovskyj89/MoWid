package com.eleks.data.repository

import com.eleks.data.firebase.source.FirebaseDataSource
import com.eleks.data.mapper.toUserModel
import com.eleks.domain.repository.UserRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserRepositoryImpl @Inject constructor(
    private val firebaseDataSource: FirebaseDataSource
): UserRepository  {

    override fun getCurrentUser() = firebaseDataSource.currentUser?.toUserModel()

    override fun signInSuccess() {
        firebaseDataSource.signInSuccess()
    }

    override fun signOutSuccess() {
        firebaseDataSource.signOutSuccess()
    }
}
