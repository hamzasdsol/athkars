package com.app.athkar.di

import android.content.Context
import android.content.Context.MODE_PRIVATE
import com.app.athkar.core.util.DefaultSharedPreferencesManager
import com.app.athkar.core.util.LocationSelectionPreferences
import com.app.athkar.core.util.alarm.DefaultPrayersAlarmPreferences
import com.app.athkar.core.util.alarm.PrayersAlarmPreferences
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
        val prefs = context.getSharedPreferences(LocationSelectionPreferences.PREFERENCES_NAME, MODE_PRIVATE)
        return DefaultSharedPreferencesManager(prefs)
    }

    @Provides
    @Singleton
    fun providePrayersAlarmPreferences(
        @ApplicationContext context: Context
    ): PrayersAlarmPreferences {
        val prefs = context.getSharedPreferences(PrayersAlarmPreferences.PREFERENCES_NAME, MODE_PRIVATE)
        return DefaultPrayersAlarmPreferences(prefs)
    }
}