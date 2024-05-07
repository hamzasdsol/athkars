package com.app.athkar.athkar_list.presentation

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import com.app.athkar.data.model.network.Athkar

data class AthkarListState(
    val athkars: SnapshotStateList<Athkar> = mutableStateListOf(),
)

/*
data class Athkar(
    val text: String = "",
    val link: String = ""
)
*/
