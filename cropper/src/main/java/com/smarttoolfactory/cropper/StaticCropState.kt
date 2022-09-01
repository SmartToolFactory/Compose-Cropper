package com.smarttoolfactory.cropper

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.input.pointer.PointerInputChange
import androidx.compose.ui.unit.IntRect
import androidx.compose.ui.unit.IntSize
import com.smarttoolfactory.cropper.util.calculateRectBounds
import kotlinx.coroutines.coroutineScope

/**
 *  * State of the crop
 * @param imageSize size of the image that is zoomed or transformed. Size of the image
 * is required to get [Rect] of visible area after current transformation.
 * @param initialZoom zoom set initially
 * @param minZoom minimum zoom value
 * @param maxZoom maximum zoom value
 * @param fling when set to true dragging pointer builds up velocity. When last
 * pointer leaves Composable a movement invoked against friction till velocity drops down
 * to threshold
 * @param moveToBounds when set to true if image zoom is lower than initial zoom or
 * panned out of image boundaries moves back to bounds with animation.
 * ##Note
 * Currently rotating back to borders is not available
 * @param zoomable when set to true zoom is enabled
 * @param pannable when set to true pan is enabled
 * @param rotatable when set to true rotation is enabled
 * @param limitPan limits pan to bounds of parent Composable. Using this flag prevents creating
 * empty space on sides or edges of parent
 */
class StaticCropState internal constructor(
    imageSize: IntSize,
    containerSize:IntSize,
    initialZoom: Float = 1f,
    minZoom: Float = 1f,
    maxZoom: Float = 5f,
    fling: Boolean = false,
    moveToBounds: Boolean = true,
    zoomable: Boolean = true,
    pannable: Boolean = true,
    rotatable: Boolean = false,
    limitPan: Boolean = false
) : CropState(
    imageSize = imageSize,
    containerSize = containerSize,
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

    override var cropRect: IntRect = calculateRectBounds()
        get() = calculateRectBounds()

    override suspend fun onDown(change: PointerInputChange) = Unit
    override suspend fun onMove(change: PointerInputChange) = Unit
    override suspend fun onUp(change: PointerInputChange) = Unit

    private var doubleTapped = false

    /*
        Transform gestures
    */
    override suspend fun onGesture(
        centroid: Offset,
        pan: Offset,
        zoom: Float,
        rotation: Float,
        mainPointer: PointerInputChange,
        changes: List<PointerInputChange>
    ) = coroutineScope {

        doubleTapped = false

        updateTransformState(
            centroid = centroid,
            zoomChange = zoom,
            panChange = pan,
            rotationChange = rotation
        )

        // Fling Gesture
        if (fling) {
            if (changes.size == 1) {
                addPosition(mainPointer.uptimeMillis, mainPointer.position)
            }
        }
    }

    override suspend fun onGestureStart() = coroutineScope {}

    override suspend fun onGestureEnd(onBoundsCalculated: () -> Unit) {

        // Gesture end might be called after second tap and we don't want to fling
        // or animate back to valid bounds when doubled tapped
        if (!doubleTapped) {

            if (fling && zoom > 1) {
                fling {
                    // We get target value on start instead of updating bounds after
                    // gesture has finished
                    onBoundsCalculated()
                }
            } else {
                onBoundsCalculated()
            }

            if (moveToBounds) {
                resetToValidBounds()
            }
        }
    }

    // Double Tap
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
}
