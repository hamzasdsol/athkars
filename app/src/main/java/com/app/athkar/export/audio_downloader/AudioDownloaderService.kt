package com.app.athkar.export.audio_downloader

import java.io.File

interface AudioDownloaderService {
    suspend fun downloadAudio(url: String): File?
}