package com.app.athkar.export.util

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Paint
import android.graphics.Picture
import android.media.MediaCodec
import android.media.MediaCodecInfo
import android.media.MediaExtractor
import android.media.MediaFormat
import android.media.MediaMuxer
import android.media.MediaScannerConnection
import android.net.Uri
import android.opengl.GLES10
import android.os.Environment
import kotlinx.coroutines.suspendCancellableCoroutine
import java.io.File
import javax.microedition.khronos.opengles.GL10
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

fun createVideoFromBitmapAndAudio(bitmap: Bitmap, audioPath: String): String {
    val file = File(
        Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
        "video-${System.currentTimeMillis()}.mp4"
    )
    val outputVideoPath = file.absolutePath
    val videoEncoder = MediaCodec.createEncoderByType(MediaFormat.MIMETYPE_VIDEO_AVC)
    val videoFormat =
        MediaFormat.createVideoFormat(MediaFormat.MIMETYPE_VIDEO_AVC, 350, 555)
    videoFormat.setInteger(
        MediaFormat.KEY_COLOR_FORMAT, MediaCodecInfo.CodecCapabilities.COLOR_FormatSurface
    )
    videoFormat.setInteger(MediaFormat.KEY_BIT_RATE, 2000000)
    videoFormat.setInteger(MediaFormat.KEY_FRAME_RATE, 5)
    videoFormat.setInteger(MediaFormat.KEY_I_FRAME_INTERVAL, 5)

    try {
        videoEncoder.configure(videoFormat, null, null, MediaCodec.CONFIGURE_FLAG_ENCODE)
    } catch (e: Exception) {
        e.printStackTrace()
        throw e
    }

    val videoInputSurface = videoEncoder.createInputSurface()

    // Prepare MediaCodec for audio encoding
    val audioEncoder = MediaCodec.createEncoderByType(MediaFormat.MIMETYPE_AUDIO_AAC)
    // Configure audio encoder parameters
    val audioFormat = MediaFormat.createAudioFormat(MediaFormat.MIMETYPE_AUDIO_AAC, 44100, 2)
    audioFormat.setInteger(MediaFormat.KEY_BIT_RATE, 128000)
    audioFormat.setInteger(
        MediaFormat.KEY_AAC_PROFILE, MediaCodecInfo.CodecProfileLevel.AACObjectLC
    )
    audioEncoder.configure(audioFormat, null, null, MediaCodec.CONFIGURE_FLAG_ENCODE)

    // Start encoders
    videoEncoder.start()
    audioEncoder.start()

    // Prepare input data (bitmap and audio)
    val canvas = videoInputSurface.lockCanvas(null)
    canvas.drawBitmap(bitmap, 0f, 0f, Paint())
    videoInputSurface.unlockCanvasAndPost(canvas)

    val audioExtractor = MediaExtractor()
    audioExtractor.setDataSource(audioPath)
    val audioTrackIndex = selectTrack(audioExtractor)
    audioExtractor.selectTrack(audioTrackIndex)

    // Encode video frames
    val videoBufferInfo = MediaCodec.BufferInfo()
    var videoInputDone = false
    while (!videoInputDone) {
        val inputBufferIndex = videoEncoder.dequeueInputBuffer(-1)
        if (inputBufferIndex >= 0) {
            val inputBuffer = videoEncoder.getInputBuffer(inputBufferIndex)
            inputBuffer?.clear()
            val presentationTimeUs = 0L // Use a dummy timestamp for now
            videoEncoder.queueInputBuffer(inputBufferIndex, 0, 0, presentationTimeUs, 0)
            videoInputDone = true
        }
    }

    // Encode audio samples
    val audioBufferInfo = MediaCodec.BufferInfo()
    val audioEncoderInputBuffers = audioEncoder.inputBuffers
    var audioInputDone = false
    while (!audioInputDone) {
        val inputBufferIndex = audioEncoder.dequeueInputBuffer(-1)
        if (inputBufferIndex >= 0) {
            val inputBuffer = audioEncoderInputBuffers[inputBufferIndex]
            val sampleSize = audioExtractor.readSampleData(inputBuffer, 0)
            if (sampleSize < 0) {
                audioEncoder.queueInputBuffer(
                    inputBufferIndex, 0, 0, 0, MediaCodec.BUFFER_FLAG_END_OF_STREAM
                )
                audioInputDone = true
            } else {
                val presentationTimeUs = audioExtractor.sampleTime
                audioEncoder.queueInputBuffer(
                    inputBufferIndex, 0, sampleSize, presentationTimeUs, 0
                )
                audioExtractor.advance()
            }
        }
    }

    // Mux video and audio streams into a single container format (MP4)
    val mediaMuxer = MediaMuxer(outputVideoPath, MediaMuxer.OutputFormat.MUXER_OUTPUT_MPEG_4)
    val audioTrackIndexMuxer = mediaMuxer.addTrack(audioFormat)
    val videoTrackIndexMuxer = mediaMuxer.addTrack(videoFormat)
    mediaMuxer.start()

    // Copy video data
    val videoDecoderOutputBuffers = videoEncoder.outputBuffers
    while (true) {
        val encoderStatus = videoEncoder.dequeueOutputBuffer(videoBufferInfo, 10000)
        if (encoderStatus == MediaCodec.INFO_TRY_AGAIN_LATER) {
            // no output available yet
        } else if (encoderStatus == MediaCodec.INFO_OUTPUT_FORMAT_CHANGED) {
            // not expected for an encoder
        } else if (encoderStatus == MediaCodec.INFO_OUTPUT_BUFFERS_CHANGED) {
            // not expected for an encoder
        } else if (encoderStatus < 0) {
            // ignore failures
        } else {
            val encodedData = videoDecoderOutputBuffers[encoderStatus]
            encodedData.position(videoBufferInfo.offset)
            encodedData.limit(videoBufferInfo.offset + videoBufferInfo.size)
            mediaMuxer.writeSampleData(videoTrackIndexMuxer, encodedData, videoBufferInfo)
            videoEncoder.releaseOutputBuffer(encoderStatus, false)
            if ((videoBufferInfo.flags and MediaCodec.BUFFER_FLAG_END_OF_STREAM) != 0) {
                break
            }
        }
    }

    // Copy audio data
    val audioDecoderOutputBuffers = audioEncoder.outputBuffers
    while (true) {
        val encoderStatus = audioEncoder.dequeueOutputBuffer(audioBufferInfo, 10000)
        if (encoderStatus == MediaCodec.INFO_TRY_AGAIN_LATER) {
            // no output available yet
        } else if (encoderStatus == MediaCodec.INFO_OUTPUT_FORMAT_CHANGED) {
            // not expected for an encoder
        } else if (encoderStatus == MediaCodec.INFO_OUTPUT_BUFFERS_CHANGED) {
            // not expected for an encoder
        } else if (encoderStatus < 0) {
            // ignore failures
        } else {
            val encodedData = audioDecoderOutputBuffers[encoderStatus]
            encodedData.position(audioBufferInfo.offset)
            encodedData.limit(audioBufferInfo.offset + audioBufferInfo.size)
            mediaMuxer.writeSampleData(audioTrackIndexMuxer, encodedData, audioBufferInfo)
            audioEncoder.releaseOutputBuffer(encoderStatus, false)
            if ((audioBufferInfo.flags and MediaCodec.BUFFER_FLAG_END_OF_STREAM) != 0) {
                break
            }
        }
    }

    // Release resources
    mediaMuxer.stop()
    mediaMuxer.release()
    audioExtractor.release()
    videoEncoder.stop()
    videoEncoder.release()
    audioEncoder.stop()
    audioEncoder.release()

    return outputVideoPath
}

private fun selectTrack(extractor: MediaExtractor): Int {
    for (i in 0 until extractor.trackCount) {
        val format = extractor.getTrackFormat(i)
        val mime = format.getString(MediaFormat.KEY_MIME)
        if (mime?.startsWith("audio/") == true) {
            return i
        }
    }
    return -1
}

