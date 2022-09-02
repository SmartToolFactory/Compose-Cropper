package com.smarttoolfactory.cropper.model

import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.Shape

/**
 * Model for drawing title with shape for crop selection menu. Aspect ratio is used
 * for setting overlay in state and UI
 */
@Immutable
data class ShapeModel(
    val title: String,
    val shape: Shape,
    val aspectRatio: AspectRatio = AspectRatio.Unspecified
)