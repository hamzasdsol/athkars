package com.app.athkar.export.presentation

import android.graphics.Bitmap
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.athkar.R
import com.app.athkar.di.ResourceProvider
import com.app.athkar.domain.repository.AppRepository
import com.app.athkar.export.audio_downloader.AudioDownloaderService
import com.app.athkar.export.util.createBitmapFromPicture
import com.app.athkar.export.util.createVideoFromImageAndAudio
import com.app.athkar.export.util.saveToCache
import com.app.athkar.export.util.saveToDisk
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.HttpException
import java.io.IOException
import java.util.concurrent.TimeoutException
import javax.inject.Inject
import com.app.athkar.domain.Result
import java.net.UnknownHostException

@HiltViewModel
class ExportViewModel @Inject constructor(
    private val audioDownloaderService: AudioDownloaderService,
    private val appRepository: AppRepository,
    private val resourceProvider: ResourceProvider
) : ViewModel() {

    private val audioUrl = "https://rommanapps.com/android/theker_43.mp3"

    private val _state = mutableStateOf(ExportState(audioUrl = audioUrl))
    val state: State<ExportState> = _state

    private val _uiEvent = MutableSharedFlow<ExportScreenUiEvent>()
    val uiEvent: SharedFlow<ExportScreenUiEvent> = _uiEvent.asSharedFlow()

    fun onEvent(event: ExportViewModelEvent) {
        when (event) {
            is ExportViewModelEvent.Pause -> {
                viewModelScope.launch {
                    _state.value = state.value.copy(isPlaying = false)
                    _uiEvent.emit(ExportScreenUiEvent.Pause)
                }
            }

            is ExportViewModelEvent.Play -> {
                viewModelScope.launch {
                    _state.value = state.value.copy(isPlaying = true)
                    _uiEvent.emit(ExportScreenUiEvent.Play)
                }
            }

            is ExportViewModelEvent.Download -> {
                viewModelScope.launch {
                    _state.value = ExportState("Downloading...")
                    val downloadedFile = audioDownloaderService.downloadAudio(event.downloadUrl)
                    downloadedFile?.let {
                        _state.value = ExportState("Downloaded to $it")
                    } ?: run {
                        _state.value = ExportState("Failed to download")
                    }
                }
            }

            is ExportViewModelEvent.ExportImage -> {
                viewModelScope.launch {
                    val bitmap: Bitmap = event.picture.createBitmapFromPicture()
                    try {
                        val uri = bitmap.saveToDisk(event.context)
                        _state.value = state.value.copy(text = uri.toString())
                    } catch (e: Exception) {
                        _state.value = state.value.copy(text = e.message ?: "Failed to save")
                    }
                }
            }

            is ExportViewModelEvent.ExportVideo -> {
                viewModelScope.launch {
                    try {
                        val bitmap: Bitmap = event.picture.createBitmapFromPicture()
                        val imagePath = bitmap.saveToCache(event.context)
                        val downloadedFile = audioDownloaderService.downloadAudio(event.audioUrl)
                        val audioPath = downloadedFile?.path ?: ""
                        val videoPath = withContext(Dispatchers.IO) {
                            createVideoFromImageAndAudio(imagePath, audioPath)
                        }
                        _state.value = state.value.copy(text = videoPath)
                    } catch (e: Exception) {
                        _state.value = state.value.copy(text = e.message ?: "Failed to save")
                    }
                }
            }
        }
    }

    private fun handleError(exception: Exception) {
        viewModelScope.launch {
            when (exception) {
                is UnknownHostException -> {
                    _uiEvent.emit(ExportScreenUiEvent.ShowMessage(resourceProvider.getString(R.string.please_check_your_internet_connection)))
                }

                is IOException -> {
                    _uiEvent.emit(ExportScreenUiEvent.ShowMessage(resourceProvider.getString(R.string.slower_internet_connection)))
                }

                is TimeoutException -> {
                    _uiEvent.emit(ExportScreenUiEvent.ShowMessage(resourceProvider.getString(R.string.timeout_error)))
                }

                is HttpException -> {
                    if (exception.code() == 401) {
                        _uiEvent.emit(ExportScreenUiEvent.ShowMessage(resourceProvider.getString(R.string.invalid_email_or_password)))
                    } else if (exception.code() == 500) {
                        _uiEvent.emit(ExportScreenUiEvent.ShowMessage(resourceProvider.getString(R.string.something_went_wrong)))
                    } else {
                        _uiEvent.emit(ExportScreenUiEvent.ShowMessage(resourceProvider.getString(R.string.unknown_error_occurred)))
                    }
                }

                is IllegalArgumentException -> {
                    _uiEvent.emit(
                        ExportScreenUiEvent.ShowMessage(
                            exception.message ?: exception.toString()
                        )
                    )
                }

                else -> {
                    _uiEvent.emit(
                        ExportScreenUiEvent.ShowMessage(
                            exception.message ?: exception.toString()
                        )
                    )
                }
            }
        }
    }
}

sealed class ExportScreenUiEvent {
    data object Pause : ExportScreenUiEvent()
    data object Play : ExportScreenUiEvent()
    data class ShowMessage(val message: String) : ExportScreenUiEvent()
}