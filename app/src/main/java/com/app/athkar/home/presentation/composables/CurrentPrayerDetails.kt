package com.app.athkar.home.presentation.composables

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.app.athkar.R
import com.app.athkar.data.model.CurrentPrayerDetails
import com.app.athkar.home.presentation.HomeState
import com.app.athkar.ui.theme.AthkarTheme
import com.app.athkar.ui.theme.CardBackground

@Composable
fun CurrentPrayerDetails(
    currentPrayerDetails: CurrentPrayerDetails
) {

    Card(
        modifier = Modifier
            .fillMaxWidth(),
        shape = CardDefaults.outlinedShape,
        border = BorderStroke(1.dp, color = CardBackground)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(CardBackground.copy(alpha = 0.6f))
        ) {

            Column(modifier = Modifier
                .padding(start = 8.dp, top = 16.dp)
                ) {
                Text(
                    text = currentPrayerDetails.name,
                    color = Color.White,
                    fontStyle = FontStyle.Italic,
                    fontWeight = FontWeight.W700,
                    fontSize = 14.sp
                )
                Text(
                    text = currentPrayerDetails.time,
                    color = Color.White,
                    fontWeight = FontWeight.W800,
                    fontSize = 32.sp
                )

                Spacer(modifier = Modifier.height(16.dp))

                Column {
                    Text(
                        text = "Next prayer: ${currentPrayerDetails.nextPrayer}",
                        color = Color.White,
                        fontWeight = FontWeight.W500,
                        fontSize = 12.sp
                    )
                    Text(
                        text = currentPrayerDetails.nextPrayerTime,
                        color = Color.White,
                        fontWeight = FontWeight.W800,
                        fontSize = 16.sp
                    )
                }

                Spacer(modifier = Modifier.height(32.dp))
            }

            Image(
                modifier = Modifier.align(Alignment.BottomEnd),
                painter = painterResource(id = R.drawable.masjid),
                contentDescription = "masjid"
            )
        }
    }
}


@Preview
@Composable
private fun CurrentPrayerDetailsPreview() {
    AthkarTheme {
        CurrentPrayerDetails(HomeState().currentPrayer)
    }
}