package com.miassolutions.rollcall.di

import android.content.Context
import com.miassolutions.rollcall.data.datastore.UserPrefsManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DataStoreModule {

    @Provides
    @Singleton
    fun provideUserPrefsManager(@ApplicationContext context: Context): UserPrefsManager = UserPrefsManager(context)
}