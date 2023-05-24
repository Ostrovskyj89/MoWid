package com.eleks.domain.interactor

import com.eleks.domain.repository.UserRepository

class UserInteractor(private val userRepository: UserRepository) {

    fun getUserFlow() = userRepository.getUserFlow()

    fun signInSuccess() {
        userRepository.signInSuccess()
    }

    fun signOutSuccess() {
        userRepository.signOutSuccess()
    }
}
