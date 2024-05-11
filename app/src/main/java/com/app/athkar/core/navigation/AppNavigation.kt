package com.app.athkar.core.navigation

import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.app.athkar.ui.activity.MainActivityViewModel
import com.app.athkar.athkar_list.presentation.AthkarListScreen
import com.app.athkar.athkar_list.presentation.AthkarsViewModel
import com.app.athkar.edit_prayer.presentation.EditPrayerScreen
import com.app.athkar.edit_prayer.presentation.EditPrayerViewModel
import com.app.athkar.export.enums.EXPORTTYPE
import com.app.athkar.export.presentation.ExportScreen
import com.app.athkar.export.presentation.ExportViewModel
import com.app.athkar.home.presentation.HomeScreen
import com.app.athkar.home.presentation.HomeViewModel


@Composable
fun AppNavigation(mainActivityViewModel: MainActivityViewModel) {
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
            val editPrayerViewModel: EditPrayerViewModel = hiltViewModel()
            EditPrayerScreen(
                state = editPrayerViewModel.state.value,
                onEvent = editPrayerViewModel::onEvent,
                uiEvent = editPrayerViewModel.uiEvent,
            ) {
                navController.navigateUp()
            }
        }

        composable(
            ScreenRoute.EXPORT + "/{exportType}",
            arguments = listOf(
                navArgument(
                    "exportType",
                    builder = { type = NavType.StringType }),
            )
        ) {backStackEntry ->
            val exportViewModel: ExportViewModel = hiltViewModel()
            ExportScreen(
                state = exportViewModel.state.value,
                athkar = mainActivityViewModel.state.value.athkar,
                onEvent = exportViewModel::onEvent,
                uiEvent = exportViewModel.uiEvent,
                exportType = EXPORTTYPE.valueOf(backStackEntry.arguments?.getString("exportType") ?: ""),
                navigateUp = {
                    navController.navigateUp()
                },
            )
        }

        composable(ScreenRoute.ATHKAR_LIST) {
            val athkarsViewModel: AthkarsViewModel = hiltViewModel()
            AthkarListScreen(
                state = athkarsViewModel.state.value,
                onMainEvent = mainActivityViewModel::onEvent,
                uiEvent = athkarsViewModel.uiEvent,
                navigateTo = {
                    navController.navigate(it)
                },
                navigateUp = {
                    navController.navigateUp()
                }
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