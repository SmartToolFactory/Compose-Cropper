package com.smarttoolfactory.cropper.crop

import android.graphics.Bitmap
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.*
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.LayoutDirection

/**
 * Crops imageBitmap based on path that is passed in [crop] function
 */
class CropAgent {

    private val imagePaint = Paint().apply {
        blendMode = BlendMode.SrcIn
    }

    private val paint = Paint()

    fun crop(
        imageBitmap: ImageBitmap,
        cropRect: Rect,
        shape: Shape,
        layoutDirection: LayoutDirection,
        density: Density,
        onCropSuccess: (ImageBitmap) -> Unit
    ) {

        val croppedBitmap = Bitmap.createBitmap(
            imageBitmap.asAndroidBitmap(),
            cropRect.left.toInt(),
            cropRect.top.toInt(),
            cropRect.width.toInt(),
            cropRect.height.toInt()
        ).asImageBitmap()

        val path = Path().apply {
            val outline = shape.createOutline(cropRect.size, layoutDirection, density)
            addOutline(outline)
        }

        Canvas(image = croppedBitmap).run {
            saveLayer(nativeCanvas.clipBounds.toComposeRect(), imagePaint)
            drawPath(path, paint)
            drawImage(image = croppedBitmap, topLeftOffset = Offset.Zero, imagePaint)
            onCropSuccess(croppedBitmap)
            restore()
        }
    }

    fun crop(
        imageBitmap: ImageBitmap,
        cropRect: Rect,
        shape: Shape,
        layoutDirection: LayoutDirection,
        density: Density,
    ): ImageBitmap {

        val croppedBitmap = Bitmap.createBitmap(
            imageBitmap.asAndroidBitmap(),
            cropRect.left.toInt(),
            cropRect.top.toInt(),
            cropRect.width.toInt(),
            cropRect.height.toInt()
        ).asImageBitmap()

        val path = Path().apply {
            val outline = shape.createOutline(cropRect.size, layoutDirection, density)
            addOutline(outline)
        }

        Canvas(image = croppedBitmap).run {
            saveLayer(nativeCanvas.clipBounds.toComposeRect(), imagePaint)
            drawPath(path, paint)
            drawImage(image = croppedBitmap, topLeftOffset = Offset.Zero, imagePaint)
            restore()
        }

        return croppedBitmap
    }

}