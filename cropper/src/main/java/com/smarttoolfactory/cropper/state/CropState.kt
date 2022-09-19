package com.smarttoolfactory.cropper.state

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.IntSize
import com.smarttoolfactory.cropper.CropProperties
import com.smarttoolfactory.cropper.CropType


/**
 * Create and [remember] the [CropState] based on the currently appropriate transform
 * configuration to allow changing pan, zoom, and rotation.
 * @param imageSize size of the **Bitmap**
 * @param containerSize size of the Composable that draws **Bitmap**
 * @param cropProperties wrapper class that contains crop state properties such as
 * crop type,
 * @param keys are used to reset remember block to initial calculations. This can be used
 * when image, contentScale or any property changes which requires values to be reset to initial
 * values
 */
@Composable
fun rememberCropState(
    imageSize: IntSize,
    containerSize: IntSize,
    drawAreaSize: IntSize,
    cropProperties: CropProperties,
    vararg keys: Any?
): CropState {

    // Properties of crop state
    val handleSize = cropProperties.handleSize
    val minOverlaySize = handleSize * 2
    val cropType = cropProperties.cropType
    val aspectRatio = cropProperties.aspectRatio
    val maxZoom = cropProperties.maxZoom
    val fling = cropProperties.fling
    val zoomable = cropProperties.zoomable
    val pannable = cropProperties.pannable
    val rotatable = cropProperties.rotatable

    val handleSizeInPx: Float
    val minOverlaySizePx: Float

    with(LocalDensity.current) {
        handleSizeInPx = handleSize.toPx()
        minOverlaySizePx = minOverlaySize.toPx()
    }

    return remember(*keys) {
        when (cropType) {
            CropType.Static -> {
                StaticCropState(
                    imageSize = imageSize,
                    containerSize = containerSize,
                    drawAreaSize = drawAreaSize,
                    aspectRatio = aspectRatio,
                    maxZoom = maxZoom,
                    fling = fling,
                    zoomable = zoomable,
                    pannable = pannable,
                    rotatable = rotatable,
                    limitPan = false
                )
            }
            else -> {

                DynamicCropState(
                    imageSize = imageSize,
                    containerSize = containerSize,
                    drawAreaSize = drawAreaSize,
                    aspectRatio = aspectRatio,
                    maxZoom = maxZoom,
                    handleSize = handleSizeInPx,
                    minOverlaySize = minOverlaySizePx,
                    fling = fling,
                    zoomable = zoomable,
                    pannable = pannable,
                    rotatable = rotatable,
                    limitPan = true
                )
            }
        }
    }
}
