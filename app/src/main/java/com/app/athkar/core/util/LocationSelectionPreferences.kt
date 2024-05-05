package com.app.athkar.core.util

interface LocationSelectionPreferences {
    fun setIsLocationSelected()
    fun isLocationSelected(): Boolean

    companion object {
        const val PREFERENCES_NAME = "location_selection_preferences"
        const val LOCATION_SELECTED = "location_selected"
    }
}