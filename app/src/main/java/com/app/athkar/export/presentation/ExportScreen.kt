package com.app.athkar.export.presentation

import android.graphics.Picture
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.graphics.Canvas
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.draw
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.PlayerView
import androidx.navigation.NavController
import com.app.athkar.R
import com.app.athkar.athkar_list.presentation.composables.ExportPopupMenu
import com.app.athkar.core.navigation.ScreenRoute
import com.app.athkar.core.ui.AppToolbar
import com.app.athkar.core.util.Constants
import com.app.athkar.export.enums.EXPORTTYPE
import com.app.athkar.ui.theme.ExportText
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow

@Composable
fun ExportScreen(
    state: ExportState,
    onEvent: (ExportViewModelEvent) -> Unit = {},
    uiEvent: SharedFlow<ExportScreenUiEvent> = MutableSharedFlow(),
    navigateUp: () -> Unit = {},
) {
    val context = LocalContext.current
    var playerIcon by remember { mutableStateOf(Icons.Default.PlayArrow) }

    val configuration = LocalConfiguration.current

    val screenWidth = configuration.screenWidthDp.dp
    val exportedImageWidth = screenWidth - 16.dp
    val exportedImageHeight = (exportedImageWidth - 16.dp) * Constants.IMAGE_ASPECT_RATIO

    val picture = remember { Picture() }

    val exoPlayer = ExoPlayer.Builder(context).build().apply {
        this.addListener(object : Player.Listener {
            override fun onIsPlayingChanged(isPlaying: Boolean) {
                playerIcon = if (isPlaying) {
                    Icons.Default.AddCircle
                } else {
                    Icons.Default.PlayArrow
                }
            }
        })
    }

    val mediaSource = remember(state.audioUrl) {
        MediaItem.fromUri(state.audioUrl)
    }

    LaunchedEffect(Unit) {
        uiEvent.collect {
            when (it) {
                is ExportScreenUiEvent.Pause -> {
                    exoPlayer.pause()
                }

                is ExportScreenUiEvent.Play -> {
                    exoPlayer.play()
                }

                is ExportScreenUiEvent.ShowMessage -> {
                    Toast.makeText(context, it.message, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    LaunchedEffect(mediaSource) {
        exoPlayer.setMediaItem(mediaSource)
        exoPlayer.prepare()
        exoPlayer.playWhenReady = false
    }

    DisposableEffect(Unit) {
        onDispose {
            exoPlayer.release()
        }
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
                AppToolbar(title = "Export", leftIcon = {
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
                                //TODO: Implement export
                            },
                            painter = painterResource(id = R.drawable.ic_export),
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
                    Button(onClick = {
                        onEvent(ExportViewModelEvent.Download("https://rommanapps.com/android/theker_43.mp3"))
                    }
                    ) {
                        Text("Download Audio")
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(text = state.text)
                    if (state.audioUrl.isNotEmpty())
                        AndroidView(
                            factory = { ctx ->
                                PlayerView(ctx).apply {
                                    player = exoPlayer
                                    useController = false
                                }
                            },
                            modifier = Modifier
                                .padding(horizontal = 16.dp)
                                .fillMaxWidth()
                                .height(0.dp)
                        )

                    Spacer(modifier = Modifier.height(16.dp))

                    Column(
                        modifier = Modifier
                            .width(exportedImageWidth)
                            .height(exportedImageHeight)
                            .drawWithCache {
                                val width = this.size.width.toInt()
                                val height = this.size.height.toInt()
                                onDrawWithContent {
                                    val pictureCanvas =
                                        Canvas(
                                            picture.beginRecording(
                                                width,
                                                height
                                            )
                                        )
                                    draw(this, this.layoutDirection, pictureCanvas, this.size) {
                                        this@onDrawWithContent.drawContent()
                                    }
                                    picture.endRecording()

                                    drawIntoCanvas { canvas ->
                                        canvas.nativeCanvas.drawPicture(
                                            picture
                                        )
                                    }
                                }
                            }
                    ) {
                        Box(
                            Modifier
                                .width(exportedImageWidth)
                                .height(exportedImageHeight),
                            contentAlignment = Alignment.Center
                        ) {
                            Image(
                                painter = painterResource(id = R.drawable.img_export_template),
                                contentDescription = null,
                                contentScale = ContentScale.FillBounds,
                                modifier = Modifier
                                    .width(exportedImageWidth)
                                    .height(exportedImageHeight)
                            )

                            Text(
                                text = "Lorem Ipsum is simply dummy text of the printing and typesetting industry. Lorem Ipsum has been the industry's standard dummy text ever since the 1500s, when an unknown printer took a galley of type and scrambled it to make a type specimen book.",
                                modifier = Modifier
                                    .padding(horizontal = 32.dp)
                                    .align(Alignment.Center),
                                color = ExportText,
                                fontSize = 16.sp,
                                textAlign = TextAlign.Center
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))
                    IconButton(onClick = {
                        if (state.isPlaying) {
                            onEvent(ExportViewModelEvent.Pause)
                        } else {
                            onEvent(ExportViewModelEvent.Play)
                        }
                    }) {
                        Icon(
                            imageVector = playerIcon,
                            contentDescription = "Play/Pause"
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))
                    Button(onClick = {
                        onEvent(ExportViewModelEvent.ExportImage(context, picture))
                    }) {
                        Text("Export Image")
                    }

                    Spacer(modifier = Modifier.height(16.dp))
                    Button(onClick = {
                        onEvent(ExportViewModelEvent.ExportVideo(context, picture, state.audioUrl))
                    }) {
                        Text("Export Video")
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewExportScreen() {
    ExportScreen(ExportState("Export Screen"))
}