package com.app.athkar.athkar_list

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel

class AthkarsViewModel: ViewModel() {

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