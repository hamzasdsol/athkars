package com.app.athkar.core.util

import android.content.SharedPreferences
import com.app.athkar.data.model.network.City
import com.google.gson.Gson
import javax.inject.Inject

class DefaultSharedPreferencesManager @Inject constructor(
    private val sharedPref: SharedPreferences
) : LocationSelectionPreferences {
    override fun setIsLocationSelected() {
        sharedPref.edit()
            .putBoolean(LocationSelectionPreferences.LOCATION_SELECTED, true)
            .apply()
    }

    override fun isLocationSelected() =
        sharedPref.getBoolean(LocationSelectionPreferences.LOCATION_SELECTED, false)

    override fun setCurrentLocation(city: City) {
        val location: String = Gson().toJson(city)
        sharedPref.edit()
            .putString(LocationSelectionPreferences.CURRENT_LOCATION, location)
            .apply()
    }

    override val currentLocation: City?
        get() = sharedPref.getString(LocationSelectionPreferences.CURRENT_LOCATION, null)?.let {
            Gson().fromJson(it, City::class.java)
        }
}