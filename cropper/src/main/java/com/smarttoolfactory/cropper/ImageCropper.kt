@file:OptIn(ExperimentalAnimationApi::class)

package com.smarttoolfactory.cropper

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.tween
import androidx.compose.animation.scaleIn
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.FilterQuality
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntSize
import com.smarttoolfactory.cropper.crop.CropAgent
import com.smarttoolfactory.cropper.draw.DrawingOverlay
import com.smarttoolfactory.cropper.draw.ImageDrawCanvas
import com.smarttoolfactory.cropper.image.ImageWithConstraints
import com.smarttoolfactory.cropper.image.getScaledImageBitmap
import com.smarttoolfactory.cropper.model.CropOutline
import com.smarttoolfactory.cropper.settings.CropDefaults
import com.smarttoolfactory.cropper.settings.CropProperties
import com.smarttoolfactory.cropper.settings.CropStyle
import com.smarttoolfactory.cropper.settings.CropType
import com.smarttoolfactory.cropper.state.rememberCropState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*

@Composable
fun ImageCropper(
    modifier: Modifier = Modifier,
    imageBitmap: ImageBitmap,
    contentDescription: String?,
    cropStyle: CropStyle = CropDefaults.style(),
    cropProperties: CropProperties,
    filterQuality: FilterQuality = DrawScope.DefaultFilterQuality,
    crop: Boolean = false,
    onCropStart: () -> Unit,
    onCropSuccess: (ImageBitmap) -> Unit
) {

    ImageWithConstraints(
        modifier = modifier.clipToBounds(),
        contentScale = cropProperties.contentScale,
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
                contentScale = cropProperties.contentScale,
            )

        val cropAgent = remember {
            CropAgent()
        }

        // Container Dimensions
        val containerWidthPx = constraints.maxWidth
        val containerHeightPx = constraints.maxHeight

        // Bitmap Dimensions
        val bitmapWidth = scaledImageBitmap.width
        val bitmapHeight = scaledImageBitmap.height

        // Dimensions of Composable that displays Bitmap
        val imageWidthPx: Int
        val imageHeightPx: Int

        val containerWidth: Dp
        val containerHeight: Dp

        with(LocalDensity.current) {
            imageWidthPx = imageWidth.roundToPx()
            imageHeightPx = imageHeight.roundToPx()
            containerWidth = containerWidthPx.toDp()
            containerHeight = containerHeightPx.toDp()
        }

        val cropType = cropProperties.cropType
        val contentScale = cropProperties.contentScale

        val cropOutline = cropProperties.cropOutlineProperty.cropOutline

        // these keys are for resetting cropper when image width/height, contentScale or
        // overlay aspect ratio changes
        val resetKeys = remember(
            scaledImageBitmap,
            imageWidthPx,
            imageHeightPx,
            contentScale,
            cropType
        ) {
            arrayOf(
                scaledImageBitmap,
                imageWidthPx,
                imageHeightPx,
                contentScale,
                cropType
            )
        }

        val cropState = rememberCropState(
            imageSize = IntSize(bitmapWidth, bitmapHeight),
            containerSize = IntSize(containerWidthPx, containerHeightPx),
            drawAreaSize = IntSize(imageWidthPx, imageHeightPx),
            cropProperties = cropProperties,
            keys = resetKeys
        )

        LaunchedEffect(key1 = cropProperties) {
            cropState.updateProperties(cropProperties)
        }

        /**
         * Rectangle that is used for cropping image, this rectangle is not the
         * one that draws on screen. We might have 4000x3000 rect while we
         * draw 1000x750px Composable on screen
         */
        val rectCrop = cropState.cropRect

        val density = LocalDensity.current
        val layoutDirection = LocalLayoutDirection.current

        LaunchedEffect(crop) {
            if (crop) {
                flow {
                    emit(
                        cropAgent.crop(
                            scaledImageBitmap,
                            rectCrop,
                            cropOutline,
                            layoutDirection,
                            density
                        )
                    )
                }
                    .flowOn(Dispatchers.Default)
                    .onStart {
                        onCropStart()
                        delay(400)
                    }
                    .onEach {
                        onCropSuccess(it)
                    }
                    .launchIn(this)
            }
        }

        val imageModifier = Modifier
            .size(containerWidth, containerHeight)
            .crop(
                keys = resetKeys,
                cropState = cropState
            )

        Box(modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
        ) {

            /// Create a MutableTransitionState<Boolean> for the AnimatedVisibility.
            var visible by remember { mutableStateOf(false) }

            LaunchedEffect(Unit) {
                delay(100)
                visible = true
            }

            AnimatedVisibility(
                visible = visible,
                enter = scaleIn(tween(500))
            ) {

                ImageCropperImpl(
                    modifier = imageModifier,
                    imageBitmap = scaledImageBitmap,
                    containerWidth = containerWidth,
                    containerHeight = containerHeight,
                    imageWidthPx = imageWidthPx,
                    imageHeightPx = imageHeightPx,
                    cropType = cropType,
                    cropOutline = cropOutline,
                    handleSize = cropProperties.handleSize,
                    cropStyle = cropStyle,
                    rectOverlay = cropState.overlayRect
                )
            }
            
            // TODO Remove this text when cropper is complete. This is for debugging
//            val drawAreaRect = cropState.drawAreaRect
//            val pan = cropState.pan
//            val zoom = cropState.zoom
//            Text(
//                modifier = Modifier.align(Alignment.TopStart),
//                color = Color.White,
//                fontSize = 10.sp,
//                text = "imageWidthInPx: $imageWidthPx, imageHeightInPx: $imageHeightPx\n" +
//                        "bitmapWidth: $bitmapWidth, bitmapHeight: $bitmapHeight\n" +
//                        "zoom: $zoom, pan: $pan\n" +
//                        "drawAreaRect: $drawAreaRect, size: ${drawAreaRect.size}\n" +
//                        "overlayRect: ${cropState.overlayRect}, size: ${cropState.overlayRect.size}\n" +
//                        "cropRect: $rectCrop, size: ${rectCrop.size}"
//            )
        }
    }
}

@Composable
private fun ImageCropperImpl(
    modifier: Modifier,
    imageBitmap: ImageBitmap,
    containerWidth: Dp,
    containerHeight: Dp,
    imageWidthPx: Int,
    imageHeightPx: Int,
    cropType: CropType,
    cropOutline: CropOutline,
    handleSize: Dp,
    cropStyle: CropStyle,
    rectOverlay: Rect
) {

    Box(contentAlignment = Alignment.Center) {

        // Draw Image
        ImageDrawCanvas(
            modifier = modifier,
            imageBitmap = imageBitmap,
            imageWidth = imageWidthPx,
            imageHeight = imageHeightPx
        )

        val drawOverlay = cropStyle.drawOverlay

        val drawGrid = cropStyle.drawGrid
        val overlayColor = cropStyle.overlayColor
        val handleColor = cropStyle.handleColor
        val drawHandles = cropType == CropType.Dynamic
        val strokeWidth = cropStyle.strokeWidth

        val handleSizeInPx = LocalDensity.current.run { handleSize.toPx() }

        DrawingOverlay(
            modifier = Modifier.size(containerWidth, containerHeight),
            drawOverlay = drawOverlay,
            rect = rectOverlay,
            cropOutline = cropOutline,
            drawGrid = drawGrid,
            overlayColor = overlayColor,
            handleColor = handleColor,
            strokeWidth = strokeWidth,
            drawHandles = drawHandles,
            handleSize = handleSizeInPx
        )

    }
}
