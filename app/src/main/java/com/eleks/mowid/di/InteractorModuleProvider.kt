package com.eleks.mowid.di

import com.eleks.data.repository.MotivationPhraseRepositoryImpl
import com.eleks.domain.intearactor.MotivationPhraseInteractor
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent

@Module
@InstallIn(ViewModelComponent::class)
object InteractorModuleProvider {

    @Provides
    fun provideMotivationPhraseInteractor(
        motivationPhraseRepository: MotivationPhraseRepositoryImpl
    ): MotivationPhraseInteractor {
        return MotivationPhraseInteractor(motivationPhraseRepository)
    }
}