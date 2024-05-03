package com.app.athkar.di

import android.content.Context
import android.content.Context.MODE_PRIVATE
import com.app.athkar.core.util.DefaultSharedPreferencesManager
import com.app.athkar.core.util.LocationSelectionPreferences
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideSharedPreference(
        @ApplicationContext context: Context
    ): LocationSelectionPreferences {
        val prefs = context.getSharedPreferences(LocationSelectionPreferences.LOCATION_SELECTION_PREFERENCES_NAME, MODE_PRIVATE)
        return DefaultSharedPreferencesManager(prefs)
    }
}