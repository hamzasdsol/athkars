package com.app.athkar.home.presentation.composables

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.app.athkar.R
import com.app.athkar.home.presentation.PrayerItem
import com.app.athkar.ui.theme.AthkarTheme
import com.app.athkar.ui.theme.CardBackground

@Composable
fun AllPrayers(
    list: List<PrayerItem>
) {
    Card(
        modifier = Modifier
            .fillMaxWidth(),
        shape = CardDefaults.outlinedShape,
        border = BorderStroke(1.dp, color = Color.White)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.White.copy(alpha = 0.6f))
                .padding(16.dp)
        ) {
            LazyVerticalGrid(
                modifier = Modifier.padding(8.dp),
                columns = GridCells.Fixed(3),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(list) {
                    PrayerItem(it)
                }
            }

            Image(
                modifier = Modifier.align(Alignment.TopEnd),
                painter = painterResource(id = R.drawable.ic_edit), contentDescription = "Edit"
            )
        }
    }
}

@Preview
@Composable
private fun AllPrayersPreview() {
    val list = listOf(
        PrayerItem(name = "Fajr", iconRes = R.drawable.ic_fajr),
        PrayerItem(name = "Shuruq", iconRes = R.drawable.ic_shuruq),
        PrayerItem(name = "Duhur", iconRes = R.drawable.ic_duhur),
        PrayerItem(name = "Asr", iconRes = R.drawable.ic_asr),
        PrayerItem(name = "Maghrib", iconRes = R.drawable.ic_maghrib),
        PrayerItem(name = "Isha", iconRes = R.drawable.ic_isha)
    )

    AthkarTheme {
        AllPrayers(list = list)
    }
}