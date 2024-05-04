package com.app.athkar.edit_prayer.presentation

import android.widget.Toolbar
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
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
import com.app.athkar.core.ui.AppToolbar
import com.app.athkar.navigation.Destinations
import com.app.athkar.ui.theme.AthkarTheme
import com.app.athkar.ui.theme.ButtonBackground
import com.app.athkar.ui.theme.PopupBackground
import com.ramcosta.composedestinations.annotation.Destination

@Destination(Destinations.EDIT_PRAYER_ROUTE)
@Composable
fun EditPrayerScreen(
    state: EditPrayerState,
    onEvent: (EditPrayerViewModelEvent) -> Unit = {},
    navigateUp: () -> Unit = {}
) {
    Box(
        modifier =
        Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        Image(
            painter = painterResource(id = R.drawable.bg_edit_prayer),
            contentDescription = "background"
        )

        Column(
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            AppToolbar(
                title = "Prayer time",
                leftIcon = {
                    Icon(
                        modifier = Modifier.clickable {
                            navigateUp()
                        },
                        painter = painterResource(id = R.drawable.ic_back),
                        tint = Color.White,
                        contentDescription = "back icon"
                    )
                }
            )


            Box(modifier = Modifier.clip(RoundedCornerShape(8.dp))) {
                Image(
                    painter = painterResource(id = R.drawable.blue_mosque),
                    contentDescription = "mosque"
                )
            }

            Column(
                modifier = Modifier.padding(8.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                PrayersDetails(list)

                Box(
                    modifier = Modifier
                        .clickable { navigateUp() }
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .background(ButtonBackground)
                        .padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Save",
                        color = PopupBackground,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.W800
                    )
                }
            }
        }
    }
}

@Preview
@Composable
private fun EditPrayerScreenPreview() {
    AthkarTheme {
        EditPrayerScreen(state = EditPrayerState())
    }
}