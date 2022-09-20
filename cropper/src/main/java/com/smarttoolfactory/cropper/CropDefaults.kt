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
        cropType: CropType = CropType.Static,
        handleSize: Dp = 20.dp,
        maxZoom: Float = 10f,
        aspectRatio: AspectRatio = AspectRatio.Unspecified,
        contentScale: ContentScale = ContentScale.Fit,
        shape: Shape = RectangleShape,
        fling: Boolean = true,
        zoomable: Boolean = true,
        pannable: Boolean = true,
        rotatable: Boolean = false
    ): CropProperties {
        return CropProperties(
            cropType = cropType,
            handleSize = handleSize,
            contentScale = contentScale,
            shape = shape,
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
        strokeWidth: Dp = 1.dp,
        overlayColor: Color = Color.DarkGray,
        handleColor: Color = Color.White
    ): CropStyle {
        return CropStyle(
            drawOverlay = drawOverlay,
            drawGrid = drawGrid,
            strokeWidth = strokeWidth,
            overlayColor = overlayColor,
            handleColor = handleColor
        )
    }
}

/**
 * Data class for selecting cropper properties. Fields of this class control inner work
 * of [CropState] while some such as [cropType], [aspectRatio], [handleSize]
 * is shared between ui and state.
 */
@Immutable
data class CropProperties internal constructor(
    val cropType: CropType,
    val handleSize: Dp,
    val aspectRatio: AspectRatio,
    val contentScale: ContentScale,
    val shape: Shape,
    val fling: Boolean,
    val pannable: Boolean,
    val rotatable: Boolean,
    val zoomable: Boolean,
    val maxZoom: Float,
)

/**
 * Data class for cropper styling only. None of the properties of this class is used
 * by [CropState] or [Modifier.crop]
 */
@Immutable
data class CropStyle internal constructor(
    val drawOverlay: Boolean,
    val drawGrid: Boolean,
    val strokeWidth: Dp,
    val overlayColor: Color,
    val handleColor: Color
)
