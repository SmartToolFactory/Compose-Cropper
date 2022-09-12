package com.smarttoolfactory.composecropper

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.smarttoolfactory.cropper.CropProperties
import com.smarttoolfactory.cropper.CropStyle
import com.smarttoolfactory.cropper.CropType
import com.smarttoolfactory.cropper.model.AspectRatio
import com.smarttoolfactory.cropper.model.AspectRatioModel
import com.smarttoolfactory.cropper.model.aspectRatios
import com.smarttoolfactory.cropper.widget.AspectRatioSelectionList

@Composable
internal fun PropertySelectionSheet(
    cropProperties: CropProperties,
    onCropPropertiesChange: (CropProperties) -> Unit,
    cropStyle: CropStyle,
    onCropStyleChange: (CropStyle) -> Unit
) {

    // Crop properties
    val cropType = cropProperties.cropType
    val aspectRatio = cropProperties.aspectRatio
    val handleSize = cropProperties.handleSize
    val minOverlaySize = cropProperties.minOverlaySize
    val flingEnabled = cropProperties.fling
    val pannable = cropProperties.pannable
    val zoomable = cropProperties.zoomable
    val minZoom = cropProperties.minZoom
    val maxZoom = cropProperties.maxZoom

    // Crop Style
    val drawOverlayEnabled = cropStyle.drawOverlay
    val overlayStrokeWidth = cropStyle.strokeWidth
    val overlayColor = cropStyle.overlayColor
    val drawGridEnabled = cropStyle.drawGrid


    Column {
        Box(
            modifier = Modifier
                .padding(vertical = 16.dp)
                .fillMaxWidth(),
            contentAlignment = Alignment.Center
        ) {
            Box(
                modifier = Modifier
                    .width(30.dp)
                    .height(5.dp)
                    .background(Color.LightGray, RoundedCornerShape(50))
            )
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .height(400.dp)
                .padding(horizontal = 16.dp, vertical = 8.dp)
                .verticalScroll(rememberScrollState())
        ) {

            Title("Crop Type")
            CropTypeSelection(
                cropType = cropType,
                onCropTypeChange = { cropTypeChange ->
                    onCropPropertiesChange(
                        cropProperties.copy(cropType = cropTypeChange)
                    )
                }
            )

            Title("Aspect Ratio")
            AspectRatioSelection(
                aspectRatio = aspectRatio,
                onAspectRatioChange = {

                    val newProp = cropProperties.copy(aspectRatio = it.aspectRatio)
                    onCropPropertiesChange(
                        newProp
                    )
                }
            )

            Title("Content Scale")
            Text("Under Development")

            // Handle size and overlay size applies only to Dynamic crop
            Title("Handle Size")
            HandleSizeSelection(
                handleSize = cropProperties.handleSize
            ) {

            }

            Title("Minimum Crop Size")
            Text("Under Development")

            Title("Overlay")
            DrawOverlaySelection(
                drawOverlayEnabled = drawOverlayEnabled,
                onDrawOverlayEnabled = {
                    onCropStyleChange(
                        cropStyle.copy(drawOverlay = it)
                    )
                }
            )

            AnimatedVisibility(visible = drawOverlayEnabled) {
                Column {
                    Title("StrokeWidth", fontSize = 16.sp)
                    Text("Under Development")

                    Title("Color", fontSize = 16.sp)
                    Text("Under Development")

                    Title("Grid")
                    DrawGridSelection(
                        drawGridEnabled = drawGridEnabled,
                        onDrawGridEnabled = {
                            onCropStyleChange(
                                cropStyle.copy(drawGrid = it)
                            )
                        }
                    )
                }
            }

            Title("Fling")
            FlingEnableSelection(
                flingEnabled = flingEnabled,
                onFlingEnabledChange = {
                    onCropPropertiesChange(
                        cropProperties.copy(fling = it)
                    )
                }
            )
            Title("Pan Enabled")
            PanEnableSelection(
                panEnabled = pannable,
                onPanEnabledChange = {
                    onCropPropertiesChange(
                        cropProperties.copy(pannable = it)
                    )
                }
            )

            Title("Zoom Enabled")
            ZoomEnableSelection(
                zoomEnabled = zoomable,
                onZoomEnabledChange = {
                    onCropPropertiesChange(
                        cropProperties.copy(zoomable = it)
                    )
                }
            )

            AnimatedVisibility(visible = zoomable) {
                Column {
                    Title("Min Zoom", fontSize = 16.sp)
                    Text("Under Development")

                    Title("Max Zoom", fontSize = 16.sp)
                    Text("Under Development")
                }
            }

        }
    }
}

