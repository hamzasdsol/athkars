package com.app.athkar.core.util

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

inline fun <reified T> String.toResponseModel(): T {
    val typeToken = object : TypeToken<T>() {}.type
    return Gson().fromJson(this, typeToken)
}