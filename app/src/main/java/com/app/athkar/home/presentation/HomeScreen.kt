package com.app.athkar.home.presentation

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.app.ActivityCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import com.app.athkar.R
import com.app.athkar.core.navigation.ScreenRoute
import com.app.athkar.core.util.canScheduleExactAlarm
import com.app.athkar.home.presentation.composables.AllPrayers
import com.app.athkar.home.presentation.composables.CurrentPrayerDetails
import com.app.athkar.home.presentation.composables.LocationSelectionDialog
import com.app.athkar.ui.theme.AthkarTheme
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow


@Composable
fun HomeScreen(
    state: HomeState,
    onEvent: (HomeViewModelEvent) -> Unit = {},
    uiEvent: SharedFlow<HomeUIEvent> = MutableSharedFlow(),
    navigateTo: (String) -> Unit = {}
) {
    val context = LocalContext.current
    val lifeCycleOwner = LocalLifecycleOwner.current

    var isALlPermissionsGranted by remember {
        mutableStateOf(
            true
        )
    }

    val showDialog by remember(state.showDialog) {
        derivedStateOf {
            isALlPermissionsGranted && state.showDialog
        }
    }

    val requestPermissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            onEvent(HomeViewModelEvent.SelectAutoLocation)
        } else {
            Toast.makeText(context, "Permission Denied", Toast.LENGTH_SHORT).show()
        }
    }

    val requestNotificationsPermissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            isALlPermissionsGranted = true
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

    DisposableEffect(lifeCycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_START) {
                if (!context.canScheduleExactAlarm()) {
                    isALlPermissionsGranted = false
                    return@LifecycleEventObserver
                }
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    if (ActivityCompat.checkSelfPermission(
                            context, Manifest.permission.POST_NOTIFICATIONS
                        ) != PackageManager.PERMISSION_GRANTED
                    ) {
                        requestNotificationsPermissionLauncher.launch(
                            Manifest.permission.POST_NOTIFICATIONS
                        )
                        isALlPermissionsGranted = false
                        return@LifecycleEventObserver
                    }
                }
                isALlPermissionsGranted = true
            }
        }
        lifeCycleOwner.lifecycle.addObserver(observer)

        onDispose {
            lifeCycleOwner.lifecycle.removeObserver(observer)
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
                text = stringResource(R.string.your_location),
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
                text = stringResource(R.string.athkar),
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

        if (showDialog) {
            LocationSelectionDialog(
                state = state,
                onEvent = onEvent
            ) {
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