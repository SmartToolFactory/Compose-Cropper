package com.smarttoolfactory.cropper.model

import androidx.compose.runtime.Immutable

/**
 * Model for drawing title with shape for crop selection menu.
 */
@Immutable
data class CropFrame(
    val outlineType: OutlineType,
    val editable: Boolean = false,
    val cropOutlineContainer: CropOutlineContainer<out CropOutline>
)

enum class OutlineType {
    Rect, RoundedRect, CutCorner, Oval, Polygon, Custom, ImageMask
}
