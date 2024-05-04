package com.app.athkar.edit_prayer.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.app.athkar.edit_prayer.presentation.composables.PrayerDetailItem
import com.app.athkar.ui.theme.AthkarTheme
import com.app.athkar.ui.theme.ButtonBackground
import com.app.athkar.ui.theme.PopupBackground

@Composable
fun PrayersDetails(
    list: List<Prayer> = emptyList()
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(ButtonBackground),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            modifier = Modifier
                .padding(top = 16.dp, bottom = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                modifier = Modifier.weight(1.0f),
                text = "Prayer",
                textAlign = TextAlign.Center,
                color = Color.Black,
                fontWeight = FontWeight.W800,
                fontSize = 14.sp
            )
            Text(
                modifier = Modifier.weight(1.0f),
                text = "Azan",
                textAlign = TextAlign.Center,
                color = Color.Black,
                fontWeight = FontWeight.W800,
                fontSize = 14.sp
            )
            Text(
                modifier = Modifier.weight(1.0f),
                text = "Iqama",
                textAlign = TextAlign.Center,
                color = Color.Black,
                fontWeight = FontWeight.W800,
                fontSize = 14.sp
            )
            Text(
                modifier = Modifier.weight(1.0f),
                text = "Alarm",
                textAlign = TextAlign.Center,
                color = Color.Black,
                fontWeight = FontWeight.W800,
                fontSize = 14.sp
            )
        }
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(
                    RoundedCornerShape(bottomEnd = 8.dp, bottomStart = 8.dp)
                )
                .background(PopupBackground),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            LazyColumn {
                items(list) {
                    PrayerDetailItem(prayer = it)
                }
            }
        }
    }
}


@Preview
@Composable
private fun PrayersDetailsPreview() {
    AthkarTheme {
        PrayersDetails(list)
    }
}