package com.app.athkar.athkar_list.di

import com.app.athkar.athkar_list.data.DefaultAthkarsRepository
import com.app.athkar.athkar_list.domain.AthkarsRepository
import com.app.athkar.core.network.remote.AppDataSource
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AthkarsModule {

    @Provides
    @Singleton
    fun provideAthkarsRepository(dataSource: AppDataSource): AthkarsRepository {
        return DefaultAthkarsRepository(dataSource)
    }
}
