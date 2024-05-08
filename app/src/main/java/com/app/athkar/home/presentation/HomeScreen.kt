package com.app.athkar.home.presentation

import android.Manifest
import android.content.pm.PackageManager
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.app.ActivityCompat
import com.app.athkar.R
import com.app.athkar.core.navigation.ScreenRoute
import com.app.athkar.edit_prayer.presentation.EditPrayerViewModelEvent
import com.app.athkar.home.presentation.composables.AllPrayers
import com.app.athkar.home.presentation.composables.CurrentPrayerDetails
import com.app.athkar.home.presentation.composables.SelectCityDropDown
import com.app.athkar.ui.theme.AthkarTheme
import com.app.athkar.ui.theme.ButtonBackground
import com.app.athkar.ui.theme.PopupBackground
import com.ramcosta.composedestinations.annotation.Destination
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow


@Destination(ScreenRoute.HOME)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    state: HomeState,
    onEvent: (HomeViewModelEvent) -> Unit = {},
    uiEvent: SharedFlow<HomeUIEvent> = MutableSharedFlow(),
    navigateTo: (String) -> Unit = {}
) {
    val context = LocalContext.current


    val requestPermissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            onEvent(HomeViewModelEvent.SelectAutoLocation)
        } else {
            Toast.makeText(context, "Permission Denied", Toast.LENGTH_SHORT).show()
        }
    }

    LaunchedEffect(Unit) {
        uiEvent.collect { event ->
            when (event) {
                is HomeUIEvent.ShowMessage -> {
                    Toast.makeText(
                        context,
                        event.message,
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }

    Box(
        modifier =
        Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        Image(
            modifier = Modifier.fillMaxWidth(),
            painter = painterResource(id = R.drawable.home_background),
            contentDescription = "background",
            contentScale = ContentScale.FillWidth,
        )
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .padding(top = 32.dp)
        ) {
            Text(
                text = "Your location",
                color = Color.White,
                fontStyle = FontStyle.Italic,
                fontWeight = FontWeight.SemiBold,
                fontSize = 12.sp
            )
            Text(
                text = state.location,
                color = Color.White,
                fontStyle = FontStyle.Italic,
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp
            )

            Spacer(modifier = Modifier.height(32.dp))

            CurrentPrayerDetails(state.currentPrayer)

            Spacer(modifier = Modifier.height(32.dp))

            AllPrayers(list = list, navigate = { navigateTo(ScreenRoute.EDIT_PRAYER) })

            Spacer(modifier = Modifier.height(32.dp))

            Text(
                text = "Athkar",
                fontWeight = FontWeight.W700,
                color = Color.Black
            )

            Image(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        navigateTo(ScreenRoute.ATHKAR_LIST)
                    },
                painter = painterResource(id = R.drawable.clouds),
                contentDescription = "clouds"
            )
        }

        if (state.showDialog) {
            BasicAlertDialog(
                modifier = Modifier.fillMaxWidth(),
                onDismissRequest = { onEvent(HomeViewModelEvent.UpdateShowDialog(false)) }
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
                        text = "Select your location",
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
                            text = "Select your location from the list or give us location access",
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
                                        onEvent(HomeViewModelEvent.UpdateShowDialog(false))
                                        onEvent(HomeViewModelEvent.SelectAutoLocation)
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
                                text = "Done",
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
                                    if (ActivityCompat.checkSelfPermission(
                                            context,
                                            Manifest.permission.ACCESS_COARSE_LOCATION
                                        ) != PackageManager.PERMISSION_GRANTED
                                    ) {
                                        requestPermissionLauncher.launch(
                                            Manifest.permission.ACCESS_COARSE_LOCATION
                                        )
                                    } else {
                                        onEvent(HomeViewModelEvent.SelectAutoLocation)
                                    }
                                }
                        ) {
                            Image(
                                painter = painterResource(id = R.drawable.ic_location),
                                colorFilter = ColorFilter.tint(color = PopupBackground),
                                contentDescription = "Location"
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Auto",
                                color = PopupBackground,
                                fontSize = 20.sp,
                                fontWeight = FontWeight.W800
                            )
                        }
                    }
                }
            }
        }
    }

}


@Preview
@Composable
private fun HomeScreenPreview() {
    AthkarTheme {
        HomeScreen(state = HomeState())
    }
}