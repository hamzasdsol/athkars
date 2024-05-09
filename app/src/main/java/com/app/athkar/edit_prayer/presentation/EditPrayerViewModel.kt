package com.app.athkar.edit_prayer.presentation

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.athkar.R
import com.app.athkar.core.util.LocationSelectionPreferences
import com.app.athkar.core.util.alarm.PrayersAlarmPreferences
import com.app.athkar.core.util.toPrayerDate
import com.app.athkar.data.model.network.GetPrayerTimesResponse
import com.app.athkar.di.ResourceProvider
import com.app.athkar.domain.Result
import com.app.athkar.domain.repository.AppRepository
import com.app.athkar.edit_prayer.broadcast_receiver.AlarmReceiver
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

                    when (event.key) {
                        PrayerName.FAJR -> {
                           setUpAlarms(event.key, event.value, 0)
                        }

                        PrayerName.DUHUR -> {
                            setUpAlarms(event.key, event.value, 2)
                        }

                        PrayerName.ASR -> {
                            setUpAlarms(event.key, event.value, 3)
                        }

                        PrayerName.MAGHRIB -> {
                            setUpAlarms(event.key, event.value, 4)
                        }

                        PrayerName.ISHA -> {
                            setUpAlarms(event.key, event.value, 5)
                        }
                    }
                }
            }
        }
    }

    private fun setUpAlarms(prayerName: PrayerName, isEnabled : Boolean, prayerIndex: Int){
        val prayers = mutableListOf<Date>()
        prayerTimesMap.forEach { (key, value) ->
            prayers.add("$key ${value[prayerIndex]}".toPrayerDate())
        }
        val currentDate = Date()
        prayers.removeAll { it.before(currentDate) }
        if (isEnabled)
            scheduleNotifications(prayers, prayerName)
        else
            cancelNotification(generateRequestCodeForPrayer(prayerName))
    }

    private fun scheduleNotifications(prayerList: MutableList<Date>, prayerName: PrayerName) {
        prayerList.forEach { date ->
            val requestCode = generateRequestCodeForPrayer(prayerName)
            scheduleNotification(date.time, prayerName.name, requestCode)
        }
    }

    private fun cancelNotification(requestCode: Int) {
        val alarmManager = resourceProvider.getApplicationContext()
            .getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(resourceProvider.getApplicationContext(), AlarmReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(
            resourceProvider.getApplicationContext(),
            requestCode,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        if (pendingIntent == null) {
            Log.e("CancelNotification", "PendingIntent is null")
            return
        }
        alarmManager.cancel(pendingIntent)
        pendingIntent.cancel()
    }

    private fun isAlarmScheduled(context: Context, requestCode: Int): Boolean {
        val intent = Intent(context, AlarmReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            requestCode,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        return pendingIntent != null
    }

    private fun scheduleNotification(prayerTime: Long, prayerName: String, requestCode: Int) {
        try {
            val context = resourceProvider.getApplicationContext()
            val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
            val intent = Intent(context, AlarmReceiver::class.java)
            intent.putExtra("prayer_name", prayerName)
            val pendingIntent = PendingIntent.getBroadcast(
                context,
                requestCode,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )

            if (pendingIntent != null) {
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.S) {
                    if (alarmManager.canScheduleExactAlarms())
                        alarmManager.setExact(AlarmManager.RTC_WAKEUP, prayerTime, pendingIntent)
                } else {
                    alarmManager.setExact(AlarmManager.RTC_WAKEUP, prayerTime, pendingIntent)
                }
            } else {
                Log.e("ScheduleNotification", "PendingIntent is null")
            }
        } catch (e: Exception) {
            Log.e("ScheduleNotification", "Error scheduling notification", e)
        }
    }

}