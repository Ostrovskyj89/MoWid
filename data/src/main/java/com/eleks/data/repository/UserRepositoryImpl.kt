package com.eleks.data.repository

import com.eleks.data.firebase.source.FirebaseDataSource
import com.eleks.data.mapper.toDomain
import com.eleks.data.model.Status
import com.eleks.domain.model.UserModel
import com.eleks.domain.repository.UserRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserRepositoryImpl @Inject constructor(
    private val firebaseDataSource: FirebaseDataSource
) : UserRepository {

    override fun getUserFlow(): Flow<UserModel?> {
        firebaseDataSource.subscribeUser()
        return firebaseDataSource.userFlow.map {
            when (it.status) {
                Status.SUCCESS -> it.data?.toDomain()
                Status.ERROR -> throw it.error ?: Exception("Unknown exception")
            }
        }
    }

    override fun signInSuccess() {
        firebaseDataSource.signInSuccess()
    }

    override fun signOutSuccess() {
        firebaseDataSource.signOutSuccess()
    }
}
