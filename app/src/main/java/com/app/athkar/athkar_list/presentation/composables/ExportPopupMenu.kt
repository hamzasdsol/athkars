package com.app.athkar.athkar_list.presentation.composables

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.material.Divider
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.MenuDefaults
import androidx.compose.material3.MenuItemColors
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.window.PopupProperties
import com.app.athkar.ui.theme.AthkarTheme
import com.app.athkar.ui.theme.ControlsColor

@Composable
fun ExportPopupMenu(
    expanded: Boolean = false,
    onImageTap: () -> Unit = {},
    onVideoTap: () -> Unit = {},
    setExpanded: (Boolean) -> Unit = {}
) {

    val interactionSource = remember {
        MutableInteractionSource()
    }

    DropdownMenu(
        modifier = Modifier.background(Color.White),
        expanded = expanded,
        onDismissRequest = { setExpanded(false) }
    ) {
        DropdownMenuItem(
            modifier = Modifier.clickable(enabled = false, interactionSource = interactionSource, indication = null) {},
            text = {  Text("Export as") },
            onClick = { /* Do nothing! */ },
            colors = MenuDefaults.itemColors(textColor = ControlsColor)
        )
        Divider()
        DropdownMenuItem(
            text = { Text("Image") },
            onClick = { onImageTap() },
            colors = MenuDefaults.itemColors(textColor = ControlsColor)
        )
        Divider()
        DropdownMenuItem(
            text = { Text("Video") },
            onClick = { onVideoTap() },
            colors = MenuDefaults.itemColors(textColor = ControlsColor)
        )
    }
}

@Preview
@Composable
private fun ExportPopupMenuPreview() {
    AthkarTheme {
        ExportPopupMenu(true)
    }
}