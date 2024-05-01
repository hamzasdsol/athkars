package com.app.athkar.export.audio_downloader

import android.content.Context
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.BufferedInputStream
import java.io.File
import java.io.FileOutputStream
import java.net.URL
import javax.inject.Inject

class AudioDownloaderServiceImpl @Inject constructor(
    private val context: Context
) : AudioDownloaderService {
    override suspend fun downloadAudio(url: String): File? {
        return withContext(Dispatchers.IO) {
            try {
                val fileName = getFileName(url)
                val cacheDir = context.cacheDir
                val file = File(cacheDir, fileName)

                val urlConnection = URL(url).openConnection()
                urlConnection.connect()

                val inputStream = BufferedInputStream(urlConnection.getInputStream())

                val outputStream = FileOutputStream(file)

                val buffer = ByteArray(1024)
                var bytesRead: Int
                while (inputStream.read(buffer).also { bytesRead = it } != -1) {
                    outputStream.write(buffer, 0, bytesRead)
                }

                outputStream.close()
                inputStream.close()

                file
            } catch (e: Exception) {
                e.printStackTrace()
                null
            }
        }
    }

    private fun getFileName(url: String): String {
        return url.substring(url.lastIndexOf('/') + 1)
    }
}