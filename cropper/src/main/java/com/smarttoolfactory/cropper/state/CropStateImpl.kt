package com.smarttoolfactory.cropper.state

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.VectorConverter
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.input.pointer.PointerInputChange
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntRect
import androidx.compose.ui.unit.IntSize
import com.smarttoolfactory.cropper.model.AspectRatio
import com.smarttoolfactory.cropper.model.CropData
import com.smarttoolfactory.cropper.settings.CropProperties

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
    var fling: Boolean = true,
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


    /**
     * Update properties of [CropState] and animate to valid intervals if required
     */
    suspend fun updateProperties(cropProperties: CropProperties) {
        fling = cropProperties.fling
        pannable = cropProperties.pannable
        zoomable = cropProperties.zoomable
        rotatable = cropProperties.rotatable

        // TODO Fix zoom reset
//        zoomMax = cropProperties.maxZoom

        // Update overlay rectangle
        val aspectRatio = cropProperties.aspectRatio
        animateOverlayRectTo(
            getOverlayFromAspectRatio(
                containerSize.width.toFloat(),
                containerSize.height.toFloat(),
                drawAreaRect.size.width,
                drawAreaRect.size.height,
                aspectRatio
            )
        )

        // Update image draw area
        updateImageDrawRectFromTransformation()
    }

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

    /**
     * Update rectangle for area that image is drawn. This rect changes when zoom and
     * pan changes and position of image changes on screen as result of transformation.
     *
     * This function is called
     *
     * * when [onGesture] is called to update rect when zoom or pan changes
     *  and if [fling] is true just after **fling** gesture starts with target
     *  value in  [StaticCropState].
     *
     *  * when [updateProperties] is called in [CropState]
     *
     *  * when [onUp] is called in [DynamicCropState] to match [overlayRect] that could be
     *  changed and animated if it's out of [containerSize] bounds or its grow
     *  bigger than previous size
     */
    internal fun updateImageDrawRectFromTransformation() {
        val containerWidth = containerSize.width
        val containerHeight = containerSize.height

        val originalDrawWidth = drawAreaSize.width
        val originalDrawHeight = drawAreaSize.height

        val panX = animatablePanX.targetValue
        val panY = animatablePanY.targetValue

        val left = (containerWidth - originalDrawWidth) / 2
        val top = (containerHeight - originalDrawHeight) / 2

        val zoom = animatableZoom.targetValue

        val newWidth = originalDrawWidth * zoom
        val newHeight = originalDrawHeight * zoom

        drawAreaRect = Rect(
            offset = Offset(
                left - (newWidth - originalDrawWidth) / 2 + panX,
                top - (newHeight - originalDrawHeight) / 2 + panY,
            ),
            size = Size(newWidth, newHeight)
        )
    }

    // TODO Add resetting back to bounds for rotated state as well
    /**
     * Resets to bounds with animation and resets tracking for fling animation.
     * Changes pan, zoom and rotation to valid bounds based on [drawAreaRect] and [overlayRect]
     */
    internal suspend fun animateTransformationToOverlayBounds() {

        val zoom = zoom.coerceAtLeast(1f)

        // Calculate new pan based on overlay
        val newDrawAreaRect = calculateValidImageDrawRect(overlayRect, drawAreaRect)

        val newZoom =
            calculateNewZoom(oldRect = drawAreaRect, newRect = newDrawAreaRect, zoom = zoom)

        val leftChange = newDrawAreaRect.left - drawAreaRect.left
        val topChange = newDrawAreaRect.top - drawAreaRect.top

        val widthChange = newDrawAreaRect.width - drawAreaRect.width
        val heightChange = newDrawAreaRect.height - drawAreaRect.height

        val panXChange = leftChange + widthChange / 2
        val panYChange = topChange + heightChange / 2

        val newPanX = pan.x + panXChange
        val newPanY = pan.y + panYChange

        // Update draw area based on new pan and zoom values
        drawAreaRect = newDrawAreaRect

        resetWithAnimation(pan = Offset(newPanX, newPanY), zoom = newZoom)
        resetTracking()
    }

    /**
     * If new overlay is bigger, when crop type is dynamic, we need to increase zoom at least
     * size of bigger dimension for image draw area([drawAreaRect]) to cover overlay([overlayRect])
     */
    private fun calculateNewZoom(oldRect: Rect, newRect: Rect, zoom: Float): Float {

        val widthChange = (newRect.width / oldRect.width)
            .coerceAtLeast(1f)
        val heightChange = (newRect.height / oldRect.height)
            .coerceAtLeast(1f)

        return widthChange.coerceAtLeast(heightChange) * zoom
    }

    /**
     * Calculate valid position for image draw rectangle when pointer is up. Overlay rectangle
     * should fit inside draw image rectangle to have valid bounds when calculation is completed.
     *
     * @param rectOverlay rectangle of overlay that is used for cropping
     * @param rectImage rectangle of image that is being drawn
     */
    private fun calculateValidImageDrawRect(rectOverlay: Rect, rectImage: Rect): Rect {

        var width = rectImage.width
        var height = rectImage.height

        if (width < rectOverlay.width) {
            width = rectOverlay.width
        }

        if (height < rectOverlay.height) {
            height = rectOverlay.height
        }

        var rectImageArea = Rect(offset = rectImage.topLeft, size = Size(width, height))

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

        // Calculate latest image draw area based on overlay position
        // This is valid rectangle that contains crop area inside overlay
        val newRect = calculateValidImageDrawRect(overlayRect, drawAreaRect)

        val overlayWidth = overlayRect.width
        val overlayHeight = overlayRect.height

        val drawAreaWidth = newRect.width
        val drawAreaHeight = newRect.height

        val widthRatio = overlayWidth / drawAreaWidth
        val heightRatio = overlayHeight / drawAreaHeight

        val diffLeft = overlayRect.left - newRect.left
        val diffTop = overlayRect.top - newRect.top

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
