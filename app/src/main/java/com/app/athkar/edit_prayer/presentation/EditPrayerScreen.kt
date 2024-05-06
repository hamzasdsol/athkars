package com.app.athkar.edit_prayer.presentation

import android.os.Build
import android.os.Build.VERSION_CODES.S
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.app.athkar.R
import com.app.athkar.core.navigation.ScreenRoute
import com.app.athkar.core.ui.AppToolbar
import com.app.athkar.ui.theme.AthkarTheme
import com.app.athkar.ui.theme.ButtonBackground
import com.app.athkar.ui.theme.PopupBackground
import com.ramcosta.composedestinations.annotation.Destination
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow

@Destination(ScreenRoute.EDIT_PRAYER)
@Composable
fun EditPrayerScreen(
    state: EditPrayerState,
    onEvent: (EditPrayerViewModelEvent) -> Unit = {},
    uiEvent: SharedFlow<EditPrayerUIEvent> = MutableSharedFlow(),
    navigateUp: () -> Unit = {}
) {
    val context = LocalContext.current
    LaunchedEffect(Unit) {
        uiEvent.collect { uiEvent ->
            when (uiEvent) {
                is EditPrayerUIEvent.ShowMessage -> {
                    Toast.makeText(context, uiEvent.message, Toast.LENGTH_SHORT).show()
                }

            }
        }

    }

    val prayerAlarmPreferences = remember(context) {
        mutableStateOf(Pair(PrayerName.FAJR, false))
    }

    val permissions = remember(context) { arrayOf<String>() }

    val requestPermissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { p ->
        if (p.all { it.value }) {
            onEvent(
                EditPrayerViewModelEvent.SetPrayerAlarmPreference(
                    prayerAlarmPreferences.value.first,
                    prayerAlarmPreferences.value.second
                )
            )
        } else {
            Toast.makeText(context, "Permission Denied", Toast.LENGTH_SHORT).show()
        }
    }


    Box(
        modifier = Modifier
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
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            AppToolbar(title = "Prayer time", leftIcon = {
                Icon(
                    modifier = Modifier.clickable {
                        navigateUp()
                    },
                    painter = painterResource(id = R.drawable.ic_back),
                    tint = Color.White,
                    contentDescription = "back icon"
                )
            })


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
                PrayersDetails(state.prayers) { key, value ->
                    if (value && Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                        permissions.plus(android.Manifest.permission.POST_NOTIFICATIONS)
                    }
                    if (Build.VERSION.SDK_INT >= S) {
                        permissions.plus(android.Manifest.permission.SCHEDULE_EXACT_ALARM)
                        requestPermissionLauncher.launch(permissions)
                    }

                    prayerAlarmPreferences.value = Pair(key, value)
                }

                Spacer(modifier = Modifier.height(32.dp))

                Box(modifier = Modifier
                    .clickable { navigateUp() }
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
                    .background(ButtonBackground)
                    .padding(16.dp),
                    contentAlignment = Alignment.Center) {
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