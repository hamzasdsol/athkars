package com.app.athkar.edit_prayer.presentation

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.athkar.R
import com.app.athkar.core.util.LocationSelectionPreferences
import com.app.athkar.core.util.alarm.AlarmItem
import com.app.athkar.core.util.alarm.AlarmScheduler
import com.app.athkar.core.util.alarm.PrayersAlarmPreferences
import com.app.athkar.core.util.toPrayerDate
import com.app.athkar.data.model.network.GetPrayerTimesResponse
import com.app.athkar.di.ResourceProvider
import com.app.athkar.domain.Result
import com.app.athkar.domain.repository.AppRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.io.IOException
import java.net.UnknownHostException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.concurrent.TimeoutException
import javax.inject.Inject

@HiltViewModel
class EditPrayerViewModel @Inject constructor(
    private val prayersAlarmPreferences: PrayersAlarmPreferences,
    private val appRepository: AppRepository,
    private val resourceProvider: ResourceProvider,
    private val locationSelectionPreferences: LocationSelectionPreferences,
    private val alarmScheduler: AlarmScheduler
) : ViewModel() {
    private val _state = mutableStateOf(EditPrayerState())
    val state: State<EditPrayerState> = _state

    private val _uiEvent = MutableSharedFlow<EditPrayerUIEvent>()
    val uiEvent = _uiEvent.asSharedFlow()

    private val prayerTimesMap = mutableMapOf<String, List<String>>()

    init {
        val data = prayersAlarmPreferences.prayerAlarmState()
        val prayers = mutableListOf<Prayer>()
        data.forEach {
            prayers.add(
                Prayer(
                    it.first,
                    iqamaTime = "",
                    prayerTime = "",
                    isAlarmEnabled = it.second
                )
            )
        }

        _state.value = state.value.copy(
            prayers = prayers
        )

        getPrayerTimes()

    }

    private fun getPrayerTimes() {
        viewModelScope.launch(Dispatchers.IO) {
            val currentLocation = locationSelectionPreferences.currentLocation?.file ?: "amman"
            when (val prayerTimesResponse = appRepository.getPrayerTimes(currentLocation)) {
                is Result.Success -> {
                    val response: GetPrayerTimesResponse = prayerTimesResponse.data
                    val prayerTimes = response.prayerTimes
                    prayerTimesMap.clear()
                    prayerTimesMap.putAll(prayerTimes)
                    val currentDateTime = Date()
                    val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                    val date: String = dateFormat.format(currentDateTime)
                    val currentPrayerTimesList: MutableList<String> =
                        (prayerTimes[date] ?: emptyList()).toMutableList()
                    if (currentPrayerTimesList.isNotEmpty() && currentPrayerTimesList.size > 1) {
                        currentPrayerTimesList.removeAt(1)
                        val data = prayersAlarmPreferences.prayerAlarmState()
                        val prayers = mutableListOf<Prayer>()
                        data.forEachIndexed { index, it ->
                            prayers.add(
                                Prayer(
                                    it.first,
                                    iqamaTime = currentPrayerTimesList[index],
                                    prayerTime = currentPrayerTimesList[index],
                                    isAlarmEnabled = it.second
                                )
                            )
                        }

                        _state.value = state.value.copy(
                            prayers = prayers
                        )
                    }
                }

                is Result.Failure -> {
                    handleError(prayerTimesResponse.exception)
                }
            }
        }
    }

    private fun handleError(exception: Exception) {
        viewModelScope.launch {
            when (exception) {
                is UnknownHostException -> {
                    _uiEvent.emit(EditPrayerUIEvent.ShowMessage(resourceProvider.getString(R.string.please_check_your_internet_connection)))
                }

                is IOException -> {
                    _uiEvent.emit(EditPrayerUIEvent.ShowMessage(resourceProvider.getString(R.string.slower_internet_connection)))
                }

                is TimeoutException -> {
                    _uiEvent.emit(EditPrayerUIEvent.ShowMessage(resourceProvider.getString(R.string.timeout_error)))
                }

                is HttpException -> {
                    if (exception.code() == 401) {
                        _uiEvent.emit(EditPrayerUIEvent.ShowMessage(resourceProvider.getString(R.string.invalid_email_or_password)))
                    } else if (exception.code() == 500) {
                        _uiEvent.emit(EditPrayerUIEvent.ShowMessage(resourceProvider.getString(R.string.something_went_wrong)))
                    } else {
                        _uiEvent.emit(EditPrayerUIEvent.ShowMessage(resourceProvider.getString(R.string.unknown_error_occurred)))
                    }
                }

                is IllegalArgumentException -> {
                    _uiEvent.emit(
                        EditPrayerUIEvent.ShowMessage(
                            exception.message ?: exception.toString()
                        )
                    )
                }

                else -> {
                    _uiEvent.emit(
                        EditPrayerUIEvent.ShowMessage(
                            exception.message ?: exception.toString()
                        )
                    )
                }
            }
        }
    }

    fun onEvent(event: EditPrayerViewModelEvent) {
        when (event) {
            EditPrayerViewModelEvent.Save -> {}
            is EditPrayerViewModelEvent.SetPrayerAlarmPreference -> {
                prayersAlarmPreferences.savePrayerNotification(event.key.name, event.value)
                _state.value = state.value.copy(
                    prayers = state.value.prayers.toMutableList().apply {
                        this[event.key.ordinal] =
                            this[event.key.ordinal].copy(isAlarmEnabled = event.value)
                    }
                )
                viewModelScope.launch(Dispatchers.IO) {
                    val prayerIndex = if (event.key == PrayerName.FAJR) 0 else event.key.ordinal + 1
                    setUpAlarms(event.key, event.value, prayerIndex)
                }
            }
        }
    }

    private fun setUpAlarms(prayerName: PrayerName, isEnabled: Boolean, prayerIndex: Int) {
        val prayers = mutableListOf<Date>()
        prayerTimesMap.forEach { (key, value) ->
            prayers.add("$key ${value[prayerIndex]}".toPrayerDate())
        }
        val currentDate = Date()
        prayers.removeAll { it.before(currentDate) }
        if (isEnabled)
            scheduleNotifications(prayers, prayerName)
        else
            cancelNotifications(prayers, prayerName)
    }

    private fun cancelNotifications(prayerList: MutableList<Date>, prayerName: PrayerName) {
        prayerList.forEach { date ->
            val alarmItem = AlarmItem(
                date,
                prayerName.name
            )
            alarmScheduler.cancel(alarmItem)
        }
    }

    private fun scheduleNotifications(prayerList: MutableList<Date>, prayerName: PrayerName) {
        prayerList.forEach { date ->
            val alarmItem = AlarmItem(
                date,
                prayerName.name
            )
            alarmScheduler.schedule(alarmItem) {
                viewModelScope.launch(Dispatchers.Main) {
                    _uiEvent.emit(EditPrayerUIEvent.RequestAlarmPermission)
                }
            }
        }
    }
}