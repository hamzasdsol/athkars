package com.app.athkar.di

import com.app.athkar.data.remote.AppDataSource
import com.app.athkar.data.repository.AppRepositoryImpl
import com.app.athkar.domain.repository.AppRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppRepositoryModule {

    /**
     * Provides the app repository.
     */
    @Provides
    @Singleton
    fun provideUserRepository(dataSource: AppDataSource): AppRepository {
        return AppRepositoryImpl(dataSource)
    }
}
