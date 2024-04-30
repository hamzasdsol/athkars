package com.app.athkar.export.presentation

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel

class ExportViewModel: ViewModel() {

    private val _state = mutableStateOf(ExportState())
    val state: State<ExportState> = _state

    fun onEvent(event: ExportViewModelEvent) {
        when(event) {
            ExportViewModelEvent.Pause -> {}
            ExportViewModelEvent.Play -> {}
        }
    }
}