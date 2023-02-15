package com.eleks.domain.repository

import com.eleks.domain.model.UserModel

interface MotivationPhraseRepository {

    fun getCurrentUser(): UserModel?
}