package com.smarttoolfactory.cropper.state

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.VectorConverter
import androidx.compose.animation.core.exponentialDecay
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.input.pointer.PointerInputChange
import androidx.compose.ui.input.pointer.util.VelocityTracker
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntRect
import androidx.compose.ui.unit.IntSize
import com.smarttoolfactory.cropper.model.AspectRatio
import com.smarttoolfactory.cropper.model.CropData
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

/**
 * Base class for crop operations. Any class that extends this class gets access to pan, zoom,
 * rotation values and animations via [TransformState], fling and moving back to bounds animations.
 * @param imageSize size of the **Bitmap**
 * @param containerSize size of the Composable that draws **Bitmap**. This is full size
 * of the Composable. [drawAreaSize] can be smaller than [containerSize] initially based
 * on content scale of Image composable
 * @param drawAreaSize size of the area that **Bitmap** is drawn
 * @param maxZoom maximum zoom value
 * @param fling when set to true dragging pointer builds up velocity. When last
 * pointer leaves Composable a movement invoked against friction till velocity drops below
 * to threshold
 * @param moveToBounds when set to true if image zoom is lower than initial zoom or
 * panned out of image boundaries moves back to bounds with animation.
 * @param zoomable when set to true zoom is enabled
 * @param pannable when set to true pan is enabled
 * @param rotatable when set to true rotation is enabled
 * @param limitPan limits pan to bounds of parent Composable. Using this flag prevents creating
 * empty space on sides or edges of parent
 */
abstract class CropState internal constructor(
    imageSize: IntSize,
    containerSize: IntSize,
    drawAreaSize: IntSize,
    aspectRatio: AspectRatio,
    maxZoom: Float,
    val fling: Boolean = true,
    val moveToBounds: Boolean = true,
    zoomable: Boolean = true,
    pannable: Boolean = true,
    rotatable: Boolean = false,
    limitPan: Boolean = false
) : TransformState(
    imageSize = imageSize,
    containerSize = containerSize,
    drawAreaSize = drawAreaSize,
    initialZoom = 1f,
    initialRotation = 0f,
    maxZoom = maxZoom,
    zoomable = zoomable,
    pannable = pannable,
    rotatable = rotatable,
    limitPan = limitPan
) {

    private val animatableRectOverlay = Animatable(
        getOverlayFromAspectRatio(
            containerSize.width.toFloat(),
            containerSize.height.toFloat(),
            drawAreaRect.size.width,
            drawAreaRect.size.height,
            aspectRatio
        ),
        Rect.VectorConverter
    )

    val overlayRect: Rect
        get() = animatableRectOverlay.value

    var cropRect: IntRect = IntRect.Zero
        get() = getCropRectangle(
            imageSize.width,
            imageSize.height,
            drawAreaRect,
            overlayRect
        )
        private set

    private val velocityTracker = VelocityTracker()

    /**
     * Animate overlay rectangle to target value
     */
    suspend fun animateOverlayRectTo(rect: Rect) {
        animatableRectOverlay.animateTo(rect)
    }

    /**
     * Snap overlay rectangle to target value
     */
    internal suspend fun snapOverlayRectTo(rect: Rect) {
        animatableRectOverlay.snapTo(rect)
    }

    /*
        Touch gestures
     */
    abstract suspend fun onDown(change: PointerInputChange)

    abstract suspend fun onMove(change: PointerInputChange)

    abstract suspend fun onUp(change: PointerInputChange)

    /*
        Transform gestures
     */
    abstract suspend fun onGesture(
        centroid: Offset,
        panChange: Offset,
        zoomChange: Float,
        rotationChange: Float,
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

        val newRect = calculateValidImageDrawRect(overlayRect, drawAreaRect)

        val leftChange = newRect.left - drawAreaRect.left
        val topChange = newRect.top - drawAreaRect.top

        val newPanX = pan.x + leftChange
        val newPanY = pan.y + topChange

        resetWithAnimation(pan = Offset(newPanX, newPanY), zoom = zoom)
        resetTracking()
    }

    /**
     * Calculate valid position for image draw rectangle when pointer is up. Overlay rectangle
     * should fit inside draw image rectangle to have valid bounds when calculation is completed.
     *
     * @param rectOverlay rectangle of overlay that is used for cropping
     * @param rectImage rectangle of image that is being drawn
     */
    private fun calculateValidImageDrawRect(rectOverlay: Rect, rectImage: Rect): Rect {

        var rectImageArea = rectImage.copy()

        if (rectImageArea.left > rectOverlay.left) {
            rectImageArea = rectImageArea.translate(rectOverlay.left - rectImageArea.left, 0f)
        }

        if (rectImageArea.right < rectOverlay.right) {
            rectImageArea = rectImageArea.translate(rectOverlay.right - rectImageArea.right, 0f)
        }

        if (rectImageArea.top > rectOverlay.top) {
            rectImageArea = rectImageArea.translate(0f, rectOverlay.top - rectImageArea.top)
        }

        if (rectImageArea.bottom < rectOverlay.bottom) {
            rectImageArea = rectImageArea.translate(0f, rectOverlay.bottom - rectImageArea.bottom)
        }

        return rectImageArea
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

    /**
     * Create [Rect] to draw overlay based on selected aspect ratio
     */
    private fun getOverlayFromAspectRatio(
        containerWidth: Float,
        containerHeight: Float,
        drawAreaWidth: Float,
        drawAreaHeight: Float,
        aspectRatio: AspectRatio
    ): Rect {

        val offset = Offset(
            x = (containerWidth - drawAreaWidth) / 2,
            y = (containerHeight - drawAreaHeight) / 2
        )

        if (aspectRatio == AspectRatio.Unspecified) return Rect(
            offset = offset,
            size = Size(drawAreaWidth, drawAreaHeight)
        )

        val aspectRatioValue = aspectRatio.value

        var width = drawAreaWidth
        var height = drawAreaWidth / aspectRatioValue

        if (height > drawAreaHeight) {
            height = drawAreaHeight
            width = height * aspectRatioValue
        }

        val posX = offset.x + ((drawAreaWidth - width) / 2)
        val posY = offset.y + ((drawAreaHeight - height) / 2)

        return Rect(offset = Offset(posX, posY), size = Size(width, height))
    }


    /**
     * Get crop rectangle
     */
    private fun getCropRectangle(
        bitmapWidth: Int,
        bitmapHeight: Int,
        drawAreaRect: Rect,
        overlayRect: Rect
    ): IntRect {

        val overlayWidth = overlayRect.width
        val overlayHeight = overlayRect.height

        val drawAreaWidth = drawAreaRect.width
        val drawAreaHeight = drawAreaRect.height

        val widthRatio = overlayWidth / drawAreaWidth
        val heightRatio = overlayHeight / drawAreaHeight

        val diffLeft = overlayRect.left - drawAreaRect.left
        val diffTop = overlayRect.top - drawAreaRect.top

        val croppedBitmapLeft = (diffLeft * (bitmapWidth / drawAreaWidth)).toInt()
        val croppedBitmapTop = (diffTop * (bitmapHeight / drawAreaHeight)).toInt()

        val croppedBitmapWidth = (bitmapWidth * widthRatio).toInt()
                .coerceAtMost(bitmapWidth - croppedBitmapLeft)
        val croppedBitmapHeight =
            (bitmapHeight * heightRatio).toInt()
                .coerceAtMost(bitmapHeight - croppedBitmapTop)

        return IntRect(
            offset = IntOffset(croppedBitmapLeft, croppedBitmapTop),
            size = IntSize(croppedBitmapWidth, croppedBitmapHeight)
        )
    }
}
