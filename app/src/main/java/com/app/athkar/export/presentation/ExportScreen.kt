package com.app.athkar.export.presentation

import android.graphics.Picture
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.ui.PlayerView
import com.app.athkar.MainActivityState
import com.app.athkar.R
import com.app.athkar.core.composables.LoadingDialog
import com.app.athkar.core.ui.AppToolbar
import com.app.athkar.data.model.network.Athkar
import com.app.athkar.export.enums.EXPORTTYPE
import com.app.athkar.export.presentation.composables.CanvasImage
import com.app.athkar.ui.theme.ButtonBackground
import com.app.athkar.ui.theme.ControlsColor
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow

@Composable
fun ExportScreen(
    state: ExportState,
    mainState: MainActivityState,
    onEvent: (ExportViewModelEvent) -> Unit = {},
    uiEvent: SharedFlow<ExportScreenUiEvent> = MutableSharedFlow(),
    exportType: EXPORTTYPE = EXPORTTYPE.IMAGE,
    navigateUp: () -> Unit = {},
) {
    val context = LocalContext.current
    var playerIcon by remember {
        mutableIntStateOf(R.drawable.ic_play)
    }

    val picture = remember { mutableStateOf(Picture()) }

    state.exoPlayer?.apply {
        this.addListener(object : Player.Listener {
            override fun onIsPlayingChanged(isPlaying: Boolean) {
                playerIcon = if (isPlaying) {
                    R.drawable.ic_pause
                } else {
                    R.drawable.ic_play
                }
            }
        })
    }

    val mediaSource = remember(mainState.athkar.link) {
        MediaItem.fromUri(mainState.athkar.link)
    }

    LaunchedEffect(Unit) {
        uiEvent.collect {
            when (it) {
                is ExportScreenUiEvent.Pause -> {
                    state.exoPlayer?.pause()
                }

                is ExportScreenUiEvent.Play -> {
                    state.exoPlayer?.play()
                }

                is ExportScreenUiEvent.ShowMessage -> {
                    Toast.makeText(context, it.message, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    LaunchedEffect(mediaSource) {
        state.exoPlayer?.setMediaItem(mediaSource)
        state.exoPlayer?.prepare()
        state.exoPlayer?.playWhenReady = false
    }

    DisposableEffect(Unit) {
        onDispose {
            state.exoPlayer?.release()
        }
    }

    if (state.isLoading) {
        LoadingDialog()
    }

    Scaffold { paddingValues ->
        Box(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
                .background(Color.White)
        ) {
            Image(
                modifier = Modifier.fillMaxWidth(),
                painter = painterResource(id = R.drawable.bg_athkars),
                contentDescription = "background",
                contentScale = ContentScale.FillWidth,
            )

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                AppToolbar(title = stringResource(R.string.export), leftIcon = {
                    Icon(
                        modifier = Modifier.clickable {
                            navigateUp()
                        },
                        painter = painterResource(id = R.drawable.ic_back),
                        tint = Color.White,
                        contentDescription = "back icon"
                    )
                }, rightIcon = {
                    Box {
                        Icon(
                            modifier = Modifier.clickable {
                                when (exportType) {
                                    EXPORTTYPE.IMAGE -> onEvent(
                                        ExportViewModelEvent.ExportImage(
                                            context, picture.value
                                        )
                                    )

                                    EXPORTTYPE.VIDEO -> onEvent(
                                        ExportViewModelEvent.ExportVideo(
                                            context, picture.value, mainState.athkar.link
                                        )
                                    )
                                }
                            },
                            painter = painterResource(id = R.drawable.ic_download),
                            tint = Color.White,
                            contentDescription = "export icon"
                        )
                    }
                })

                Column(
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .fillMaxWidth()
                        .verticalScroll(rememberScrollState()),
                ) {
                    if (mainState.athkar.link.isNotEmpty()) AndroidView(
                        factory = { ctx ->
                            PlayerView(ctx).apply {
                                player = state.exoPlayer
                                useController = false
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(0.dp)
                    )

                    CanvasImage(mainState.athkar.text, picture.value)

                    if (exportType == EXPORTTYPE.VIDEO) {
                        Box(
                            contentAlignment = Alignment.Center,
                        ) {
                            Image(
                                painter = painterResource(id = R.drawable.ic_play_bg),
                                contentDescription = "PlayBG",
                            )
                            IconButton(
                                modifier = Modifier
                                    .padding(bottom = 4.dp, end = 4.dp),
                                onClick = {

                                    if (state.isPlaying) {
                                        onEvent(ExportViewModelEvent.Pause)
                                    } else {
                                        onEvent(ExportViewModelEvent.Play)
                                    }
                                }) {
                                Icon(
                                    painterResource(id = playerIcon),
                                    contentDescription = "Play/Pause",
                                    tint = ControlsColor
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewExportScreen() {
    ExportScreen(ExportState(), MainActivityState(Athkar("", "")))
}