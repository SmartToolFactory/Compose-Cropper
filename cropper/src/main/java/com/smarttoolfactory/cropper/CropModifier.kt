package com.smarttoolfactory.cropper

import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.debugInspectorInfo
import com.smarttoolfactory.cropper.model.CropData
import com.smarttoolfactory.cropper.util.*
import com.smarttoolfactory.cropper.util.getNextZoomLevel
import com.smarttoolfactory.cropper.util.update
import com.smarttoolfactory.gesture.detectMotionEvents
import com.smarttoolfactory.gesture.detectTransformGestures
import kotlinx.coroutines.launch

/**
 * Modifier that zooms in or out of Composable set to. This zoom modifier has option
 * to move back to bounds with an animation or option to have fling gesture when user removes
 * from screen while velocity is higher than threshold to have smooth touch effect.
 *
 * @param keys are used for [Modifier.pointerInput] to restart closure when any keys assigned
 * change
 * @param clip when set to true clips to parent bounds. Anything outside parent bounds is not
 * drawn
 * empty space on sides or edges of parent.
 * @param cropState State of the zoom that contains option to set initial, min, max zoom,
 * enabling rotation, pan or zoom and contains current [CropData]
 * event propagations. Also contains [Rect] of visible area based on pan, zoom and rotation
 * @param zoomOnDoubleTap lambda that returns current [ZoomLevel] and based on current level
 * enables developer to define zoom on double tap gesture
 * @param enabled lambda can be used selectively enable or disable pan and intercepting with
 * scroll, drag or lists or pagers using current zoom, pan or rotation values
 * @param onGestureStart callback to to notify gesture has started and return current
 * [CropData]  of this modifier
 * @param onGesture callback to notify about ongoing gesture and return current
 * [CropData]  of this modifier
 * @param onGestureEnd callback to notify that gesture finished return current
 * [CropData]  of this modifier
 */
fun Modifier.crop(
    vararg keys: Any?,
    clip: Boolean = true,
    cropState: CropState,
    enabled: (Float, Offset, Float) -> Boolean = DefaultEnabled,
    zoomOnDoubleTap: (ZoomLevel) -> Float = cropState.DefaultOnDoubleTap,
    onGestureStart: ((CropData) -> Unit)? = null,
    onGesture: ((CropData) -> Unit)? = null,
    onGestureEnd: ((CropData) -> Unit)? = null,
) = composed(

    factory = {

        val coroutineScope = rememberCoroutineScope()

        // Current Zoom level
        var zoomLevel by remember { mutableStateOf(ZoomLevel.Min) }

        // Whether panning should be limited to bounds of gesture area or not
        val boundPan = cropState.limitPan && !cropState.rotatable

        // If we bound to touch area or clip is true Modifier.clipToBounds is used
        val clipToBounds = (clip || boundPan)

        val transformModifier = Modifier.pointerInput(*keys) {
            // Pass size of this Composable this Modifier is attached for constraining operations
            // inside this bounds
            cropState.size = this.size
            detectTransformGestures(
                consume = false,
                onGestureStart = {
                    onGestureStart?.invoke(cropState.cropData)
                },
                onGestureEnd = {
                    coroutineScope.launch {
                        cropState.onGestureEnd {
                            onGestureEnd?.invoke(cropState.cropData)
                        }
                    }
                },
                onGesture = { centroid, pan, zoom, rotate, mainPointer, pointerList ->

                    val cropData = cropState.cropData
                    val currentZoom = cropData.zoom
                    val currentPan = cropData.pan
                    val currentRotation = cropData.rotation
                    val gestureEnabled = enabled(currentZoom, currentPan, currentRotation)

                    coroutineScope.launch {
                        cropState.onGesture(
                            centroid = centroid,
                            pan = if (gestureEnabled) pan else Offset.Zero,
                            zoom = zoom,
                            rotation = rotate,
                            mainPointer = mainPointer,
                            changes = pointerList
                        )
                    }

                    onGesture?.invoke(cropState.cropData)

                    if (gestureEnabled) {
                        mainPointer.consume()
                    }
                }
            )
        }

        val tapModifier = Modifier.pointerInput(*keys) {
            // Pass size of this Composable this Modifier is attached for constraining operations
            // inside this bounds
            cropState.size = this.size
            detectTapGestures(
                onDoubleTap = {
                    coroutineScope.launch {
                        zoomLevel = getNextZoomLevel(zoomLevel)
                        val newZoom = zoomOnDoubleTap(zoomLevel)
                        cropState.onDoubleTap(zoom = newZoom) {
                            onGestureEnd?.invoke(cropState.cropData)
                        }
                    }
                }
            )
        }

        val touchModifier = Modifier.pointerInput(*keys){
            detectMotionEvents(
                onDown = {
                    cropState.onDown(it)
                    onGestureStart?.invoke(cropState.cropData)
                },
                onMove = {
                    cropState.onMove(it)
                    onGesture?.invoke(cropState.cropData)
                },
                onUp = {
                    cropState.onUp(it)
                    onGestureEnd?.invoke(cropState.cropData)
                }
            )
        }

        val graphicsModifier = Modifier.graphicsLayer {
            this.update(cropState)
        }

        this.then(
            (if (clipToBounds) Modifier.clipToBounds() else Modifier)
                .then(tapModifier)
                .then(transformModifier)
                .then(touchModifier)
                .then(graphicsModifier)
        )
    },
    inspectorInfo = debugInspectorInfo {
        name = "crop"
        // add name and value of each argument
        properties["keys"] = keys
        properties["clip"] = clip
        properties["onDown"] = onGestureStart
        properties["onMove"] = onGesture
        properties["onUp"] = onGestureEnd
    }
)

internal val DefaultEnabled = { zoom: Float, pan: Offset, rotation: Float ->
    true
}

internal val DefaultOnDoubleTap: (ZoomLevel) -> Float
    get() = { zoomLevel: ZoomLevel ->
        when (zoomLevel) {
            ZoomLevel.Min -> 1f
            ZoomLevel.Mid -> 2f
            ZoomLevel.Max -> 3f
        }
    }

internal val CropState.DefaultOnDoubleTap: (ZoomLevel) -> Float
    get() = { zoomLevel: ZoomLevel ->
        when (zoomLevel) {
            ZoomLevel.Min -> 1f.coerceAtMost(zoomMin)
            ZoomLevel.Mid -> 3f.coerceIn(zoomMin, zoomMax)
            ZoomLevel.Max -> 5f.coerceAtLeast(zoomMax)
        }
    }
