package com.smarttoolfactory.composecropper.preferences

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.smarttoolfactory.cropper.settings.CropTheme

@Composable
fun CropThemeSelection(
    cropTheme: CropTheme,
    onThemeChange: (CropTheme) -> Unit
) {

    val cropThemeOptions =
        remember {
            listOf(
                CropTheme.Light.toString(),
                CropTheme.Dark.toString(),
                CropTheme.System.toString()
            )
        }

    var showDialog by remember { mutableStateOf(false) }

    val index = when (cropTheme) {
        CropTheme.Light -> 0
        CropTheme.Dark -> 1
        else -> 2
    }

    Text(
        text = cropThemeOptions[index],
        fontSize = 18.sp,
        modifier = Modifier
            .fillMaxWidth()
            .clickable {
                showDialog = true
            }
            .padding(8.dp)

    )

    if (showDialog) {
        DialogWithMultipleSelection(
            title = "Theme",
            options = cropThemeOptions,
            value = index,
            onDismiss = { showDialog = false },
            onConfirm = {

                val cropThemeChange = when (it) {
                    0 -> CropTheme.Light
                    1 -> CropTheme.Dark
                    else -> CropTheme.System
                }
                onThemeChange(cropThemeChange)
                showDialog = false
            }
        )
    }

}
