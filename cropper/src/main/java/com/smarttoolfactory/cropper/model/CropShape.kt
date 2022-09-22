package com.smarttoolfactory.cropper.model

import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.Shape

/**
 * Model for drawing title with shape for crop selection menu. Aspect ratio is used
 * for setting overlay in state and UI
 */
@Immutable
open class CropShape(
    open val title: String,
    open val shape: Shape,
    val editable:Boolean = false,
    val shapeType: ShapeType = ShapeType.Rect
)

enum class ShapeType {
    Rect, RoundedRect, CutCorner, Oval, Polygon, Custom
}
