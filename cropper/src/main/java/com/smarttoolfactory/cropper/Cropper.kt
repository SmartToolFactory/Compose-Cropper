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

@Composable
fun ImageCropper(
    modifier: Modifier = Modifier,
    imageBitmap: ImageBitmap,
    contentScale: ContentScale = ContentScale.Fit,
    contentDescription: String?,
    cropStyle: CropStyle = CropDefaults.style(),
    cropProperties: CropProperties = CropDefaults.properties(),
    filterQuality: FilterQuality = DrawScope.DefaultFilterQuality,
    crop: Boolean = false,
    onCropSuccess: (ImageBitmap) -> Unit
) {

    ImageWithConstraints(
        modifier = modifier,
        contentScale = contentScale,
        contentDescription = contentDescription,
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

        val aspectRatio = cropProperties.aspectRatio

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

        val cropState = rememberCropState(
            imageSize = IntSize(bitmapWidth, bitmapHeight),
            containerSize = IntSize(imageWidthInPx.toInt(), imageHeightInPx.toInt()),
            cropProperties = cropProperties,
            keys = resetKeys
        )

        /**
         * Rectangle that is used for cropping image, this rectangle is not the
         * one that draws on screen. We might have 4000x3000 rect while we
         * draw 1000x750px Composable on screen
         */
        var rectCrop by remember(
            bitmapWidth,
            bitmapHeight,
            imageWidthInPx,
            imageHeightInPx
        ) {
            mutableStateOf(
                getInitialCropRect(
                    bitmapWidth,
                    bitmapHeight,
                    imageWidthInPx.toInt(),
                    imageHeightInPx.toInt(),
                    cropState.overlayRect
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
            .crop(
                keys = resetKeys,
                cropState = cropState,
                onGestureStart = { cropData: CropData ->
                    rectCrop = cropData.cropRect
                },
                onGesture = { cropData: CropData ->
                    rectCrop = cropData.cropRect
                },
                onGestureEnd = { cropData: CropData ->
                    rectCrop = cropData.cropRect
                }
            )

        Box {
            CropperImpl(
                modifier = imageModifier,
                imageBitmap = scaledImageBitmap,
                containerWidth = imageWidth,
                containerHeight = imageHeight,
                cropType = cropProperties.cropType,
                handleSize = cropProperties.handleSize,
                cropStyle = cropStyle,
                rectOverlay = cropState.overlayRect
            )

            // TODO Remove this text when cropper is complete. This is for debugging
            Text(
                modifier = Modifier.align(Alignment.BottomStart),
                color = Color.White,
                fontSize = 10.sp,
                text = "imageWidthInPx: $imageWidthInPx, imageHeightInPx: $imageHeightInPx\n" +
                        "bitmapWidth: $bitmapWidth, bitmapHeight: $bitmapHeight\n" +
                        "cropRect: $rectCrop, size: ${rectCrop.size}\n" +
                        "drawRect: ${cropState.overlayRect}, size: ${cropState.overlayRect.size}"
            )
        }
    }
}

@Composable
private fun CropperImpl(
    modifier: Modifier,
    imageBitmap: ImageBitmap,
    containerWidth: Dp,
    containerHeight: Dp,
    cropType: CropType,
    handleSize: Dp,
    cropStyle: CropStyle,
    rectOverlay: Rect
) {

    Box {
        ImageOverlay(modifier = modifier, imageBitmap = imageBitmap)

        val drawOverlay = cropStyle.drawOverlay

        if (drawOverlay) {
            val drawGrid = cropStyle.drawGrid
            val color = cropStyle.overlayColor
            val drawHandles = cropType == CropType.Dynamic
            val strokeWidth = cropStyle.strokeWidth

            val handleSizeInPx = LocalDensity.current.run { handleSize.toPx() }

            DrawingOverlay(
                modifier = Modifier.size(containerWidth, containerHeight),
                rect = rectOverlay,
                drawGrid = drawGrid,
                color = color,
                strokeWidth = strokeWidth,
                drawHandles = drawHandles,
                handleSize = handleSizeInPx
            )
        }
    }
}
