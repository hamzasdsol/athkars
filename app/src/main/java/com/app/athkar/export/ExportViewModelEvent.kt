package com.app.athkar.export

sealed class ExportViewModelEvent {
    data object Play: ExportViewModelEvent()
    data object Pause: ExportViewModelEvent()
}