package com.app.athkar.home.data

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class City(
    val file: String,
    val id: String,
    val location: Location,
    @SerializedName("name_ar")
    val name_ar: String,
    @SerializedName("name_en")
    val name_en: String
) : Parcelable