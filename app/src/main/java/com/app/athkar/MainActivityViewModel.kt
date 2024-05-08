package com.app.athkar

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.app.athkar.data.model.network.Athkar
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class MainActivityViewModel @Inject constructor() : ViewModel() {

    private val _state = mutableStateOf(MainActivityState(athkar = Athkar("","")))
    val state: State<MainActivityState> = _state

    fun onEvent(event: MainActivityModelEvent) {
        when (event) {
            is MainActivityModelEvent.SaveAthkar -> {
                _state.value = state.value.copy(athkar = event.athkar)
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
    }

}