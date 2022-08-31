package com.smarttoolfactory.cropper

import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.Color


/**
 * Contains the default values used by [ImageCropper]
 */
object CropDefaults {

    fun properties(
        initialZoom: Float = 1f,
        minZoom: Float = 1f,
        maxZoom: Float = 5f,
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
        drawInnerOverlay: Boolean = true,
        overlayColor: Color = Color.White
    ): CropStyle {
        return CropStyle(
            cropType = cropType,
            drawOverlay = drawOverlay,
            drawInnerOverlay = drawInnerOverlay,
            overlayColor = overlayColor
        )
    }
}

/**
 * Data class for cropper styling
 */
@Immutable
data class CropStyle internal constructor(
    val cropType: CropType = CropType.Dynamic,
    val drawOverlay: Boolean = true,
    val drawInnerOverlay: Boolean = true,
    val overlayColor: Color = Color.White
)

/**
 * Data class for selecting cropper properties
 */
@Immutable
data class CropProperties internal constructor(
    val initialZoom: Float = 1f,
    val minZoom: Float = 1f,
    val maxZoom: Float = 5f,
    val fling: Boolean = false,
    val moveToBounds: Boolean = true,
    val zoomable: Boolean = true,
    val pannable: Boolean = true,
    val rotatable: Boolean = false,
    val limitPan: Boolean = false
)