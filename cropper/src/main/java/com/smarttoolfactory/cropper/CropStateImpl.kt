package com.smarttoolfactory.cropper

import androidx.compose.animation.core.exponentialDecay
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.input.pointer.PointerInputChange
import androidx.compose.ui.input.pointer.util.VelocityTracker
import androidx.compose.ui.unit.IntRect
import androidx.compose.ui.unit.IntSize
import com.smarttoolfactory.cropper.model.CropData
import com.smarttoolfactory.cropper.util.calculateRectBounds
import com.smarttoolfactory.cropper.util.coerceIn
import com.smarttoolfactory.cropper.util.getCropRect
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch

val CropState.cropData: CropData
    get() = CropData(
        zoom = animatableZoom.targetValue,
        pan = Offset(animatablePanX.targetValue, animatablePanY.targetValue),
        rotation = animatableRotation.targetValue,
        overlayRect = overlayRect,
        cropRect = cropRect
    )

abstract class CropState internal constructor(
    val imageSize: IntSize,
    initialZoom: Float = 1f,
    minZoom: Float = .5f,
    maxZoom: Float = 5f,
    val fling: Boolean = true,
    val moveToBounds: Boolean = true,
    zoomable: Boolean = true,
    pannable: Boolean = true,
    rotatable: Boolean = false,
    limitPan: Boolean = false
) : TransformState(
    initialZoom = initialZoom,
    initialRotation = 0f,
    minZoom = minZoom,
    maxZoom = maxZoom,
    zoomable = zoomable,
    pannable = pannable,
    rotatable = rotatable,
    limitPan = limitPan
) {

    open val overlayRect: Rect =
        Rect(offset = Offset.Zero, size = Size(size.width.toFloat(), size.height.toFloat()))

    open val cropRect: IntRect = IntRect.Zero

    private val velocityTracker = VelocityTracker()

    /*
        Touch gestures
     */
    abstract fun onDown(position: Offset)

    abstract fun onMove(position: Offset)

    abstract fun onUp(position: Offset)

    /*
        Transform gestures
     */
    abstract suspend fun onGesture(
        centroid: Offset,
        pan: Offset,
        zoom: Float,
        rotation: Float,
        mainPointer: PointerInputChange,
        changes: List<PointerInputChange>
    )

    abstract suspend fun onGestureStart()

    abstract suspend fun onGestureEnd(onBoundsCalculated: () -> Unit)

    // Double Tap
    abstract suspend fun onDoubleTap(
        pan: Offset = Offset.Zero,
        zoom: Float = 1f,
        rotation: Float = 0f,
        onAnimationEnd: () -> Unit
    )

    // TODO Add resetting back to bounds for rotated state as well
    /**
     * Resets to bounds with animation and resets tracking for fling animation
     */
    internal suspend fun resetToValidBounds() {
        val zoom = zoom.coerceAtLeast(1f)
        val bounds = getBounds()
        val pan = pan.coerceIn(-bounds.x..bounds.x, -bounds.y..bounds.y)
        resetWithAnimation(pan = pan, zoom = zoom)
        resetTracking()
    }

    /*
        Fling gesture
     */
    internal fun addPosition(timeMillis: Long, position: Offset) {
        velocityTracker.addPosition(
            timeMillis = timeMillis,
            position = position
        )
    }

    /**
     * Create a fling gesture when user removes finger from scree to have continuous movement
     * until [velocityTracker] speed reached to lower bound
     */
    internal suspend fun fling(onFlingStart: () -> Unit) = coroutineScope {
        val velocityTracker = velocityTracker.calculateVelocity()
        val velocity = Offset(velocityTracker.x, velocityTracker.y)
        var flingStarted = false

        launch {
            animatablePanX.animateDecay(
                velocity.x,
                exponentialDecay(absVelocityThreshold = 20f),
                block = {
                    // This callback returns target value of fling gesture initially
                    if (!flingStarted) {
                        onFlingStart()
                        flingStarted = true
                    }
                }
            )
        }

        launch {
            animatablePanY.animateDecay(
                velocity.y,
                exponentialDecay(absVelocityThreshold = 20f),
                block = {
                    // This callback returns target value of fling gesture initially
                    if (!flingStarted) {
                        onFlingStart()
                        flingStarted = true
                    }
                }
            )
        }
    }

    internal fun resetTracking() {
        velocityTracker.resetTracking()
    }
}
