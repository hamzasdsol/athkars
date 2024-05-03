package com.app.athkar.export.util

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Picture
import android.media.MediaScannerConnection
import android.net.Uri
import android.os.Environment
import android.util.Log
import com.arthenica.ffmpegkit.FFmpegKit
import com.arthenica.ffmpegkit.ReturnCode
import kotlinx.coroutines.suspendCancellableCoroutine
import java.io.File
import kotlin.coroutines.resume


fun Picture.createBitmapFromPicture(): Bitmap {
    val bitmap = Bitmap.createBitmap(
        width, height, Bitmap.Config.ARGB_8888
    )

    val canvas = android.graphics.Canvas(bitmap)
    canvas.drawColor(android.graphics.Color.TRANSPARENT)
    canvas.drawPicture(this)
    return bitmap
}

suspend fun Bitmap.saveToDisk(context: Context): Uri {
    val file = File(
        Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
        "screenshot-${System.currentTimeMillis()}.png"
    )

    file.writeBitmap(this, Bitmap.CompressFormat.PNG, 100)

    return scanFilePath(context, file.path) ?: throw Exception("File could not be saved")
}

fun Bitmap.saveToCache(context: Context): String {
    val fileName = "screenshot-${System.currentTimeMillis()}.png"
    val cacheDir = context.cacheDir
    val file = File(cacheDir, fileName)

    file.writeBitmap(this, Bitmap.CompressFormat.PNG, 100)

    return file.path
}



private suspend fun scanFilePath(context: Context, filePath: String): Uri? {
    return suspendCancellableCoroutine { continuation ->
        MediaScannerConnection.scanFile(
            context, arrayOf(filePath), arrayOf("image/png")
        ) { _, scannedUri ->
            if (scannedUri == null) {
                continuation.cancel(Exception("File $filePath could not be scanned"))
            } else {
                continuation.resume(scannedUri)
            }
        }
    }
}

private fun File.writeBitmap(bitmap: Bitmap, format: Bitmap.CompressFormat, quality: Int) {
    outputStream().use { out ->
        bitmap.compress(format, quality, out)
        out.flush()
    }
}

suspend fun createVideoFromImageAndAudio(imagePath: String, audioPath: String): String {
    val file = File(
        Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
        "video-${System.currentTimeMillis()}.mp4"
    )
    val outputVideoPath = file.absolutePath
    val command = "-loop 1 -i $imagePath -i $audioPath -c:v libx264 -tune stillimage -c:a aac -b:a 192k -vf \"scale='iw-mod(iw,2)':'ih-mod(ih,2)',format=yuv420p\" -shortest $outputVideoPath"
    val rc = FFmpegKit.execute(command)
    return when {
        ReturnCode.isSuccess(rc.returnCode) -> {
            Log.i("Config.TAG", "Command execution completed successfully.")
            outputVideoPath
        }
        ReturnCode.isCancel(rc.returnCode) -> {
            Log.i("Config.TAG", "Command execution cancelled by user.")
            throw Exception("Command execution cancelled by user.")
        }
        else -> {
            Log.i("Config.TAG",String.format("Command failed with state %s and rc %s.%s", rc.state, rc.returnCode))
            rc.failStackTrace
            throw Exception("Command execution failed with rc=$rc")
        }
    }
}

