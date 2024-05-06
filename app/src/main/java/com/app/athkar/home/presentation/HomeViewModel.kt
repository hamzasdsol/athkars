package com.app.athkar.home.presentation

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
class HomeViewModel @Inject constructor(
    private val locationSelectionPreferences: LocationSelectionPreferences,
    private val resourceProvider: ResourceProvider,
    private val appRepository: AppRepository
) : ViewModel() {

    private val _state = mutableStateOf(HomeState())
    val state: State<HomeState> = _state

    private val _uiEvent = MutableSharedFlow<HomeUIEvent>()
    val uiEvent = _uiEvent.asSharedFlow()

    init {
        _state.value =
            state.value.copy(isFirstTime = !locationSelectionPreferences.isLocationSelected())
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
                    _state.value = state.value.copy(cities = cities)
                }

                is Result.Failure -> {
                    handleError(citiesResponse.exception)
                }
            }
        }
    }

    fun onEvent(event: HomeViewModelEvent) {
        when (event) {
            is HomeViewModelEvent.SelectAutoLocation -> {}
            is HomeViewModelEvent.SelectManualLocation -> {

            }

            is HomeViewModelEvent.UpdateLocation -> {
                locationSelectionPreferences.setIsLocationSelected()
                locationSelectionPreferences.setCurrentLocation(event.city)
                _state.value = state.value.copy(location = event.city.name_en)
                getPrayerTimes(event.city.file)
            }
        }
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