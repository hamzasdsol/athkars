package com.app.athkar.edit_prayer.presentation

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.athkar.R
import com.app.athkar.core.util.LocationSelectionPreferences
import com.app.athkar.core.util.alarm.PrayersAlarmPreferences
import com.app.athkar.data.model.CurrentPrayerDetails
import com.app.athkar.data.model.network.GetPrayerTimesResponse
import com.app.athkar.data.model.toPrayersModel
import com.app.athkar.di.ResourceProvider
import com.app.athkar.domain.Result
import com.app.athkar.domain.repository.AppRepository
import com.app.athkar.home.presentation.HomeUIEvent
import dagger.hilt.android.lifecycle.HiltViewModel
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
) : ViewModel() {
    private val _state = mutableStateOf(EditPrayerState())
    val state: State<EditPrayerState> = _state

    private val _uiEvent = MutableSharedFlow<EditPrayerUIEvent>()
    val uiEvent = _uiEvent.asSharedFlow()

    init {
        val data = prayersAlarmPreferences.prayerAlarmState()
        val prayers = mutableListOf<Prayer>()
        data.forEach {
            prayers.add(
                Prayer(
                    it.first,
                    iqamaTime = "6:35",
                    prayerTime = "6:14",
                    isAlarmEnabled = it.second
                )
            )
        }

        println("PrayersAlarm :: $data")

        _state.value = state.value.copy(
            prayers = prayers
        )

        println("PrayersAlarm :: prayers state $prayers")

        getPrayerTimes()

    }

    private fun getPrayerTimes() {
        viewModelScope.launch {
            val currentLocation = locationSelectionPreferences.currentLocation?.file ?: "amman"
            when (val prayerTimesResponse = appRepository.getPrayerTimes(currentLocation)) {
                is Result.Success -> {
                    val response: GetPrayerTimesResponse = prayerTimesResponse.data
                    val prayerTimes = response.prayerTimes
                    val currentDateTime = Date()
                    val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                    val date: String = dateFormat.format(currentDateTime)
                    val currentPrayerTimesList: List<String> = prayerTimes[date] ?: emptyList()
                    if (currentPrayerTimesList.isNotEmpty()) {
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
            }
        }
    }
}