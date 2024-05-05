package com.app.athkar.athkar_list.presentation

import androidx.compose.runtime.Composable
import com.app.athkar.core.navigation.ScreenRoute
import com.ramcosta.composedestinations.annotation.Destination

@Destination(ScreenRoute.ATHKAR_LIST)
@Composable
fun AthkarListScreen(
    state: AthkarListState,
    onEvent: (AthkarsViewModelEvent) -> Unit = {},
) {

}