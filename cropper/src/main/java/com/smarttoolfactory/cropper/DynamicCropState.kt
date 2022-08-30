package com.smarttoolfactory.cropper

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.input.pointer.PointerInputChange
import androidx.compose.ui.unit.IntSize
import com.smarttoolfactory.cropper.util.*

class DynamicCropState internal constructor(
    imageSize: IntSize,
    initialZoom: Float = 1f,
    minZoom: Float = 1f,
    maxZoom: Float = 5f,
    private val touchRegionSize: Float,
    private val minDimension: Float,
    fling: Boolean = false,
    moveToBounds: Boolean = true,
    zoomable: Boolean = true,
    pannable: Boolean = true,
    rotatable: Boolean = false,
    limitPan: Boolean = false
) : CropState(
    imageSize = imageSize,
    initialZoom = initialZoom,
    minZoom = minZoom,
    maxZoom = maxZoom,
    fling = fling,
    moveToBounds = moveToBounds,
    zoomable = zoomable,
    pannable = pannable,
    rotatable = rotatable,
    limitPan = limitPan
) {

    // Rectangle that covers Image composable
    private val rectBounds =
        Rect(offset = Offset.Zero, size = Size(size.width.toFloat(), size.height.toFloat()))

    // This rectangle is needed to set bounds set at first touch position while
    // moving to constraint current bounds to temp one from first down
    // When pointer is up
    private var rectTemp = Rect.Zero

    // Region of touch inside, corners of or outside of overlay rectangle
    private var touchRegion = TouchRegion.None

    // Touch position for edge of the rectangle, used for not jumping to edge of rectangle
    // when user moves a handle. We set positionActual as position of selected handle
    // and using this distance as offset to not have a jump from touch position
    private var distanceToEdgeFromTouch = Offset.Zero

    private var doubleTapped = false

    override fun onDown(change: PointerInputChange) {

        if (overlayRect.size == Size.Zero) {
            overlayRect =
                Rect(offset = Offset.Zero, size = Size(size.width.toFloat(), size.height.toFloat()))
        }

        rectTemp = overlayRect.copy()

        println("🍏 DynamicCropState() onDown overLayRect: $overlayRect, rectTemp: $rectTemp")

        val position = change.position
        val touchPositionScreenX = position.x
        val touchPositionScreenY = position.y

        val touchPositionOnScreen = Offset(touchPositionScreenX, touchPositionScreenY)
        touchRegion = getTouchRegion(
            position = touchPositionOnScreen,
            rect = overlayRect,
            threshold = touchRegionSize
        )

        // This is the difference between touch position and edge
        // This is required for not moving edge of draw rect to touch position on move
        distanceToEdgeFromTouch =
            getDistanceToEdgeFromTouch(touchRegion, rectTemp, touchPositionOnScreen)
    }

    override fun onMove(change: PointerInputChange) {
        overlayRect = updateDrawRect(
            distanceToEdgeFromTouch = distanceToEdgeFromTouch,
            touchRegion = touchRegion,
            minDimension = minDimension,
            rectTemp = rectTemp,
            rectDraw = overlayRect,
            change = change
        )

        println("🔥 DynamicCropState() onMove overLayRect: $overlayRect, touchRegion: $touchRegion")

        if (touchRegion != TouchRegion.None) {
            change.consume()
        }

        // Calculate crop rectangle
        cropRect = calculateRectBounds()

    }

    override fun onUp(change: PointerInputChange) {
        touchRegion = TouchRegion.None

//        overlayRect = moveIntoBounds(rectBounds, overlayRect)

        // Calculate crop rectangle
        cropRect = calculateRectBounds()
        rectTemp = overlayRect.copy()

        println("🍎 DynamicCropState() onUp overLayRect: $overlayRect, cropRect: $cropRect")

    }

    override suspend fun onGesture(
        centroid: Offset,
        pan: Offset,
        zoom: Float,
        rotation: Float,
        mainPointer: PointerInputChange,
        changes: List<PointerInputChange>
    ) {
    }

    override suspend fun onGestureStart() {}

    override suspend fun onGestureEnd(onBoundsCalculated: () -> Unit) {}

    override suspend fun onDoubleTap(
        pan: Offset,
        zoom: Float,
        rotation: Float,
        onAnimationEnd: () -> Unit
    ) {
        doubleTapped = true

        if (fling) {
            resetTracking()
        }
        resetWithAnimation(pan = pan, zoom = zoom, rotation = rotation)
        onAnimationEnd()
    }

    internal fun moveIntoBounds(rectBounds: Rect, rectCurrent: Rect): Rect {
        var width = rectCurrent.width
        var height = rectCurrent.height


        if (width > rectBounds.width) {
            width = rectBounds.width
        }

        if (height > rectBounds.height) {
            height = rectBounds.height
        }

        var rect = Rect(offset = rectCurrent.topLeft, size = Size(width, height))

        if (rect.left < rectBounds.left) {
            rect = rect.translate(rectBounds.left - rect.left, 0f)
        }

        if (rect.top < rectBounds.top) {
            rect = rect.translate(0f, rectBounds.top - rect.top)
        }

        if (rect.right > rectBounds.right) {
            rect = rect.translate(rectBounds.right - rect.right, 0f)
        }

        if (rect.bottom > rectBounds.bottom) {
            rect = rect.translate(0f, rectBounds.bottom - rect.bottom)
        }

        return rect
    }
}