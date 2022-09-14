package com.smarttoolfactory.cropper

import androidx.compose.runtime.Immutable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.smarttoolfactory.cropper.model.AspectRatio
import com.smarttoolfactory.cropper.state.CropState

/**
 * Contains the default values used by [ImageCropper]
 */
object CropDefaults {

    fun properties(
        cropType: CropType = CropType.Dynamic,
        handleSize: Dp = 30.dp,
        minOverlaySize: Dp = 70.dp,
        maxZoom: Float = 5f,
        aspectRatio: AspectRatio = AspectRatio.Unspecified,
        fling: Boolean = false,
        zoomable: Boolean = true,
        pannable: Boolean = true,
        rotatable: Boolean = false
    ): CropProperties {
        return CropProperties(
            cropType = cropType,
            handleSize = handleSize,
            minOverlaySize = minOverlaySize,
            maxZoom = maxZoom,
            aspectRatio = aspectRatio,
            fling = fling,
            zoomable = zoomable,
            pannable = pannable,
            rotatable = rotatable
        )
    }

    fun style(
        drawOverlay: Boolean = true,
        drawGrid: Boolean = true,
        strokeWidth: Dp = 2.dp,
        overlayColor: Color = Color.White
    ): CropStyle {
        return CropStyle(
            drawOverlay = drawOverlay,
            drawGrid = drawGrid,
            strokeWidth = strokeWidth,
            overlayColor = overlayColor
        )
    }
}

/**
 * Data class for selecting cropper properties. Fields of this class control inner work
 * of [CropState] while some such as [cropType], [aspectRatio], [handleSize], and [minOverlaySize]
 * is shared between ui and state.
 */
@Immutable
data class CropProperties internal constructor(
    val cropType: CropType = CropType.Dynamic,
    val handleSize: Dp = 30.dp,
    val minOverlaySize: Dp = 70.dp,
    val aspectRatio: AspectRatio = AspectRatio.Unspecified,
    val contentScale: ContentScale = ContentScale.Fit,
    val shape: Shape = RectangleShape,
    val fling: Boolean = false,
    val pannable: Boolean = true,
    val rotatable: Boolean = false,
    val zoomable: Boolean = true,
    val maxZoom: Float = 10f,
)

/**
 * Data class for cropper styling only. None of the properties of this class is used
 * by [CropState] or [Modifier.crop]
 */
@Immutable
data class CropStyle internal constructor(
    val drawOverlay: Boolean = true,
    val drawGrid: Boolean = true,
    val strokeWidth: Dp = 2.dp,
    val overlayColor: Color = Color.White,
    val handleColor: Color = Color.White
)
