package com.eleks.data.di

import com.eleks.data.repository.MotivationPhraseRepositoryImpl
import com.eleks.domain.repository.MotivationPhraseRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModuleBinder {

    @Binds
    abstract fun bindTestRepository(
        motivationPhraseRepository: MotivationPhraseRepositoryImpl
    ): MotivationPhraseRepository
}