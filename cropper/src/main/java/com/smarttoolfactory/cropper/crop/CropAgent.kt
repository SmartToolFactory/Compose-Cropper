package com.smarttoolfactory.cropper.crop

import android.graphics.Bitmap
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.*
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.LayoutDirection
import com.smarttoolfactory.cropper.model.CropImageMask
import com.smarttoolfactory.cropper.model.CropOutline
import com.smarttoolfactory.cropper.model.CropPath
import com.smarttoolfactory.cropper.model.CropShape


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
        cropOutline: CropOutline,
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

        drawCroppedImage(cropOutline, cropRect, layoutDirection, density, croppedBitmap)
        onCropSuccess(croppedBitmap)
    }

    fun crop(
        imageBitmap: ImageBitmap,
        cropRect: Rect,
        cropOutline: CropOutline,
        layoutDirection: LayoutDirection,
        density: Density,
    ): ImageBitmap {

        val croppedBitmap = Bitmap.createBitmap(
            imageBitmap.asAndroidBitmap(),
            cropRect.left.toInt(),
            cropRect.top.toInt(),
            cropRect.width.toInt(),
            cropRect.height.toInt(),
        ).asImageBitmap()

        drawCroppedImage(cropOutline, cropRect, layoutDirection, density, croppedBitmap)
        return croppedBitmap
    }

    private fun drawCroppedImage(
        cropOutline: CropOutline,
        cropRect: Rect,
        layoutDirection: LayoutDirection,
        density: Density,
        croppedBitmap: ImageBitmap,
    ) {

        val imageToCrop = croppedBitmap
            .asAndroidBitmap()
            .copy(Bitmap.Config.ARGB_8888, true)!!
            .asImageBitmap()

        when (cropOutline) {
            is CropShape -> {
                val path = Path().apply {
                    val outline =
                        cropOutline.shape.createOutline(cropRect.size, layoutDirection, density)
                    addOutline(outline)
                }

                Canvas(image = imageToCrop).run {
                    saveLayer(nativeCanvas.clipBounds.toComposeRect(), imagePaint)

                    // Destination
                    drawPath(path, imagePaint)

                    // Source
                    drawImage(
                        image = imageToCrop,
                        topLeftOffset = Offset.Zero,
                        paint = imagePaint
                    )
                    restore()
                }
            }
            is CropPath -> {
                val path = cropOutline.path
                Canvas(image = imageToCrop).run {
                    saveLayer(nativeCanvas.clipBounds.toComposeRect(), imagePaint)

                    // Destination
                    drawPath(path, paint)

                    // Source
                    drawImage(image = imageToCrop, topLeftOffset = Offset.Zero, imagePaint)
                    restore()
                }
            }
            is CropImageMask -> {

                val image = cropOutline.image

                Canvas(image = imageToCrop).run {
                    saveLayer(nativeCanvas.clipBounds.toComposeRect(), imagePaint)

                    // Destination
                    drawImage(image, topLeftOffset = cropRect.topLeft, imagePaint)

                    // Source
                    drawImage(image = imageToCrop, topLeftOffset = Offset.Zero, paint)

                    restore()
                }
            }
        }
    }
}

