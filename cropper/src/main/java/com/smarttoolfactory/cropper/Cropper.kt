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
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.sp
import com.smarttoolfactory.cropper.image.ImageWithConstraints
import com.smarttoolfactory.cropper.image.getScaledImageBitmap
import com.smarttoolfactory.cropper.model.CropData
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

        // Bitmap Dimensions
        val bitmapWidth = scaledImageBitmap.width
        val bitmapHeight = scaledImageBitmap.height

        // Dimensions of Composable that displays Bitmap
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
         * [Rect] that is used for drawing overlay
         */
        var rectOverlay by remember(imageWidthInPx, imageHeightInPx, contentScale, aspectRatio) {
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
            rectOverlay
        ) {
            mutableStateOf(
                getInitialCropRect(
                    bitmapWidth,
                    bitmapHeight,
                    imageWidthInPx,
                    imageHeightInPx,
                    rectOverlay
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

        // these keys are for resetting cropper when image width/height, contentScale or
        // overlay aspect ratio changes
        val resetKeys = remember(
            scaledImageBitmap,
            imageWidthInPx,
            imageHeightInPx,
            contentScale,
            aspectRatio
        ) {
            arrayOf(scaledImageBitmap, imageWidthInPx, imageHeightInPx, contentScale, aspectRatio)
        }

        val imageModifier = Modifier
            .size(imageWidth, imageHeight)
            .crop(
                keys = resetKeys,
                cropState = rememberCropState(
                    imageSize = IntSize(bitmapWidth, bitmapHeight),
                    keys = resetKeys
                ),
                onGestureStart = { cropData: CropData ->
                    rectOverlay = cropData.overlayRect
                    rectCrop = cropData.cropRect
                },
                onGesture = { cropData: CropData ->
                    rectOverlay = cropData.overlayRect
                    rectCrop = cropData.cropRect
                },
                onGestureEnd = { cropData: CropData ->
                    rectOverlay = cropData.overlayRect
                    rectCrop = cropData.cropRect
                }
            )

        Box {
            CropperImpl(
                modifier = imageModifier,
                imageBitmap = scaledImageBitmap,
                imageWidth = imageWidth,
                imageHeight = imageHeight,
                rectOverlay = rectOverlay
            )

            // TODO Remove this text when cropper is complete. This is for debugging
            Text(
                modifier = Modifier.align(Alignment.BottomStart),
                color = Color.White,
                fontSize = 10.sp,
                text = "imageWidthInPx: $imageWidthInPx, imageHeightInPx: $imageHeightInPx\n" +
                        "bitmapWidth: $bitmapWidth, bitmapHeight: $bitmapHeight\n" +
                        "cropRect: $rectCrop, size: ${rectCrop.size}\n" +
                        "drawRect: $rectOverlay, size: ${rectOverlay.size}"
            )
        }
    }
}

@Composable
private fun CropperImpl(
    modifier: Modifier,
    imageBitmap: ImageBitmap,
    imageWidth: Dp,
    imageHeight: Dp,
    rectOverlay: Rect
) {
    Box {
        ImageOverlay(modifier = modifier, imageBitmap = imageBitmap)

        DrawingOverlay(
            modifier = Modifier.size(imageWidth, imageHeight),
            rect = rectOverlay,
            dynamicOverlay = true,
            touchRegionWidth = 100f
        )
    }
}
