package com.app.athkar.core.util

import com.app.athkar.home.data.City

interface LocationSelectionPreferences {
    fun setIsLocationSelected()
    fun isLocationSelected(): Boolean

    fun setCurrentLocation(city: City)
    val currentLocation: City?

    companion object {
        const val PREFERENCES_NAME = "location_selection_preferences"
        const val LOCATION_SELECTED = "location_selected"
        const val CURRENT_LOCATION = "current_location"
    }
}