package com.app.athkar.core.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.app.athkar.R
import com.app.athkar.ui.theme.AthkarTheme

@Composable
fun AppToolbar(
    title: String,
    leftIcon: @Composable () -> Unit = {},
    rightIcon: @Composable () -> Unit = {}
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        contentAlignment = Alignment.CenterStart
    ) {
        leftIcon()
        Text(
            modifier = Modifier.align(Alignment.Center),
            text = title,
            color = Color.White,
            fontSize = 18.sp,
            fontWeight = FontWeight.W800
        )
    }
}

@Preview
@Composable
private fun AppToolbarPreview() {
    AthkarTheme {
        AppToolbar(
            title = "App toolbar title",
            leftIcon = {
                Icon(
                    painter = painterResource(id = R.drawable.ic_back),
                    tint = Color.White,
                    contentDescription = "back"
                )
            }
        )
    }
}