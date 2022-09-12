package com.smarttoolfactory.cropper.util

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.input.pointer.PointerInputChange
import androidx.compose.ui.input.pointer.positionChangeIgnoreConsumed
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntRect
import androidx.compose.ui.unit.IntSize
import com.smarttoolfactory.cropper.CropState
import com.smarttoolfactory.cropper.TouchRegion
import com.smarttoolfactory.cropper.model.AspectRatio

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
    containerWidth: Int,
    containerHeight: Int,
    overlayRect: Rect
): IntRect {
    val overlayWidth = overlayRect.width
    val overlayHeight = overlayRect.height

    val widthRatio = bitmapWidth / containerWidth
    val heightRatio = bitmapHeight / containerHeight

    val width = (overlayWidth * widthRatio).toInt()
    val height = (overlayHeight * heightRatio).toInt()

    val left = (overlayRect.left * widthRatio).toInt()
    val top = (overlayRect.top * heightRatio).toInt()
    return IntRect(offset = IntOffset(left, top), size = IntSize(width, height))
}

/**
 * Create [Rect] to draw overlay based on selected aspect ratio
 */
internal fun getOverlayFromAspectRatio(
    containerWidth: Float,
    containerHeight: Float,
    aspectRatio: AspectRatio
): Rect {

    if (aspectRatio== AspectRatio.Unspecified) return Rect(
        offset = Offset.Zero,
        size = Size(containerWidth, containerHeight)
    )

    val aspectRatioValue = aspectRatio.value

    var width = containerWidth
    var height = containerWidth / aspectRatioValue

    if (height > containerHeight) {
        height = containerHeight
        width = height * aspectRatioValue
    }

    val posX = ((containerWidth - width) / 2)
    val posY = ((containerHeight - height) / 2)

    return Rect(offset = Offset(posX, posY), size = Size(width, height))
}


internal fun CropState.calculateRectBounds(): IntRect {
    val width = containerSize.width
    val height = containerSize.height

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

/**
 * Update overlay rectangle based on touch gesture
 */
fun updateOverlayRect(
    distanceToEdgeFromTouch: Offset,
    touchRegion: TouchRegion,
    minDimension: Float,
    rectTemp: Rect,
    overlayRect: Rect,
    change: PointerInputChange
): Rect {

    val position = change.position
    // Get screen coordinates from touch position inside composable
    // and add how far it's from corner to not jump edge to user's touch position
    val screenPositionX = position.x + distanceToEdgeFromTouch.x
    val screenPositionY = position.y + distanceToEdgeFromTouch.y

    return when (touchRegion) {

        // Corners
        TouchRegion.TopLeft -> {

            // Set position of top left while moving with top left handle and
            // limit position to not intersect other handles
            val left = screenPositionX.coerceAtMost(rectTemp.right - minDimension)
            val top = screenPositionY.coerceAtMost(rectTemp.bottom - minDimension)
            Rect(
                left = left,
                top = top,
                right = rectTemp.right,
                bottom = rectTemp.bottom
            )
        }

        TouchRegion.BottomLeft -> {

            // Set position of top left while moving with bottom left handle and
            // limit position to not intersect other handles
            val left = screenPositionX.coerceAtMost(rectTemp.right - minDimension)
            val bottom = screenPositionY.coerceAtLeast(rectTemp.top + minDimension)
            Rect(
                left = left,
                top = rectTemp.top,
                right = rectTemp.right,
                bottom = bottom,
            )
        }

        TouchRegion.TopRight -> {

            // Set position of top left while moving with top right handle and
            // limit position to not intersect other handles
            val right = screenPositionX.coerceAtLeast(rectTemp.left + minDimension)
            val top = screenPositionY.coerceAtMost(rectTemp.bottom - minDimension)

            Rect(
                left = rectTemp.left,
                top = top,
                right = right,
                bottom = rectTemp.bottom,
            )

        }

        TouchRegion.BottomRight -> {

            // Set position of top left while moving with bottom right handle and
            // limit position to not intersect other handles
            val right = screenPositionX.coerceAtLeast(rectTemp.left + minDimension)
            val bottom = screenPositionY.coerceAtLeast(rectTemp.top + minDimension)

            Rect(
                left = rectTemp.left,
                top = rectTemp.top,
                right = right,
                bottom = bottom
            )
        }

        TouchRegion.Inside -> {
            val drag = change.positionChangeIgnoreConsumed()
            val scaledDragX = drag.x
            val scaledDragY = drag.y
            overlayRect.translate(scaledDragX, scaledDragY)
        }

        else -> overlayRect
    }
}

/**
 * Returns how far user touched to corner or center of sides of the screen. [TouchRegion]
 * where user exactly has touched is already passed to this function. For instance user
 * touched top left then this function returns distance to top left from user's position so
 * we can add an offset to not jump edge to position user touched.
 */
fun getDistanceToEdgeFromTouch(
    touchRegion: TouchRegion,
    rect: Rect,
    touchPosition: Offset
) = when (touchRegion) {
    TouchRegion.TopLeft -> {
        rect.topLeft - touchPosition
    }
    TouchRegion.TopRight -> {
        rect.topRight - touchPosition
    }
    TouchRegion.BottomLeft -> {
        rect.bottomLeft - touchPosition
    }
    TouchRegion.BottomRight -> {
        rect.bottomRight - touchPosition
    }
    else -> {
        Offset.Zero
    }
}

/**
 * get touch region inside this rectangle based on touch position.
 */
fun getTouchRegion(
    position: Offset,
    rect: Rect,
    threshold: Float
): TouchRegion {

    return when {

        position.x - rect.left in 0.0f..threshold &&
                position.y - rect.top in 0.0f..threshold -> TouchRegion.TopLeft

        rect.right - position.x in 0f..threshold &&
                position.y - rect.top in 0.0f..threshold -> TouchRegion.TopRight

        rect.right - position.x in 0f..threshold &&
                rect.bottom - position.y in 0.0f..threshold -> TouchRegion.BottomRight

        position.x - rect.left in 0.0f..threshold &&
                rect.bottom - position.y in 0.0f..threshold -> TouchRegion.BottomLeft


        rect.contains(offset = position) -> TouchRegion.Inside
        else -> TouchRegion.None
    }
}

