package com.app.athkar.core.di

import android.content.Context
import androidx.annotation.StringRes
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ResourceProvider @Inject constructor(
    @ApplicationContext private val context: Context
) {

    fun getApplicationContext(): Context {
        return context
    }

    fun getString(
        @StringRes stringResId: Int,
    ): String {
        return context.getString(stringResId)
    }
}