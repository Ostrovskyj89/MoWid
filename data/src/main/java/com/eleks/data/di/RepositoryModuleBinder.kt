package com.eleks.data.di

import com.eleks.data.repository.MotivationPhraseRepositoryImpl
import com.eleks.domain.repository.MotivationPhraseRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.serialization.StringFormat
import kotlinx.serialization.json.Json

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModuleBinder {

    @Binds
    abstract fun bindTestRepository(
        motivationPhraseRepository: MotivationPhraseRepositoryImpl
    ): MotivationPhraseRepository
}