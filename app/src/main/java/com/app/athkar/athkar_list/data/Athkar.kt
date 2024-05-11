package com.app.athkar.athkar_list.data

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Athkar(
    val link: String,
    val text: String
) : Parcelable