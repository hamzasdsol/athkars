package com.app.athkar.athkar_list.presentation.composables

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.app.athkar.R
import com.app.athkar.ui.theme.AthkarTheme
import com.app.athkar.ui.theme.ControlsColor
import com.app.athkar.ui.theme.PagerControlsBackground

@Composable
fun PagerControls(
    onPlayTap: () -> Unit = {},
    onNextTap: () -> Unit = {},
    onBackTap: () -> Unit = {}
) {

    Row(
        modifier = Modifier
            .clip(RoundedCornerShape(12.dp))
            .background(PagerControlsBackground)
            .padding(16.dp),
        horizontalArrangement = Arrangement.spacedBy(24.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {

        Text(
            modifier = Modifier.clickable {
                onBackTap()
            },
            text = "Back",
            color = ControlsColor,
            fontSize = 16.sp,
            fontWeight = FontWeight.W400
        )

        Icon(
            modifier = Modifier.clickable {
                onPlayTap()
            },
            painter = painterResource(id = R.drawable.ic_play),
            tint = ControlsColor,
            contentDescription = "play icon"
        )

        Text(
            modifier = Modifier.clickable {
                onNextTap()
            },
            text = "Next",
            color = ControlsColor,
            fontSize = 16.sp,
            fontWeight = FontWeight.W400
        )

    }

}


@Preview
@Composable
private fun PagerControlsPreview() {
    AthkarTheme {
        PagerControls()
    }
}