package com.app.athkar.ui.activity

import com.app.athkar.athkar_list.data.Athkar

sealed class MainActivityModelEvent {
    data class SaveAthkar(val athkar: Athkar) : MainActivityModelEvent()
}