package com.app.athkar.export.presentation

import android.graphics.Bitmap
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.media3.exoplayer.ExoPlayer
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
import javax.inject.Inject

@HiltViewModel
class ExportViewModel @Inject constructor(
    private val audioDownloaderService: AudioDownloaderService,
    private val appRepository: AppRepository,
    private val resourceProvider: ResourceProvider
) : ViewModel() {


    private val exoPlayer = ExoPlayer.Builder(resourceProvider.getApplicationContext()).build()

    private val _state = mutableStateOf(ExportState(exoPlayer))
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
                    val downloadedFile = audioDownloaderService.downloadAudio(event.downloadUrl)
                    downloadedFile?.let {
                    } ?: run {
                    }
                }
            }

            is ExportViewModelEvent.ExportImage -> {
                viewModelScope.launch {
                    val bitmap: Bitmap = event.picture.createBitmapFromPicture()
                    try {
                        val uri = bitmap.saveToDisk(event.context)
                        _uiEvent.emit(ExportScreenUiEvent.ShowMessage(resourceProvider.getString(R.string.image_saved)))
                    } catch (e: Exception) {
                        _uiEvent.emit(ExportScreenUiEvent.ShowMessage(resourceProvider.getString(R.string.failed_to_save)))
                    }
                }
            }

            is ExportViewModelEvent.ExportVideo -> {
                viewModelScope.launch {
                    _state.value = state.value.copy(isLoading = true)
                    try {
                        val bitmap: Bitmap = event.picture.createBitmapFromPicture()
                        val imagePath = bitmap.saveToCache(event.context)
                        val downloadedFile = audioDownloaderService.downloadAudio(event.audioUrl)
                        val audioPath = downloadedFile?.path ?: ""
                        val videoUri = withContext(Dispatchers.IO) {
                            createVideoFromImageAndAudio(event.context,imagePath, audioPath)
                        }
                        _state.value = state.value.copy(isLoading = false)
                        if (videoUri != null) {
                            _uiEvent.emit(ExportScreenUiEvent.ShowMessage(resourceProvider.getString(R.string.video_saved)))
                        } else {
                            _uiEvent.emit(ExportScreenUiEvent.ShowMessage(resourceProvider.getString(R.string.failed_to_save)))
                        }
                    } catch (e: Exception) {
                        _state.value = state.value.copy(isLoading = false)
                        _uiEvent.emit(ExportScreenUiEvent.ShowMessage(resourceProvider.getString(R.string.failed_to_save)))
                    }
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