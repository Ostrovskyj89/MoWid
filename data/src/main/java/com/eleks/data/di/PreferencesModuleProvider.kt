package com.eleks.data.di

import android.content.Context
import android.content.SharedPreferences
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.serialization.StringFormat
import kotlinx.serialization.json.Json
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object PreferencesModuleProvider {

    @Provides
    @Singleton
    fun provideSharedPreferences(
        @ApplicationContext context: Context
    ): SharedPreferences {
        return context.getSharedPreferences("TestPrefs", Context.MODE_PRIVATE)
    }

    @Provides
    @Singleton
    fun provideJson(): StringFormat {
        return Json{
            ignoreUnknownKeys = true
        }
    }
}