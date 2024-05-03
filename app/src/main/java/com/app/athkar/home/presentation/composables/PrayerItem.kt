package com.app.athkar.home.presentation.composables

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.app.athkar.R
import com.app.athkar.home.presentation.PrayerItem
import com.app.athkar.ui.theme.AthkarTheme
import com.app.athkar.ui.theme.PrayerIconBorder

@Composable
fun PrayerItem(
    item: PrayerItem
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        
        val borderColor = if (item.isSelected) PrayerIconBorder else Color.Transparent

        Box(
            modifier = Modifier
                .size(48.dp)
                .clip(RoundedCornerShape(8.dp))
                .border(1.dp, color = borderColor, shape = RoundedCornerShape(8.dp))
                .background(Color.White)
                .padding(8.dp)
        ) {
            Image(
                painter = painterResource(id = item.iconRes),
                contentDescription = "prayer icon"
            )
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = item.name,
            fontWeight = FontWeight.W400,
            fontSize = 12.sp
        )
    }
}

@Preview
@Composable
private fun PrayerItemPreview() {
    val item = PrayerItem(name = "Fajr", R.drawable.ic_fajr, isSelected = false)
    AthkarTheme {
        Column {
            PrayerItem(item)
            PrayerItem(item.copy(isSelected = true))
        }
    }
}