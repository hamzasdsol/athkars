package com.app.athkar.athkar_list.presentation

import android.widget.Toast
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.app.athkar.R
import com.app.athkar.athkar_list.presentation.composables.ExportPopupMenu
import com.app.athkar.athkar_list.presentation.composables.PagerControls
import com.app.athkar.athkar_list.presentation.composables.PagerItemContent
import com.app.athkar.core.navigation.ScreenRoute
import com.app.athkar.core.ui.AppToolbar
import com.app.athkar.ui.theme.AthkarTheme
import com.app.athkar.ui.theme.PagerActiveIndicator
import com.app.athkar.ui.theme.PagerInActiveIndicator
import com.google.accompanist.pager.HorizontalPagerIndicator
import com.ramcosta.composedestinations.annotation.Destination
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.launch
import kotlin.math.exp

@OptIn(ExperimentalFoundationApi::class)
@Destination(ScreenRoute.ATHKAR_LIST)
@Composable
fun AthkarListScreen(
    state: AthkarListState,
    onEvent: (AthkarsViewModelEvent) -> Unit = {},
    uiEvent: SharedFlow<AthkarListUIEvent> = MutableSharedFlow(),
    navigateTo: (String) -> Unit = {},
    navigateUp: () -> Unit = {}
) {
    val context = LocalContext.current
    val pagerState = rememberPagerState(pageCount = {
        state.athkars.size
    }
    )

    var expanded by remember { mutableStateOf(false) }

    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        uiEvent.collect { event ->
            when (event) {
                is AthkarListUIEvent.ShowMessage -> {
                    Toast.makeText(
                        context,
                        event.message,
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }

    Box(
        modifier =
        Modifier
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
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            AppToolbar(
                title = "Athkar",
                leftIcon = {
                    Icon(
                        modifier = Modifier.clickable {
                            navigateUp()
                        },
                        painter = painterResource(id = R.drawable.ic_back),
                        tint = Color.White,
                        contentDescription = "back icon"
                    )
                },
                rightIcon = {
                    Box {
                        Icon(
                            modifier = Modifier.clickable {
                                expanded = true
                            },
                            painter = painterResource(id = R.drawable.ic_export),
                            tint = Color.White,
                            contentDescription = "export icon"
                        )

                        ExportPopupMenu(
                            expanded = expanded,
                            setExpanded = { expanded = it },
                            onVideoTap = { /* handle video export */ },
                            onImageTap = { /* handle image export */ }
                        )
                    }
                }
            )

            Box(modifier = Modifier.clip(RoundedCornerShape(8.dp))) {
                Image(
                    painter = painterResource(id = R.drawable.blue_mosque),
                    contentDescription = "mosque"
                )
            }

            Column(
                Modifier.verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                HorizontalPager(state = pagerState) {
                    Column {
                        PagerItemContent(text = state.athkars[it].text)
                    }
                }

                Box(
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally)
                        .padding(top = 10.dp, bottom = 10.dp)
                ) {
                    HorizontalPagerIndicator(
                        pageCount = state.athkars.size,
                        pagerState = pagerState,
                        activeColor = PagerActiveIndicator,
                        inactiveColor = PagerInActiveIndicator,
                        indicatorHeight = 12.dp,
                        indicatorWidth = 12.dp,
                        modifier = Modifier.align(Alignment.Center))

                    LaunchedEffect(pagerState) {
                        snapshotFlow { pagerState.currentPage }
                            .collect { currentPage ->
                                pagerState.animateScrollToPage(currentPage)
                            }
                    }
                }

                PagerControls(
                    onNextTap = {
                        handleSlide(pagerState, state.athkars.size, TapDirection.FORWARD, coroutineScope)
                    },
                    onBackTap = {
                        handleSlide(pagerState, state.athkars.size, TapDirection.BACK, coroutineScope)
                    },
                    onPlayTap = { }
                )
            }
        }
    }
}


@OptIn(ExperimentalFoundationApi::class)
private fun handleSlide(
    pagerState: PagerState,
    totalPages: Int,
    tapDirection: TapDirection,
    coroutineScope: CoroutineScope
) {
    val currentPage = pagerState.currentPage

    var nextPage = 0

    nextPage = when(tapDirection) {
        TapDirection.BACK -> if (currentPage != 0) currentPage - 1 else totalPages - 1
        TapDirection.FORWARD -> if (currentPage < totalPages - 1) currentPage + 1 else 0
    }

    coroutineScope.launch { pagerState.animateScrollToPage(nextPage) }
}

enum class TapDirection {
    BACK, FORWARD
}


@Preview
@Composable
private fun AthkarsListScreenPreview() {
    AthkarTheme {
        AthkarListScreen(state = AthkarListState())
    }
}