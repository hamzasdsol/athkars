package com.app.athkar.home.di

import com.app.athkar.core.network.remote.AppDataSource
import com.app.athkar.home.data.DefaultHomeRepository
import com.app.athkar.home.domain.HomeRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object HomeModule {

    @Provides
    @Singleton
    fun provideHomeRepository(dataSource: AppDataSource): HomeRepository {
        return DefaultHomeRepository(dataSource)
    }
}