package com.app.athkar.export.presentation

import android.content.Context
import android.graphics.Picture

sealed class ExportViewModelEvent {
    data object Play : ExportViewModelEvent()
    data object Pause : ExportViewModelEvent()
    data class Download(val downloadUrl: String) : ExportViewModelEvent()

    data class ExportImage(val context:Context, val picture: Picture) : ExportViewModelEvent()
    data class ExportVideo(val context:Context, val picture: Picture, val audioUrl : String) : ExportViewModelEvent()
}