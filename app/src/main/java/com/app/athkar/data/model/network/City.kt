package com.app.athkar.data.model.network

import com.google.gson.annotations.SerializedName

data class City(
    val file: String,
    val id: String,
    val location: Location,
    @SerializedName("name_ar")
    val name_ar: String,
    @SerializedName("name_en")
    val name_en: String
)