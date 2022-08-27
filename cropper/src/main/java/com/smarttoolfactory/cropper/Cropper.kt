package com.smarttoolfactory.cropper

import android.graphics.Bitmap
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.sp
import com.smarttoolfactory.cropper.image.ImageWithConstraints
import com.smarttoolfactory.cropper.image.getScaledImageBitmap
import com.smarttoolfactory.cropper.util.getInitialCropRect
import com.smarttoolfactory.cropper.util.getOverlayFromAspectRatio


@Composable
fun ImageCropper(
    modifier: Modifier = Modifier,
    imageBitmap: ImageBitmap,
    aspectRatio: Float = -1f,
    contentScale: ContentScale = ContentScale.Fit,
    alignment: Alignment = Alignment.Center,
    contentDescription: String?,
    alpha: Float = DefaultAlpha,
    colorFilter: ColorFilter? = null,
    filterQuality: FilterQuality = DrawScope.DefaultFilterQuality,
    crop: Boolean = false,
    onCropSuccess: (ImageBitmap) -> Unit
) {

    ImageWithConstraints(
        modifier = modifier,
        contentScale = contentScale,
        alignment = alignment,
        contentDescription = contentDescription,
        alpha = alpha,
        colorFilter = colorFilter,
        filterQuality = filterQuality,
        imageBitmap = imageBitmap,
        drawImage = false
    ) {

        // No crop operation is applied by ScalableImage so rect points to bounds of original
        // bitmap
        val scaledImageBitmap =
            getScaledImageBitmap(
                imageWidth = imageWidth,
                imageHeight = imageHeight,
                rect = rect,
                bitmap = imageBitmap,
                contentScale = contentScale,
            )

        val bitmapWidth = scaledImageBitmap.width
        val bitmapHeight = scaledImageBitmap.height

        val imageWidthInPx: Float
        val imageHeightInPx: Float
        with(LocalDensity.current) {
            imageWidthInPx = imageWidth.toPx()
            imageHeightInPx = imageHeight.toPx()
        }


        /**
         * This rectangle is the section of drawing on screen it's correlated with boundRect
         * and its dimensions cannot be bigger than draw area.
         *
         * Corners of this [Rect] is used as handle to change bounds and grid is drawn
         * inside this rect
         */
        /**
         * This rectangle is the section of drawing on screen it's correlated with boundRect
         * and its dimensions cannot be bigger than draw area.
         *
         * Corners of this [Rect] is used as handle to change bounds and grid is drawn
         * inside this rect
         */
        var rectDraw by remember(imageWidthInPx, imageHeightInPx, contentScale, aspectRatio) {
            mutableStateOf(
                getOverlayFromAspectRatio(imageWidthInPx, imageHeightInPx, aspectRatio)
            )
        }

        /**
         * Rectangle that is used for cropping image, this rectangle is not the
         * one that draws on screen. We might have 4000x3000 rect while we
         * draw 1000x750px Composable on screen
         */
        var rectCrop by remember(
            bitmapWidth,
            bitmapHeight,
            imageWidthInPx,
            imageHeightInPx,
            rectDraw
        ) {
            mutableStateOf(
                getInitialCropRect(
                    bitmapWidth,
                    bitmapHeight,
                    imageWidthInPx,
                    imageHeightInPx,
                    rectDraw
                )
            )
        }

        LaunchedEffect(crop) {
            if (crop) {
                val croppedBitmap = Bitmap.createBitmap(
                    scaledImageBitmap.asAndroidBitmap(),
                    rectCrop.left,
                    rectCrop.top,
                    rectCrop.width,
                    rectCrop.height
                ).asImageBitmap()

                onCropSuccess(croppedBitmap)
            }
        }

        val imageModifier = Modifier
            .size(imageWidth, imageHeight)


        Box {
            ImageOverlay(modifier = imageModifier, imageBitmap = scaledImageBitmap)

            DrawingOverlay(
                modifier = Modifier.size(imageWidth, imageHeight),
                rect = rectDraw,
                dynamicOverlay = true,
                touchRegionWidth = 100f
            )

            Text(
                modifier = Modifier.align(Alignment.BottomStart),
                color = Color.White,
                fontSize = 10.sp,
                text = "imageWidthInPx: $imageWidthInPx, imageHeightInPx: $imageHeightInPx\n" +
                        "bitmapWidth: $bitmapWidth, bitmapHeight: $bitmapHeight\n" +
                        "cropRect: $rectCrop, size: ${rectCrop.size}\n" +
                        "drawRect: $rectDraw, size: ${rectDraw.size}"
            )
        }
    }
}
