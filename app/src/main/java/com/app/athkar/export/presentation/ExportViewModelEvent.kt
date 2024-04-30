package com.app.athkar.export.presentation

sealed class ExportViewModelEvent {
    data object Play: ExportViewModelEvent()
    data object Pause: ExportViewModelEvent()
}