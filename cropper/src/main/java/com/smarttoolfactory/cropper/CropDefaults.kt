package com.smarttoolfactory.cropper

import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp


/**
 * Contains the default values used by [ImageCropper]
 */
object CropDefaults {

    fun properties(
        initialZoom: Float = 1f,
        minZoom: Float = 1f,
        maxZoom: Float = 5f,
        aspectRatio: Float = 1f,
        fling: Boolean = false,
        moveToBounds: Boolean = true,
        zoomable: Boolean = true,
        pannable: Boolean = true,
        rotatable: Boolean = false,
        limitPan: Boolean = false
    ): CropProperties {
        return CropProperties(
            initialZoom = initialZoom,
            minZoom = minZoom,
            maxZoom = maxZoom,
            aspectRatio = aspectRatio,
            fling = fling,
            moveToBounds = moveToBounds,
            zoomable = zoomable,
            pannable = pannable,
            rotatable = rotatable,
            limitPan = limitPan
        )
    }

    fun style(
        cropType: CropType = CropType.Dynamic,
        drawOverlay: Boolean = true,
        drawGrid: Boolean = true,
        handleSize: Dp = 40.dp,
        minCropSize: Dp = 100.dp,
        overlayColor: Color = Color.White
    ): CropStyle {
        return CropStyle(
            cropType = cropType,
            drawOverlay = drawOverlay,
            drawGrid = drawGrid,
            handleSize = handleSize,
            minCropSize = minCropSize,
            overlayColor = overlayColor
        )
    }
}


/**
 * Data class for selecting cropper properties
 */
@Immutable
data class CropProperties internal constructor(
    val initialZoom: Float = 1f,
    val minZoom: Float = 1f,
    val maxZoom: Float = 5f,
    val aspectRatio: Float = 1f,
    val fling: Boolean = false,
    val moveToBounds: Boolean = true,
    val zoomable: Boolean = true,
    val pannable: Boolean = true,
    val rotatable: Boolean = false,
    val limitPan: Boolean = false
)

/**
 * Data class for cropper styling
 */
@Immutable
data class CropStyle internal constructor(
    val cropType: CropType = CropType.Dynamic,
    val drawOverlay: Boolean = true,
    val drawGrid: Boolean = true,
    val handleSize: Dp = 40.dp,
    val minCropSize: Dp = 100.dp,
    val overlayColor: Color = Color.White
)