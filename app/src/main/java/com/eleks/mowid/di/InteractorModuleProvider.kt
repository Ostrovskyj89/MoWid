package com.eleks.mowid.di

import com.eleks.data.repository.MotivationPhraseRepositoryImpl
import com.eleks.data.repository.UserRepositoryImpl
import com.eleks.domain.intearactor.MotivationPhraseInteractor
import com.eleks.domain.intearactor.UserInteractor
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

    @Provides
    fun provideUserInteractor(
        userRepository: UserRepositoryImpl
    ): UserInteractor {
        return UserInteractor(userRepository)
    }
}