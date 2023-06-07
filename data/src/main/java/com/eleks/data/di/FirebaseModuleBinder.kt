package com.eleks.data.di

import com.eleks.data.firebase.source.FirebaseDataSource
import com.eleks.data.firebase.source.impl.FirebaseDataSourceImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class FirebaseModuleBinder {

    @Binds
    abstract fun bindFirebaseDataSource(
        firebaseDataSourceImpl: FirebaseDataSourceImpl
    ): FirebaseDataSource
}
