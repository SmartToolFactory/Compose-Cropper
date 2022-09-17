package com.smarttoolfactory.composecropper.preferences

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.smarttoolfactory.colorpicker.dialog.ColorPickerRingDiamondHSLDialog
import com.smarttoolfactory.cropper.CropStyle

/**
 * Crop style selection menu
 */
@Composable
internal fun CropStyleSelectionMenu(
    cropStyle: CropStyle,
    onCropStyleChange: (CropStyle) -> Unit
) {

    BaseSheet {
        val drawOverlayEnabled = cropStyle.drawOverlay
        val overlayStrokeWidth = cropStyle.strokeWidth
        val overlayColor = cropStyle.overlayColor
        val handleColor = cropStyle.handleColor
        val drawGridEnabled = cropStyle.drawGrid

        Title("Overlay")
        FullRowSwitch(
            label = "Draw overlay",
            state = drawOverlayEnabled,
            onStateChange = {
                onCropStyleChange(
                    cropStyle.copy(drawOverlay = it)
                )
            }
        )

        AnimatedVisibility(
            visible = drawOverlayEnabled
        ) {

            Column {
                Title("StrokeWidth", fontSize = 16.sp)
                DpSliderSelection(
                    value = overlayStrokeWidth,
                    onValueChange = {
                        onCropStyleChange(
                            cropStyle.copy(strokeWidth = it)
                        )
                    },
                    lowerBound = 1.dp,
                    upperBound = 4.dp
                )

                ColorSelection(
                    title = "Overlay Color",
                    color = overlayColor,
                    onColorChange = { color: Color ->
                        onCropStyleChange(
                            cropStyle.copy(overlayColor = color)
                        )
                    }
                )

                Spacer(modifier = Modifier.height(20.dp))
                ColorSelection(
                    title = "Handle Color",
                    color = handleColor,
                    onColorChange = { color: Color ->
                        onCropStyleChange(
                            cropStyle.copy(handleColor = color)
                        )
                    }
                )

                Title("Grid")
                FullRowSwitch(
                    label = "Draw grid",
                    state = drawGridEnabled,
                    onStateChange = {
                        onCropStyleChange(
                            cropStyle.copy(drawGrid = it)
                        )
                    }
                )
            }
        }
    }
}

@Composable
internal fun ColorSelection(
    title: String,
    color: Color,
    onColorChange: (Color) -> Unit
) {

    var showColorDialog by remember { mutableStateOf(false) }
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Title(title, fontSize = 16.sp)
        Spacer(modifier = Modifier.weight(1f))
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(25))
                .size(50.dp)
                .border(
                    1.dp,
                    MaterialTheme.colorScheme.primary,
                    RoundedCornerShape(25)
                )
                .background(color = color)
                .clickable {
                    showColorDialog = true
                }
        )
    }

    if (showColorDialog) {
        ColorPickerRingDiamondHSLDialog(
            initialColor = color,
            onDismiss = { colorChange: Color, _: String ->
                showColorDialog = false
                onColorChange(colorChange)
            }
        )
    }
}
