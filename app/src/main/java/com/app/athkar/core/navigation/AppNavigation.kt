package com.app.athkar.core.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.app.athkar.edit_prayer.presentation.EditPrayerScreen
import com.app.athkar.edit_prayer.presentation.EditPrayerState
import com.app.athkar.home.presentation.HomeScreen
import com.app.athkar.home.presentation.HomeState


@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = ScreenSoute.HOME) {
        composable(ScreenSoute.HOME) {
            HomeScreen(state = HomeState(), navigateTo = {
                navController.navigate(it)
            })
        }

        composable(ScreenSoute.EDIT_PRAYER) {
            EditPrayerScreen(state = EditPrayerState()) {
                navController.navigateUp()
            }
        }
    }
}


object ScreenSoute {
    const val HOME = "home"
    const val EDIT_PRAYER = "edit_prayer"
}