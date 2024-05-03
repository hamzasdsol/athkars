package com.app.athkar.export.presentation

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class ExportViewModel @Inject constructor(): ViewModel() {

    private val _state = mutableStateOf(ExportState())
    val state: State<ExportState> = _state

    fun onEvent(event: ExportViewModelEvent) {
        when(event) {
            ExportViewModelEvent.Pause -> {}
            ExportViewModelEvent.Play -> {}
        }
    }
}