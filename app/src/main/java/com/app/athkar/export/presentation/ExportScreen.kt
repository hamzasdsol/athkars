package com.app.athkar.export.presentation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.app.athkar.navigation.Destinations
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator

@Destination(Destinations.EXPORT_ROUTE)
@Composable
fun ExportScreen(
    state: ExportState,
    onEvent: (ExportViewModelEvent) -> Unit = {},
    navHostController: DestinationsNavigator? = null
) {
    Scaffold { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Button(onClick = {
                onEvent(ExportViewModelEvent.Download("https://rommanapps.com/android/theker_43.mp3"))
            }
            ) {
                Text("Download Audio")
            }
            Spacer(modifier = Modifier.height(16.dp))
            Text(text = state.text)
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewExportScreen() {
    ExportScreen(ExportState("Export Screen"))
}