@Composable
private fun CropTypeSelection(
    cropType: CropType,
    onCropTypeChange: (CropType) -> Unit
) {
    val cropTypeOptions =
        listOf(CropType.Dynamic.toString(), CropType.Static.toString())

    val index = when (cropType) {
        CropType.Dynamic -> 0
        else -> 1
    }

    ExposedSelectionMenu(
        modifier = Modifier.fillMaxWidth(),
        index = index,
        title = "Crop Type",
        options = cropTypeOptions,
        onSelected = {
            val newCropType = when (it) {
                0 -> CropType.Dynamic
                else -> CropType.Static
            }

            onCropTypeChange(newCropType)
        }
    )
}

@Composable
private fun AspectRatioSelection(
    aspectRatio: AspectRatio,
    onAspectRatioChange: (AspectRatioModel) -> Unit
) {
    val aspectRatios = aspectRatios
    val indexOf = aspectRatios.firstOrNull { it.aspectRatio.value == aspectRatio.value } ?: 0

    AspectRatioSelectionList(
        onSelectedItemChange = {
            onAspectRatioChange(aspectRatios[it])
        }
    )
}

@Composable
private fun HandleSizeSelection(
    handleSize: Dp,
    onHandleSizeChange: (Dp) -> Unit
) {

}

@Composable
private fun MinimumCropSizeSelection(
    cropType: CropType,
    onCropTypeChange: (CropType) -> Unit
) {

}

@Composable
private fun DrawOverlaySelection(
    drawOverlayEnabled: Boolean,
    onDrawOverlayEnabled: (Boolean) -> Unit
) {
    FullRowSwitch(
        label = "Draw overlay",
        state = drawOverlayEnabled,
        onStateChange = onDrawOverlayEnabled
    )
}

@Composable
private fun OverlayStrokeWidthSelection(
    strokeWidth: Dp,
    onStrokeWidthChange: (Dp) -> Unit
) {

}

@Composable
private fun OverlayColorSelection(
    cropType: CropType,
    onCropTypeChange: (CropType) -> Unit
) {

}

@Composable
private fun DrawGridSelection(
    drawGridEnabled: Boolean,
    onDrawGridEnabled: (Boolean) -> Unit
) {
    FullRowSwitch(
        label = "Draw grid",
        state = drawGridEnabled,
        onStateChange = onDrawGridEnabled
    )
}

@Composable
private fun ContentScaleSelection(
    contentScale: ContentScale,
    onContentScaleChange: (ContentScale) -> Unit
) {

}


@Composable
private fun FlingEnableSelection(
    flingEnabled: Boolean,
    onFlingEnabledChange: (Boolean) -> Unit
) {
    FullRowSwitch(
        label = "Enable fling gesture",
        state = flingEnabled,
        onStateChange = onFlingEnabledChange
    )

}

@Composable
private fun PanEnableSelection(
    panEnabled: Boolean,
    onPanEnabledChange: (Boolean) -> Unit
) {
    FullRowSwitch(
        label = "Enable pan gesture",
        state = panEnabled,
        onStateChange = onPanEnabledChange
    )

}

@Composable
private fun ZoomEnableSelection(
    zoomEnabled: Boolean,
    onZoomEnabledChange: (Boolean) -> Unit
) {
    FullRowSwitch(
        label = "Enable zoom gesture",
        state = zoomEnabled,
        onStateChange = onZoomEnabledChange
    )
}

@Composable
private fun MinimumZoomSelection(
    minZoom: Float,
    onMinZoomChange: (Float) -> Unit
) {

}

@Composable
private fun MaxZoomSelection(
    maxZoom: Float,
    onMaxZoomChange: (Float) -> Unit
) {

}


@Composable
private fun Title(
    text: String,
    fontSize: TextUnit = 20.sp
) {
    Text(
        modifier = Modifier.padding(vertical = 1.dp),
        text = text,
        color = MaterialTheme.colorScheme.primary,
        fontSize = fontSize,
        fontWeight = FontWeight.Bold
    )
}


@Composable
private fun FullRowSwitch(
    label: String,
    state: Boolean,
    onStateChange: (Boolean) -> Unit
) {

    // Checkbox with text on right side
    Row(modifier = Modifier
        .fillMaxWidth()
        .clickable(
            interactionSource = remember { MutableInteractionSource() },
            indication = null,
            role = Role.Switch,
            onClick = {
                onStateChange(!state)
            }
        )
        .padding(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {

        Text(text = label, modifier = Modifier.weight(1f))

        Switch(
            checked = state,
            onCheckedChange = null
        )
    }
}
