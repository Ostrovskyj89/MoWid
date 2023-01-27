package com.eleks.data.repository

import com.eleks.data.firebase.source.FirebaseDataSource
import com.eleks.domain.repository.MotivationPhraseRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MotivationPhraseRepositoryImpl @Inject constructor(
    private val firebaseDataSource: FirebaseDataSource
) : MotivationPhraseRepository {
}