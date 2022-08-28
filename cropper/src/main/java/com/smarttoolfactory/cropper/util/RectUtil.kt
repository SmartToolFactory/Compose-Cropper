package com.smarttoolfactory.cropper.util

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntRect
import androidx.compose.ui.unit.IntSize
import com.smarttoolfactory.cropper.CropState

/**
 * Get rectangle of current transformation of [pan], [zoom] and current bounds of the Composable's
 * selected area as [rectSelection] in a Bitmap with [bitmapWidth] and [bitmapHeight] which
 * is displayed in a Composable with [containerWidth] and [containerHeight].
 *
 * This is getting area to display or crop based on the rectangle returned
 *
 * @param bitmapWidth width of the physical file
 * @param bitmapHeight height of the physical file
 * @param containerWidth width of the Composable that displays image
 * @param containerHeight height of the Composable that displays image
 * @param pan current translation of image via gestures or set values
 * @param rectSelection bounds of the selected area in Composable. This can be used for selecting
 * area via a cropping app
 */
fun getCropRect(
    bitmapWidth: Int,
    bitmapHeight: Int,
    containerWidth: Float,
    containerHeight: Float,
    pan: Offset,
    zoom: Float,
    rectSelection: Rect
): IntRect {
    val widthRatio = bitmapWidth / containerWidth
    val heightRatio = bitmapHeight / containerHeight

    val width = (widthRatio * rectSelection.width / zoom).coerceIn(0f, bitmapWidth.toFloat())
    val height = (heightRatio * rectSelection.height / zoom).coerceIn(0f, bitmapHeight.toFloat())

    val offsetXInBitmap = (widthRatio * (pan.x + rectSelection.left / zoom))
        .coerceIn(0f, bitmapWidth - width).toInt()
    val offsetYInBitmap = (heightRatio * (pan.y + rectSelection.top / zoom))
        .coerceIn(0f, bitmapHeight - height).toInt()

    return IntRect(
        offset = IntOffset(offsetXInBitmap, offsetYInBitmap),
        size = IntSize(width.toInt(), height.toInt())
    )
}

/**
 * Get crop rectangle initially from overlay rectangle
 */
internal fun getInitialCropRect(
    bitmapWidth: Int,
    bitmapHeight: Int,
    containerWidth: Float,
    containerHeight: Float,
    rectDraw: Rect
): IntRect {
    val overlayWidth = rectDraw.width
    val overlayHeight = rectDraw.height

    val widthRatio = bitmapWidth / containerWidth
    val heightRatio = bitmapHeight / containerHeight

    val width = (overlayWidth * widthRatio).toInt()
    val height = (overlayHeight * heightRatio).toInt()

    val left = (rectDraw.left * widthRatio).toInt()
    val top = (rectDraw.top * heightRatio).toInt()
    return IntRect(offset = IntOffset(left, top), size = IntSize(width, height))
}

/**
 * Create [Rect] to draw overlay based on selected aspect ratio
 */
internal fun getOverlayFromAspectRatio(
    containerWidth: Float,
    containerHeight: Float,
    aspectRatio: Float
): Rect {

    if (aspectRatio < 0) return Rect(
        offset = Offset.Zero,
        size = Size(containerWidth, containerHeight)
    )

    var height = containerWidth * aspectRatio
    var width = containerWidth

    if (height > containerHeight) {
        height = containerHeight
        width = height / aspectRatio
    }

    val posX = ((containerWidth - width) / 2)
    val posY = ((containerHeight - height) / 2)

    return Rect(offset = Offset(posX, posY), size = Size(width, height))
}


internal fun CropState.calculateRectBounds(): IntRect {
    val width = size.width
    val height = size.height

    val bounds = getBounds()
    val zoom = animatableZoom.targetValue
    val panX = animatablePanX.targetValue.coerceIn(-bounds.x, bounds.x)
    val panY = animatablePanY.targetValue.coerceIn(-bounds.y, bounds.y)

    // Offset for interpolating offset from (imageWidth/2,-imageWidth/2) interval
    // to (0, imageWidth) interval when
    // transform origin is TransformOrigin(0.5f,0.5f)
    val horizontalCenterOffset = width * (zoom - 1) / 2f
    val verticalCenterOffset = height * (zoom - 1) / 2f

    val offsetX = (horizontalCenterOffset - panX)
        .coerceAtLeast(0f) / zoom
    val offsetY = (verticalCenterOffset - panY)
        .coerceAtLeast(0f) / zoom

    val offset = Offset(offsetX, offsetY)

    return getCropRect(
        bitmapWidth = imageSize.width,
        bitmapHeight = imageSize.height,
        containerWidth = width.toFloat(),
        containerHeight = height.toFloat(),
        pan = offset,
        zoom = zoom,
        rectSelection = overlayRect
    )
}
