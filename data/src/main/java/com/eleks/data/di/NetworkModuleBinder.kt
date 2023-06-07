package com.eleks.data.di

import com.eleks.data.network.NetworkDataSource
import com.eleks.data.network.impl.NetworkDataSourceImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent


@Module
@InstallIn(SingletonComponent::class)
abstract class NetworkModuleBinder {

    @Binds
    abstract fun bindNetworkDataSource(
        networkDataSourceImpl: NetworkDataSourceImpl
    ): NetworkDataSource
}
