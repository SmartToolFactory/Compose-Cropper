package com.smarttoolfactory.cropper.model

import com.smarttoolfactory.cropper.util.createRectShape

/**
 * Aspect ratio list with pre-defined aspect ratios
 */
val aspectRatios = listOf(
    CropAspectRatio("9:16", createRectShape(AspectRatio(9 / 16f)), AspectRatio(9 / 16f)),
    CropAspectRatio("2:3", createRectShape(AspectRatio(2 / 3f)), AspectRatio(2 / 3f)),
    CropAspectRatio("Full", createRectShape(AspectRatio.Unspecified), AspectRatio.Unspecified),
    CropAspectRatio("1:1", createRectShape(AspectRatio(1 / 1f)), AspectRatio(1 / 1f)),
    CropAspectRatio("16:9", createRectShape(AspectRatio(16 / 9f)), AspectRatio(16 / 9f)),
    CropAspectRatio("1.91:1", createRectShape(AspectRatio(1.91f / 1f)), AspectRatio(1.91f / 1f)),
    CropAspectRatio("3:2", createRectShape(AspectRatio(3 / 2f)), AspectRatio(3 / 2f)),
    CropAspectRatio("3:4", createRectShape(AspectRatio(3 / 4f)), AspectRatio(3 / 4f)),
    CropAspectRatio("3:5", createRectShape(AspectRatio(3 / 5f)), AspectRatio(3 / 5f))
)