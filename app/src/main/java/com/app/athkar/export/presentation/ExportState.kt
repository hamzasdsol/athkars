package com.app.athkar.export.presentation

import androidx.media3.exoplayer.ExoPlayer

data class ExportState(
    val exoPlayer: ExoPlayer? = null,
    val isPlaying: Boolean = false,
    val isLoading: Boolean = false,
)
