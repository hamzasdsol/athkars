package com.app.athkar.navigation

import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.navigation
import com.app.athkar.athkar_list.presentation.AthkarListScreen
import com.app.athkar.athkar_list.presentation.AthkarsViewModel
import com.app.athkar.destinations.AthkarListScreenDestination
import com.app.athkar.destinations.EditPrayerScreenDestination
import com.app.athkar.destinations.ExportScreenDestination
import com.app.athkar.destinations.HomeScreenDestination
import com.app.athkar.edit_prayer.presentation.EditPrayerScreen
import com.app.athkar.edit_prayer.presentation.EditPrayerViewModel
import com.app.athkar.export.presentation.ExportScreen
import com.app.athkar.export.presentation.ExportViewModel
import com.app.athkar.home.presentation.HomeScreen
import com.app.athkar.home.presentation.HomeViewModel
import com.app.athkar.navigation.Destinations.APP_NAV_GRAPH_ROUTE
import com.ramcosta.composedestinations.utils.composable

fun NavGraphBuilder.appNavGraph(navController: NavController, startDestination: String) {

    navigation(route = APP_NAV_GRAPH_ROUTE, startDestination = startDestination) {
        composable(HomeScreenDestination) {
            val homeViewModel: HomeViewModel = hiltViewModel()
            HomeScreen(
                state = homeViewModel.state.value,
                onEvent = homeViewModel::onEvent,
//                navHostController = destinationsNavigator(navController)
            )
        }

        composable(AthkarListScreenDestination) {
            val athkarListViewModel: AthkarsViewModel = hiltViewModel()
            AthkarListScreen(
                state = athkarListViewModel.state.value,
                onEvent = athkarListViewModel::onEvent,
                navHostController = destinationsNavigator(navController)
            )
        }

        composable(EditPrayerScreenDestination) {
            val editPrayerViewModel: EditPrayerViewModel = hiltViewModel()
            EditPrayerScreen(
                state = editPrayerViewModel.state.value,
                onEvent = editPrayerViewModel::onEvent,
//                navHostController = destinationsNavigator(navController)
            )
        }

        composable(ExportScreenDestination) {
            val exportViewModel: ExportViewModel = hiltViewModel()
            ExportScreen(
                state = exportViewModel.state.value,
                onEvent = exportViewModel::onEvent,
                uiEvent = exportViewModel.uiEvent,
                navHostController = destinationsNavigator(navController)
            )
        }
    }
}