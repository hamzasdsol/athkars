package com.app.athkar.di

import android.content.Context
import androidx.annotation.StringRes
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ResourceProvider @Inject constructor(@ApplicationContext val context: Context){

    fun getString(
        @StringRes stringResId: Int,
    ): String {
        return context.getString(stringResId)
    }
}