package com.smarttoolfactory.cropper.model

import com.smarttoolfactory.cropper.util.createRectShape

/**
 * Aspect ratio list with pre-defined aspect ratios
 */
val aspectRatios = listOf(
    AspectRatioModel("9:16", createRectShape(9 / 16f), AspectRatio(9 / 16f)),
    AspectRatioModel("2:3", createRectShape(2 / 3f), AspectRatio(2 / 3f)),
    AspectRatioModel("1:1", createRectShape(1 / 1f), AspectRatio(1 / 1f)),
    AspectRatioModel("16:9", createRectShape(16 / 9f), AspectRatio(16 / 9f)),
    AspectRatioModel("1.91:1", createRectShape(1.91f / 1f), AspectRatio(1.91f / 1f)),
    AspectRatioModel("3:2", createRectShape(3 / 2f), AspectRatio(3 / 2f)),
    AspectRatioModel("3:4", createRectShape(3 / 4f), AspectRatio(3 / 4f)),
    AspectRatioModel("3:5", createRectShape(3 / 5f), AspectRatio(3 / 5f)),
)