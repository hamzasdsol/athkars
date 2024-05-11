package com.app.athkar.home.presentation.composables

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.app.athkar.R
import com.app.athkar.home.presentation.HomeState
import com.app.athkar.home.presentation.HomeViewModelEvent
import com.app.athkar.ui.theme.ButtonBackground
import com.app.athkar.ui.theme.PopupBackground

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LocationSelectionDialog(
    state: HomeState = HomeState(),
    onEvent: (HomeViewModelEvent) -> Unit = {},
    permissionLauncher: () -> Unit
) {

    val context = LocalContext.current

    BasicAlertDialog(
        modifier = Modifier.fillMaxWidth(),
        onDismissRequest = { onEvent(HomeViewModelEvent.DismissDialog) }
    ) {
        Column(
            modifier = Modifier
                .clip(
                    RoundedCornerShape(8.dp)
                )
                .background(ButtonBackground),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(4.dp),
                text = stringResource(R.string.select_your_location),
                color = Color.Black,
                textAlign = TextAlign.Center,
                fontWeight = FontWeight.W700,
                fontSize = 20.sp
            )
            Column(
                modifier = Modifier
                    .clip(
                        RoundedCornerShape(bottomEnd = 8.dp, bottomStart = 8.dp)
                    )
                    .background(PopupBackground)
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                Text(
                    text = stringResource(R.string.select_your_location_from_the_list_or_give_us_location_access),
                    textAlign = TextAlign.Center,
                    color = Color.White,
                    fontSize = 16.sp
                )

                SelectCityDropDown(cities = state.cities) {
                    onEvent(HomeViewModelEvent.UpdateLocation(it))
                }

                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .width(150.dp)
                        .border(1.dp, Color.White, RoundedCornerShape(8.dp))
                        .padding(16.dp)
                        .clickable {
                            if (state.location.isNotBlank()) {
                                onEvent(HomeViewModelEvent.DismissDialog)
                                onEvent(HomeViewModelEvent.SelectManualLocation)
                            } else
                                Toast
                                    .makeText(
                                        context,
                                        "Please select a location",
                                        Toast.LENGTH_SHORT
                                    )
                                    .show()
                        }
                ) {
                    Text(
                        text = stringResource(R.string.done),
                        color = Color.White,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.W800
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(
                            RoundedCornerShape(12.dp)
                        )
                        .border(1.dp, Color.White)
                        .background(ButtonBackground)
                        .padding(16.dp)
                        .clickable {
                            permissionLauncher()
                        }
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.ic_location),
                        colorFilter = ColorFilter.tint(color = PopupBackground),
                        contentDescription = "Location"
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = stringResource(R.string.auto),
                        color = PopupBackground,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.W800
                    )
                }
            }
        }
    }
}