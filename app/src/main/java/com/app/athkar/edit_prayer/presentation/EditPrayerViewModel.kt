package com.app.athkar.edit_prayer.presentation

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.app.athkar.core.util.alarm.PrayersAlarmPreferences
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class EditPrayerViewModel @Inject constructor(
    private val prayersAlarmPreferences: PrayersAlarmPreferences
) : ViewModel() {
    private val _state = mutableStateOf(EditPrayerState())
    val state: State<EditPrayerState> = _state

    init {
        val data = prayersAlarmPreferences.prayerAlarmState()
        val prayers = mutableListOf<Prayer>()
        data.forEach {
            prayers.add(Prayer(it.first, iqamaTime = "6:35", prayerTime = "6:14", isAlarmEnabled =  it.second))
        }

        println("PrayersAlarm :: $data")

        _state.value = state.value.copy(
            prayers = prayers
        )

        println("PrayersAlarm :: prayers state $prayers")
    }

    fun onEvent(event: EditPrayerViewModelEvent) {
        when (event) {
            EditPrayerViewModelEvent.Save -> {}
            is EditPrayerViewModelEvent.SetPrayerAlarmPreference -> {
                prayersAlarmPreferences.savePrayerNotification(event.key.name, event.value)
                _state.value = state.value.copy(
                    prayers = state.value.prayers.toMutableList().apply {
                        this[event.key.ordinal] = this[event.key.ordinal].copy(isAlarmEnabled = event.value)
                    }
                )
            }
        }
    }
}