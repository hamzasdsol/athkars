package com.app.athkar.home.presentation

import android.annotation.SuppressLint
import android.location.Location
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.athkar.R
import com.app.athkar.core.util.LocationSelectionPreferences
import com.app.athkar.data.model.CurrentPrayerDetails
import com.app.athkar.data.model.network.City
import com.app.athkar.data.model.network.GetCitiesResponse
import com.app.athkar.data.model.network.GetPrayerTimesResponse
import com.app.athkar.data.model.toPrayersModel
import com.app.athkar.di.ResourceProvider
import com.app.athkar.domain.Result
import com.app.athkar.domain.repository.AppRepository
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.model.LatLng
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
import kotlin.math.pow
import kotlin.math.sqrt

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val locationSelectionPreferences: LocationSelectionPreferences,
    private val resourceProvider: ResourceProvider,
    private val appRepository: AppRepository
) : ViewModel() {

    private val _state = mutableStateOf(HomeState())
    val state: State<HomeState> = _state

    private val _uiEvent = MutableSharedFlow<HomeUIEvent>()
    val uiEvent = _uiEvent.asSharedFlow()

    private val cities: MutableList<City> = mutableListOf()

    private lateinit var fusedLocationClient: FusedLocationProviderClient

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
        viewModelScope.launch {
            when (val prayerTimesResponse = appRepository.getPrayerTimes(currentLocation)) {
                is Result.Success -> {
                    val response: GetPrayerTimesResponse = prayerTimesResponse.data
                    val prayerTimes = response.prayerTimes
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

                is Result.Failure -> {
                    handleError(prayerTimesResponse.exception)
                }
            }
        }
    }

    private fun getCities() {
        viewModelScope.launch {
            when (val citiesResponse = appRepository.getCities()) {
                is Result.Success -> {
                    val response: GetCitiesResponse = citiesResponse.data
                    val cities: List<City> = response.cities
                    this@HomeViewModel.cities.apply {
                        clear()
                        addAll(cities)
                    }
                    _state.value = state.value.copy(cities = cities)
                }

                is Result.Failure -> {
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
                viewModelScope.launch {
                    fusedLocationClient =
                        LocationServices.getFusedLocationProviderClient(resourceProvider.getApplicationContext())
                    fusedLocationClient.lastLocation
                        .addOnSuccessListener { location: Location? ->
                            if (location != null) {
                                val latLng = LatLng(location.latitude, location.longitude)
                                val closestCity = getClosestCity(latLng)
                                locationSelectionPreferences.setIsLocationSelected()
                                locationSelectionPreferences.setCurrentLocation(closestCity)
                                _state.value = state.value.copy(location = closestCity.name_en, showDialog = false)
                                getPrayerTimes(closestCity.file)
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
                locationSelectionPreferences.setIsLocationSelected()
                locationSelectionPreferences.setCurrentLocation(event.city)
                _state.value = state.value.copy(location = event.city.name_en)
                getPrayerTimes(event.city.file)
            }

            is HomeViewModelEvent.UpdateShowDialog -> {
                _state.value = state.value.copy(showDialog = event.showDialog)
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
}