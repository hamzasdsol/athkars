package com.app.athkar.home.presentation

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.app.athkar.R
import com.app.athkar.home.presentation.composables.AllPrayers
import com.app.athkar.home.presentation.composables.CurrentPrayerDetails
import com.app.athkar.ui.theme.AthkarTheme


@Composable
fun HomeScreen(
    state: HomeState,
    onEvent: (HomeViewModelEvent) -> Unit = {}
) {
    Box(
        modifier =
        Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        Image(
            painter = painterResource(id = R.drawable.home_background),
            contentDescription = "background"
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
                text = "Green Campus, Central University, Ganderbal",
                color = Color.White,
                fontStyle = FontStyle.Italic,
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp
            )

            Spacer(modifier = Modifier.height(32.dp))

            CurrentPrayerDetails()

            Spacer(modifier = Modifier.height(32.dp))

            AllPrayers(list = list)

            Spacer(modifier = Modifier.height(32.dp))

            Text(
                text = "Athkar",
                fontWeight = FontWeight.W700
            )

            Image(
                modifier = Modifier.fillMaxWidth(),
                painter = painterResource(id = R.drawable.clouds),
                contentDescription = "clouds"
            )
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