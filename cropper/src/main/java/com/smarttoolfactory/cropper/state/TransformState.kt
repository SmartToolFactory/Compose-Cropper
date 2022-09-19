package com.smarttoolfactory.cropper.state

import androidx.compose.animation.core.Animatable
import androidx.compose.runtime.Stable
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.unit.IntSize
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch

/**
 * State of the pan, zoom and rotation. Allows to change zoom, pan via [Animatable]
 * objects' [Animatable.animateTo], [Animatable.snapTo].
 * @param imageSize size of the **Bitmap**
 * @param initialZoom initial zoom level
 * @param initialRotation initial angle in degrees
 * @param minZoom minimum zoom
 * @param maxZoom maximum zoom
 * @param zoomable when set to true zoom is enabled
 * @param pannable when set to true pan is enabled
 * @param rotatable when set to true rotation is enabled
 * @param limitPan limits pan to bounds of parent Composable. Using this flag prevents creating
 * empty space on sides or edges of parent.
 *
 */
@Stable
open class TransformState(
    internal val imageSize: IntSize,
    val containerSize: IntSize,
    val drawAreaSize: IntSize,
    initialZoom: Float = 1f,
    initialRotation: Float = 0f,
    minZoom: Float = 1f,
    maxZoom: Float = 10f,
    internal val zoomable: Boolean = true,
    internal val pannable: Boolean = true,
    internal val rotatable: Boolean = true,
    internal val limitPan: Boolean = false
) {

    var drawAreaRect: Rect = Rect(
        offset = Offset(
            x = ((containerSize.width - drawAreaSize.width) / 2).toFloat(),
            y = ((containerSize.height - drawAreaSize.height) / 2).toFloat()
        ),
        size = Size(drawAreaSize.width.toFloat(), drawAreaSize.height.toFloat())
    )

    internal val zoomMin = minZoom.coerceAtLeast(.5f)
    internal val zoomMax = maxZoom.coerceAtLeast(1f)
    private val zoomInitial = initialZoom.coerceIn(zoomMin, zoomMax)
    private val rotationInitial = initialRotation % 360

    internal val animatablePanX = Animatable(0f)
    internal val animatablePanY = Animatable(0f)
    internal val animatableZoom = Animatable(zoomInitial)
    internal val animatableRotation = Animatable(rotationInitial)

    init {
        animatableZoom.updateBounds(zoomMin, zoomMax)
        require(zoomMax >= zoomMin)
    }

    val pan: Offset
        get() = Offset(animatablePanX.value, animatablePanY.value)

    val zoom: Float
        get() = animatableZoom.value

    val rotation: Float
        get() = animatableRotation.value

    val isZooming: Boolean
        get() = animatableZoom.isRunning

    val isPanning: Boolean
        get() = animatablePanX.isRunning || animatablePanY.isRunning

    val isRotating: Boolean
        get() = animatableRotation.isRunning

    val isAnimationRunning: Boolean
        get() = isZooming || isPanning || isRotating

    internal open fun updateBounds(lowerBound: Offset?, upperBound: Offset?) {
        animatablePanX.updateBounds(lowerBound?.x, upperBound?.x)
        animatablePanY.updateBounds(lowerBound?.y, upperBound?.y)
    }


    /**
     * Update centroid, pan, zoom and rotation of this state when transform gestures are
     * invoked with one or multiple pointers
     */
    open suspend fun updateTransformState(
        centroid: Offset,
        panChange: Offset,
        zoomChange: Float,
        rotationChange: Float = 1f,
    ) {
        val newZoom = (this.zoom * zoomChange).coerceIn(zoomMin, zoomMax)

        snapZoomTo(newZoom)
        val newRotation = if (rotatable) {
            this.rotation + rotationChange
        } else {
            0f
        }
        snapRotationTo(newRotation)

        if (pannable) {
            val newPan = this.pan + panChange.times(this.zoom)
            snapPanXto(newPan.x)
            snapPanYto(newPan.y)
        }

        updateImageDrawAreaRect()
    }

    /**
     * Update rectangle for area that image is drawn. This rect changes when zoom and
     * pan changes and position of image changes on screen as result of transformation
     */
    private fun updateImageDrawAreaRect(){
        val containerWidth = containerSize.width
        val containerHeight = containerSize.height

        val originalDrawWidth = drawAreaSize.width
        val originalDrawHeight = drawAreaSize.height

        val panX = pan.x
        val panY = pan.y

        val left = (containerWidth - originalDrawWidth) / 2
        val top = (containerHeight - originalDrawHeight) / 2

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

    /**
     * Reset [pan], [zoom] and [rotation] with animation.
     */
    protected open suspend fun resetWithAnimation(
        pan: Offset = Offset.Zero,
        zoom: Float = 1f,
        rotation: Float = 0f
    ) = coroutineScope {
        launch { animatePanXto(pan.x) }
        launch { animatePanYto(pan.y) }
        launch { animateZoomTo(zoom) }
        launch { animateRotationTo(rotation) }
    }

    internal suspend fun animatePanXto(panX: Float) {
        if (pannable && pan.x != panX) {
            animatablePanX.animateTo(panX)
        }
    }

    internal suspend fun animatePanYto(panY: Float) {
        if (pannable && pan.y != panY) {
            animatablePanY.animateTo(panY)
        }
    }

    internal suspend fun animateZoomTo(zoom: Float) {
        if (zoomable && this.zoom != zoom) {
            val newZoom = zoom.coerceIn(zoomMin, zoomMax)
            animatableZoom.animateTo(newZoom)
        }
    }

    internal suspend fun animateRotationTo(rotation: Float) {
        if (rotatable && this.rotation != rotation) {
            animatableRotation.animateTo(rotation)
        }
    }

    internal suspend fun snapPanXto(panX: Float) {
        if (pannable) {
            animatablePanX.snapTo(panX)
        }
    }

    internal suspend fun snapPanYto(panY: Float) {
        if (pannable) {
            animatablePanY.snapTo(panY)
        }
    }

    internal suspend fun snapZoomTo(zoom: Float) {
        if (zoomable) {
            animatableZoom.snapTo(zoom.coerceIn(zoomMin, zoomMax))
        }
    }

    internal suspend fun snapRotationTo(rotation: Float) {
        if (rotatable) {
            animatableRotation.snapTo(rotation)
        }
    }
}
