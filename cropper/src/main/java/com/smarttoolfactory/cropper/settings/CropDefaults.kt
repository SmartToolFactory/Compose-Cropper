package com.smarttoolfactory.cropper.settings

import androidx.compose.runtime.Immutable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.smarttoolfactory.cropper.ImageCropper
import com.smarttoolfactory.cropper.crop
import com.smarttoolfactory.cropper.model.AspectRatio
import com.smarttoolfactory.cropper.model.CropOutline
import com.smarttoolfactory.cropper.model.OutlineType
import com.smarttoolfactory.cropper.model.aspectRatios
import com.smarttoolfactory.cropper.state.CropState

/**
 * Contains the default values used by [ImageCropper]
 */
object CropDefaults {

    fun properties(
        cropType: CropType = CropType.Dynamic,
        handleSize: Dp = 20.dp,
        maxZoom: Float = 10f,
        aspectRatio: AspectRatio = aspectRatios[2].aspectRatio,
        contentScale: ContentScale = ContentScale.Fit,
        cropOutlineProperty: CropOutlineProperty,
        pannable: Boolean = true,
        fling: Boolean = false,
        zoomable: Boolean = true,
        rotatable: Boolean = false
    ): CropProperties {
        return CropProperties(
            cropType = cropType,
            handleSize = handleSize,
            contentScale = contentScale,
            cropOutlineProperty = cropOutlineProperty,
            maxZoom = maxZoom,
            aspectRatio = aspectRatio,
            pannable = pannable,
            fling = fling,
            zoomable = zoomable,
            rotatable = rotatable
        )
    }

    fun style(
        drawOverlay: Boolean = true,
        drawGrid: Boolean = true,
        strokeWidth: Dp = 1.dp,
        overlayColor: Color = Color.Gray,
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
    val cropOutlineProperty: CropOutlineProperty,
    val pannable: Boolean,
    val fling: Boolean,
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

/**
 * Property for passing [CropOutline] between settings UI to [ImageCropper]
 */
@Immutable
data class CropOutlineProperty(
    val outlineType: OutlineType,
    val cropOutline: CropOutline
)
