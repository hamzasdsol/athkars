package com.app.athkar.data.model.network

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Athkar(
    val link: String,
    val text: String
) : Parcelable