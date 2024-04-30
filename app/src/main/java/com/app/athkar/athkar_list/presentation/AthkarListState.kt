package com.app.athkar.athkar_list.presentation

data class AthkarListState(
    val athkars: List<Athkar> = emptyList()
)

data class Athkar(
    val text: String = "",
    val link: String = ""
)
