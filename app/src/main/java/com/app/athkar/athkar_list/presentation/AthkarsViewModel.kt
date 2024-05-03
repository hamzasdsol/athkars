package com.app.athkar.athkar_list.presentation

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class AthkarsViewModel @Inject constructor(): ViewModel() {

    private val _state = mutableStateOf(AthkarListState())
    val state: State<AthkarListState> = _state

    fun onEvent(event: AthkarsViewModelEvent) {
        when(event) {
            AthkarsViewModelEvent.Back -> {}
            AthkarsViewModelEvent.Export -> {}
            AthkarsViewModelEvent.Next -> {}
        }
    }
}