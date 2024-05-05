package com.app.athkar.export.di

import android.content.Context
import com.app.athkar.export.audio_downloader.AudioDownloaderService
import com.app.athkar.export.audio_downloader.AudioDownloaderServiceImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AudioDownloaderModule {
    @Provides
    @Singleton
    fun provideAudioDownloaderService(@ApplicationContext context: Context): AudioDownloaderService {
        return AudioDownloaderServiceImpl(context)
    }
}