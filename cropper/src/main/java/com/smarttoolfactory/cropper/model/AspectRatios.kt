package com.smarttoolfactory.cropper.model

import com.smarttoolfactory.cropper.util.createRectShape

/**
 * Aspect ratio list with pre-defined aspect ratios
 */
val aspectRatios = listOf(
    AspectRatioModel("9:16", createRectShape(AspectRatio(9 / 16f)), AspectRatio(9 / 16f)),
    AspectRatioModel("2:3", createRectShape(AspectRatio(2 / 3f)), AspectRatio(2 / 3f)),
    AspectRatioModel("Full", createRectShape(AspectRatio.Unspecified), AspectRatio.Unspecified),
    AspectRatioModel("1:1", createRectShape(AspectRatio(1 / 1f)), AspectRatio(1 / 1f)),
    AspectRatioModel("16:9", createRectShape(AspectRatio(16 / 9f)), AspectRatio(16 / 9f)),
    AspectRatioModel("1.91:1", createRectShape(AspectRatio(1.91f / 1f)), AspectRatio(1.91f / 1f)),
    AspectRatioModel("3:2", createRectShape(AspectRatio(3 / 2f)), AspectRatio(3 / 2f)),
    AspectRatioModel("3:4", createRectShape(AspectRatio(3 / 4f)), AspectRatio(3 / 4f)),
    AspectRatioModel("3:5", createRectShape(AspectRatio(3 / 5f)), AspectRatio(3 / 5f))
)