package com.app.athkar.core.util

import android.content.SharedPreferences
import javax.inject.Inject

class DefaultSharedPreferencesManager @Inject constructor(
    private val sharedPref: SharedPreferences
): LocationSelectionPreferences {
    override fun setIsLocationSelected() {
        sharedPref.edit()
            .putBoolean(LocationSelectionPreferences.LOCATION_SELECTED, true)
            .apply()
    }

    override fun isLocationSelected() = sharedPref.getBoolean(LocationSelectionPreferences.LOCATION_SELECTED, false)
}