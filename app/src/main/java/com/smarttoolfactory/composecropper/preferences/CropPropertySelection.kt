package com.smarttoolfactory.composecropper.preferences

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.smarttoolfactory.cropper.CropProperties
import com.smarttoolfactory.cropper.CropType
import com.smarttoolfactory.cropper.model.AspectRatio
import com.smarttoolfactory.cropper.model.AspectRatioModel
import com.smarttoolfactory.cropper.model.aspectRatios
import com.smarttoolfactory.cropper.widget.AspectRatioSelectionList
import kotlin.math.roundToInt


@Composable
internal fun CropPropertySelectionMenu(
    cropProperties: CropProperties,
    onCropPropertiesChange: (CropProperties) -> Unit
) {
    // Crop properties
    val cropType = cropProperties.cropType
    val aspectRatio = cropProperties.aspectRatio
    val handleSize = cropProperties.handleSize
    val contentScale = cropProperties.contentScale
    val shape = cropProperties.shape

    Title("Crop Type")
    CropTypeDialogSelection(
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
            onCropPropertiesChange(
                cropProperties.copy(aspectRatio = it.aspectRatio)
            )
        }
    )

    Title("Content Scale")
    ContentScaleDialogSelection(contentScale) {
        onCropPropertiesChange(
            cropProperties.copy(contentScale = it)
        )
    }

    // Handle size and overlay size applies only to Dynamic crop
    if(cropType == CropType.Dynamic){
        Title("Handle Size")
        DpSliderSelection(
            value = handleSize,
            onValueChange = {
                onCropPropertiesChange(
                    cropProperties.copy(handleSize = it)
                )
            },
            lowerBound = 10.dp,
            upperBound = 60.dp
        )
    }
}

@Composable
internal fun CropGestureSelectionMenu(
    cropProperties: CropProperties,
    onCropPropertiesChange: (CropProperties) -> Unit
) {
    // Gestures
    val flingEnabled = cropProperties.fling
    val pannable = cropProperties.pannable
    val zoomable = cropProperties.zoomable
    val maxZoom = cropProperties.maxZoom

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

            Title("Max Zoom ${maxZoom}x", fontSize = 16.sp)
            MaxZoomSelection(
                maxZoom = maxZoom,
                onMaxZoomChange = {

                    val max = (it * 100f).roundToInt() / 100f
                    onCropPropertiesChange(
                        cropProperties.copy(maxZoom = max)
                    )
                },
                valueRange = 1f..10f
            )
        }
    }

}

@Composable
internal fun AspectRatioSelection(
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
internal fun FlingEnableSelection(
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
internal fun PanEnableSelection(
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
internal fun ZoomEnableSelection(
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
internal fun MaxZoomSelection(
    maxZoom: Float,
    onMaxZoomChange: (Float) -> Unit,
    valueRange: ClosedFloatingPointRange<Float>
) {
    SliderSelection(
        value = maxZoom,
        onValueChange = onMaxZoomChange,
        valueRange = valueRange
    )
}