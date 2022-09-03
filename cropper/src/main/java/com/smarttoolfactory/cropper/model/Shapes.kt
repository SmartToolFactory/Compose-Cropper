package com.smarttoolfactory.cropper.model

import com.smarttoolfactory.cropper.util.createRectShape

/**
 * Aspect ratio list with pre-defined aspect ratios
 */
val AspectRatios = listOf(
    ShapeModel("9:16", createRectShape(9 / 16f)),
    ShapeModel("2:3", createRectShape(2 / 3f)),
    ShapeModel("1:1", createRectShape(1 / 1f)),
    ShapeModel("16:9", createRectShape(16 / 9f)),
    ShapeModel("1.91:1", createRectShape(1.91f / 1f)),
    ShapeModel("3:2", createRectShape(3 / 2f)),
    ShapeModel("3:4", createRectShape(3 / 4f)),
    ShapeModel("3:5", createRectShape(3 / 5f)),
)