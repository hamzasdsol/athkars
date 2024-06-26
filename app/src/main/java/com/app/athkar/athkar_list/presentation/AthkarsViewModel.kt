package com.app.athkar.athkar_list.presentation

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.athkar.R
import com.app.athkar.athkar_list.data.Athkar
import com.app.athkar.athkar_list.domain.AthkarsRepository
import com.app.athkar.core.network.NetworkResult
import com.app.athkar.core.di.ResourceProvider
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.io.IOException
import java.net.UnknownHostException
import java.util.concurrent.TimeoutException
import javax.inject.Inject

@HiltViewModel
class AthkarsViewModel @Inject constructor(
    private val resourceProvider: ResourceProvider,
    private val athkarsRepository: AthkarsRepository
): ViewModel() {

    private val _state = mutableStateOf(AthkarListState())
    val state: State<AthkarListState> = _state
    
    private val _uiEvent = MutableSharedFlow<AthkarListUIEvent>()
    val uiEvent = _uiEvent.asSharedFlow()

    init {
        getAthkars()
    }

    private fun getAthkars() {
        viewModelScope.launch {
            when(val athkarsResponse = athkarsRepository.getAthkars()) {
                is NetworkResult.Success -> {
                    val athkarsList = athkarsResponse.data.athkars
                    _state.value = _state.value.copy(athkars = SnapshotStateList<Athkar>().apply { addAll(athkarsList) })
                }
                is NetworkResult.Failure -> {
                    handleError(athkarsResponse.exception)
                }
            }
        }
    }

    private fun handleError(exception: Exception) {
        viewModelScope.launch {
            when (exception) {
                is UnknownHostException -> {
                    _uiEvent.emit(AthkarListUIEvent.ShowMessage(resourceProvider.getString(R.string.please_check_your_internet_connection)))
                }

                is IOException -> {
                    _uiEvent.emit(AthkarListUIEvent.ShowMessage(resourceProvider.getString(R.string.slower_internet_connection)))
                }

                is TimeoutException -> {
                    _uiEvent.emit(AthkarListUIEvent.ShowMessage(resourceProvider.getString(R.string.timeout_error)))
                }

                is HttpException -> {
                    if (exception.code() == 401) {
                        _uiEvent.emit(AthkarListUIEvent.ShowMessage(resourceProvider.getString(R.string.invalid_email_or_password)))
                    } else if (exception.code() == 500) {
                        _uiEvent.emit(AthkarListUIEvent.ShowMessage(resourceProvider.getString(R.string.something_went_wrong)))
                    } else {
                        _uiEvent.emit(AthkarListUIEvent.ShowMessage(resourceProvider.getString(R.string.unknown_error_occurred)))
                    }
                }

                is IllegalArgumentException -> {
                    _uiEvent.emit(
                        AthkarListUIEvent.ShowMessage(
                            exception.message ?: exception.toString()
                        )
                    )
                }

                else -> {
                    _uiEvent.emit(
                        AthkarListUIEvent.ShowMessage(
                            exception.message ?: exception.toString()
                        )
                    )
                }
            }
        }
    }
}