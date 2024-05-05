package com.app.athkar.core.navigation

import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.app.athkar.edit_prayer.presentation.EditPrayerScreen
import com.app.athkar.edit_prayer.presentation.EditPrayerState
import com.app.athkar.export.presentation.ExportScreen
import com.app.athkar.export.presentation.ExportViewModel
import com.app.athkar.home.presentation.HomeScreen
import com.app.athkar.home.presentation.HomeViewModel


@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = ScreenRoute.HOME) {
        composable(ScreenRoute.HOME) {
            val homeViewModel: HomeViewModel = hiltViewModel()
            HomeScreen(state = homeViewModel.state.value,
                uiEvent = homeViewModel.uiEvent,
                onEvent = homeViewModel::onEvent,
                navigateTo = {
                    navController.navigate(it)
                })
        }

        composable(ScreenRoute.EDIT_PRAYER) {
            EditPrayerScreen(state = EditPrayerState()) {
                navController.navigateUp()
            }
        }

        composable(ScreenRoute.EXPORT) {
            val exportViewModel: ExportViewModel = hiltViewModel()
            ExportScreen(
                state = exportViewModel.state.value,
                onEvent = exportViewModel::onEvent,
                uiEvent = exportViewModel.uiEvent,
                navController = navController
            )
        }
    }
}


object ScreenRoute {
    const val HOME = "home"
    const val EDIT_PRAYER = "edit_prayer"
    const val EXPORT = "export"
    const val ATHKAR_LIST = "athkar_list"
}