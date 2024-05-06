package com.app.athkar.edit_prayer.presentation.composables

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp
import com.app.athkar.edit_prayer.presentation.Prayer
import com.app.athkar.ui.theme.AthkarTheme

@Composable
fun PrayerDetailItem(
    prayer: Prayer,
    switchPrayerAlarm: () -> Unit = {}
) {
    Row(
        modifier = Modifier
            .fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            modifier = Modifier.weight(1f),
            textAlign = TextAlign.Center,
            text = prayer.name,
            color = Color.White,
            fontWeight = FontWeight.W800,
            fontSize = 14.sp
        )
        Text(
            modifier = Modifier.weight(1f),
            textAlign = TextAlign.Center,
            text = prayer.prayerTime,
            color = Color.White,
            fontWeight = FontWeight.W800,
            fontSize = 14.sp
        )
        Text(
            modifier = Modifier.weight(1f),
            textAlign = TextAlign.Center,
            text = prayer.iqamaTime,
            color = Color.White,
            fontWeight = FontWeight.W800,
            fontSize = 14.sp
        )
        Box(modifier = Modifier.weight(1f), contentAlignment = Alignment.Center) {
            Switch(checked = prayer.isAlarmEnabled, onCheckedChange = { switchPrayerAlarm() })
        }
    }
}


@Preview
@Composable
private fun PrayerDetailItemPreview() {
    val prayer = Prayer(
        "Fajr", "4:15", "5:15", true
    )
    AthkarTheme {
        PrayerDetailItem(prayer)
    }
}