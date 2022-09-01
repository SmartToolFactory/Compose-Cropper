package com.smarttoolfactory.cropper

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp


/**
 * Create and [remember] the [CropState] based on the currently appropriate transform
 * configuration to allow changing pan, zoom, and rotation.
 * @param imageSize size of the [ImageBitmap]
 * @param containerSize size of the Composable that draws [ImageBitmap]
 * @param initialZoom zoom set initially
 * @param minZoom minimum zoom value
 * @param maxZoom maximum zoom value
 * @param fling when set to true dragging pointer builds up velocity. When last
 * pointer leaves Composable a movement invoked against friction till velocity drops below
 * to threshold
 * @param moveToBounds when set to true if image zoom is lower than initial zoom or
 * panned out of image boundaries moves back to bounds with animation.
 * @param zoomable when set to true zoom is enabled
 * @param pannable when set to true pan is enabled
 * @param rotatable when set to true rotation is enabled
 * @param limitPan limits pan to bounds of parent Composable. Using this flag prevents creating
 * empty space on sides or edges of parent
 * @param keys are used to reset remember block to initial calculations. This can be used
 * when image, contentScale or any property changes which requires values to be reset to initial
 * values
 */
@Composable
fun rememberCropState(
    imageSize: IntSize,
    containerSize: IntSize,
    initialZoom: Float = 1f,
    minZoom: Float = 1f,
    maxZoom: Float = 5f,
    cropType: CropType = CropType.Dynamic,
    handleSize: Dp = 40.dp,
    minCropSize: Dp = 100.dp,
    fling: Boolean = false,
    moveToBounds: Boolean = true,
    zoomable: Boolean = true,
    pannable: Boolean = true,
    rotatable: Boolean = false,
    limitPan: Boolean = false,
    vararg keys: Any?
): CropState {
    val density = LocalDensity.current
    val handleSizeInPx: Float
    val minCropSizePx: Float

    with(density) {
        handleSizeInPx = handleSize.toPx()
        minCropSizePx = minCropSize.toPx()
    }

    return remember(*keys) {
        when (cropType) {
            CropType.Static -> {
                StaticCropState(
                    imageSize = imageSize,
                    containerSize = containerSize,
                    initialZoom = initialZoom,
                    minZoom = minZoom,
                    maxZoom = maxZoom,
                    fling = fling,
                    moveToBounds = moveToBounds,
                    zoomable = zoomable,
                    pannable = pannable,
                    rotatable = rotatable,
                    limitPan = limitPan
                )
            }
            else -> {

                DynamicCropState(
                    imageSize = imageSize,
                    containerSize = containerSize,
                    initialZoom = initialZoom,
                    minZoom = minZoom,
                    maxZoom = maxZoom,
                    handleSize = handleSizeInPx,
                    minCropSize = minCropSizePx,
                    fling = fling,
                    moveToBounds = moveToBounds,
                    zoomable = zoomable,
                    pannable = pannable,
                    rotatable = rotatable,
                    limitPan = true
                )
            }
        }
    }
}
