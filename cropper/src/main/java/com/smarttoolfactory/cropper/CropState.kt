package com.smarttoolfactory.cropper

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp


/**
 * Create and [remember] the [CropState] based on the currently appropriate transform
 * configuration to allow changing pan, zoom, and rotation.
 *
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
    initialZoom: Float = 1f,
    minZoom: Float = 1f,
    maxZoom: Float = 5f,
    cropType: CropType = CropType.Dynamic,
    touchRegionSize: Dp = 20.dp,
    minDimension: Dp = 40.dp,
    fling: Boolean = false,
    moveToBounds: Boolean = true,
    zoomable: Boolean = true,
    pannable: Boolean = true,
    rotatable: Boolean = false,
    limitPan: Boolean = false,
    vararg keys: Any?
): CropState {
    val density = LocalDensity.current

    return remember(*keys) {
        when (cropType) {
            CropType.Static -> {
                StaticCropState(
                    imageSize = imageSize,
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
            else-> {

                DynamicCropState(
                    imageSize = imageSize,
                    initialZoom = initialZoom,
                    minZoom = minZoom,
                    maxZoom = maxZoom,
                    touchRegionSize = 100f,
                    minDimension = 200f,
                    fling = fling,
                    moveToBounds = moveToBounds,
                    zoomable = zoomable,
                    pannable = pannable,
                    rotatable = rotatable,
                    limitPan = limitPan
                )
            }
        }
    }
}

enum class CropType {
    Static, Dynamic
}