package com.app.athkar.athkar_list.presentation

import androidx.compose.runtime.Composable
import com.app.athkar.navigation.Destinations
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator

@Destination(Destinations.ATHKAR_LIST_ROUTE)
@Composable
fun AthkarListScreen(
    state: AthkarListState,
    onEvent: (AthkarsViewModelEvent) -> Unit = {},
    navHostController: DestinationsNavigator? = null
) {

}