package com.app.athkar.shared.di

import com.app.athkar.core.network.remote.AppDataSource
import com.app.athkar.shared.data.DefaultPrayerRepository
import com.app.athkar.shared.domain.PrayersRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object SharedModule {

    @Provides
    @Singleton
    fun providePrayerRepository(dataSource: AppDataSource): PrayersRepository {
        return DefaultPrayerRepository(dataSource)
    }
}