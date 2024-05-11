package com.app.athkar.home.presentation

import android.annotation.SuppressLint
import android.location.Location
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.athkar.R
import com.app.athkar.core.network.NetworkResult
import com.app.athkar.core.util.LocationSelectionPreferences
import com.app.athkar.core.util.alarm.AlarmItem
import com.app.athkar.core.util.alarm.AlarmScheduler
import com.app.athkar.core.util.alarm.PrayersAlarmPreferences
import com.app.athkar.core.util.toPrayerDate
import com.app.athkar.home.data.CurrentPrayerDetails
import com.app.athkar.home.data.City
import com.app.athkar.home.data.GetCitiesResponse
import com.app.athkar.shared.data.GetPrayerTimesResponse
import com.app.athkar.shared.data.toPrayersModel
import com.app.athkar.core.di.ResourceProvider
import com.app.athkar.edit_prayer.presentation.PrayerName
import com.app.athkar.home.domain.HomeRepository
import com.app.athkar.shared.domain.PrayersRepository
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.model.LatLng
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
import kotlin.math.pow
import kotlin.math.sqrt

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val locationSelectionPreferences: LocationSelectionPreferences,
    private val resourceProvider: ResourceProvider,
    private val homeRepository: HomeRepository,
    private val prayersRepository: PrayersRepository,
    private val alarmScheduler: AlarmScheduler,
    private val prayersAlarmPreferences: PrayersAlarmPreferences
) : ViewModel() {

    private val _state = mutableStateOf(HomeState())
    val state: State<HomeState> = _state

    private val _uiEvent = MutableSharedFlow<HomeUIEvent>()
    val uiEvent = _uiEvent.asSharedFlow()

    private val cities: MutableList<City> = mutableListOf()

    private lateinit var fusedLocationClient: FusedLocationProviderClient

    private val prayerTimesMap = mutableMapOf<String, List<String>>()

    init {
        _state.value =
            state.value.copy(showDialog = !locationSelectionPreferences.isLocationSelected())
        if (locationSelectionPreferences.isLocationSelected()) {
            val location = locationSelectionPreferences.currentLocation
            if (location != null) {
                _state.value = state.value.copy(location = location.name_en)
                getPrayerTimes(location.file)
            }
        } else {
            getCities()
        }
    }

    private fun getPrayerTimes(currentLocation: String) {
        viewModelScope.launch(Dispatchers.IO) {
            when (val prayerTimesResponse = prayersRepository.getPrayerTimes(currentLocation)) {
                is NetworkResult.Success -> {
                    val response: GetPrayerTimesResponse = prayerTimesResponse.data
                    val prayerTimes = response.prayerTimes
                    prayerTimesMap.clear()
                    prayerTimesMap.putAll(prayerTimes)
                    val currentDateTime = Date()
                    val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                    val date: String = dateFormat.format(currentDateTime)
                    val currentPrayerTimesList: List<String> = prayerTimes[date] ?: emptyList()
                    if (currentPrayerTimesList.isNotEmpty()) {
                        val currentPrayerTimes = currentPrayerTimesList.toPrayersModel(date)
                        val currentPrayer = currentPrayerTimes.getCurrentPrayer()
                        _state.value = state.value.copy(
                            currentPrayer = CurrentPrayerDetails(
                                name = currentPrayer.first.first,
                                time = currentPrayer.first.second,
                                nextPrayer = currentPrayer.second.first,
                                nextPrayerTime = currentPrayer.second.second
                            )
                        )
                    }
                }

                is NetworkResult.Failure -> {
                    handleError(prayerTimesResponse.exception)
                }
            }
        }
    }

    private fun getCities() {
        viewModelScope.launch(Dispatchers.IO) {
            when (val citiesResponse = homeRepository.getCities()) {
                is NetworkResult.Success -> {
                    val response: GetCitiesResponse = citiesResponse.data
                    val cities: List<City> = response.cities
                    this@HomeViewModel.cities.apply {
                        clear()
                        addAll(cities)
                    }
                    _state.value = state.value.copy(cities = cities)
                }

                is NetworkResult.Failure -> {
                    handleError(citiesResponse.exception)
                }
            }
        }
    }

    private fun emitEvent(event: HomeUIEvent) {
        viewModelScope.launch {
            _uiEvent.emit(event)
        }
    }

    @SuppressLint("MissingPermission")
    fun onEvent(event: HomeViewModelEvent) {
        when (event) {
            is HomeViewModelEvent.SelectAutoLocation -> {
                viewModelScope.launch(Dispatchers.IO) {
                    fusedLocationClient =
                        LocationServices.getFusedLocationProviderClient(resourceProvider.getApplicationContext())
                    fusedLocationClient.lastLocation
                        .addOnSuccessListener { location: Location? ->
                            if (location != null) {
                                val latLng = LatLng(location.latitude, location.longitude)
                                val closestCity = getClosestCity(latLng)
                                locationSelectionPreferences.setIsLocationSelected()
                                locationSelectionPreferences.setCurrentLocation(closestCity)
                                _state.value = state.value.copy(
                                    location = closestCity.name_en,
                                    showDialog = false
                                )
                                getPrayerTimes(closestCity.file)
                                setupAlarms()
                            } else {
                                emitEvent(HomeUIEvent.ShowMessage(resourceProvider.getString(R.string.failed_to_get_location)))
                            }
                        }
                        .addOnFailureListener {
                            emitEvent(HomeUIEvent.ShowMessage(resourceProvider.getString(R.string.failed_to_get_location)))
                        }
                }
            }

            is HomeViewModelEvent.SelectManualLocation -> {

            }

            is HomeViewModelEvent.UpdateLocation -> {
                locationSelectionPreferences.setCurrentLocation(event.city)
            }

            is HomeViewModelEvent.UpdateShowDialog -> {
                locationSelectionPreferences.setIsLocationSelected()
                val city = locationSelectionPreferences.currentLocation ?: return
                _state.value = state.value.copy(location = city.name_en)
                getPrayerTimes(city.file)
                setupAlarms()
                _state.value = state.value.copy(showDialog = event.showDialog)
            }
        }
    }

    private fun setupAlarms() {
        viewModelScope.launch(Dispatchers.IO) {
            PrayerName.entries.forEach { payerName ->
                val prayers = mutableListOf<Date>()
                val prayerIndex = if (payerName == PrayerName.FAJR) 0 else payerName.ordinal + 1
                prayerTimesMap.forEach { (key, value) ->
                    prayers.add("$key ${value[prayerIndex]}".toPrayerDate())
                }
                scheduleNotifications(prayers, payerName)
                prayersAlarmPreferences.savePrayerNotification(payerName.name, true)
            }
        }
    }

    private fun getClosestCity(latLng: LatLng): City {
        val closestCity = cities.minByOrNull {
            val cityLat = it.location.lat
            val cityLon = it.location.long
            val result = sqrt(
                (latLng.latitude - cityLat).pow(2) + (latLng.longitude - cityLon).pow(2)
            )
            result
        }
        return closestCity ?: cities.first()
    }

    private fun handleError(exception: Exception) {
        viewModelScope.launch {
            when (exception) {
                is UnknownHostException -> {
                    _uiEvent.emit(HomeUIEvent.ShowMessage(resourceProvider.getString(R.string.please_check_your_internet_connection)))
                }

                is IOException -> {
                    _uiEvent.emit(HomeUIEvent.ShowMessage(resourceProvider.getString(R.string.slower_internet_connection)))
                }

                is TimeoutException -> {
                    _uiEvent.emit(HomeUIEvent.ShowMessage(resourceProvider.getString(R.string.timeout_error)))
                }

                is HttpException -> {
                    if (exception.code() == 401) {
                        _uiEvent.emit(HomeUIEvent.ShowMessage(resourceProvider.getString(R.string.invalid_email_or_password)))
                    } else if (exception.code() == 500) {
                        _uiEvent.emit(HomeUIEvent.ShowMessage(resourceProvider.getString(R.string.something_went_wrong)))
                    } else {
                        _uiEvent.emit(HomeUIEvent.ShowMessage(resourceProvider.getString(R.string.unknown_error_occurred)))
                    }
                }

                is IllegalArgumentException -> {
                    _uiEvent.emit(
                        HomeUIEvent.ShowMessage(
                            exception.message ?: exception.toString()
                        )
                    )
                }

                else -> {
                    _uiEvent.emit(
                        HomeUIEvent.ShowMessage(
                            exception.message ?: exception.toString()
                        )
                    )
                }
            }
        }
    }

    private fun scheduleNotifications(prayerList: MutableList<Date>, prayerName: PrayerName) {
        prayerList.forEach { date ->
            val alarmItem = AlarmItem(
                date,
                prayerName.name
            )
            alarmScheduler.schedule(alarmItem)
        }
    }
}