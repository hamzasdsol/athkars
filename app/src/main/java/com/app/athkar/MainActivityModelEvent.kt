package com.app.athkar

import com.app.athkar.data.model.network.Athkar

sealed class MainActivityModelEvent {
    data class SaveAthkar(val athkar: Athkar) : MainActivityModelEvent()
}