package com.smarttoolfactory.cropper

import androidx.compose.foundation.Canvas
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.LayoutDirection
import com.smarttoolfactory.cropper.util.drawGrid
import kotlin.math.roundToInt

/**
 * Draw overlay composed of 9 rectangles. When [drawHandles]
 * is set draw handles for changing drawing rectangle
 */
@Composable
internal fun DrawingOverlay(
    modifier: Modifier,
    drawOverlay: Boolean,
    rect: Rect,
    shape: Shape,
    drawGrid: Boolean,
    overlayColor: Color,
    handleColor: Color,
    strokeWidth: Dp,
    drawHandles: Boolean,
    handleSize: Float
) {
    val density = LocalDensity.current
    val layoutDirection: LayoutDirection = LocalLayoutDirection.current

    val pathHandles = remember {
        Path()
    }

    // TODO Update with a way that doesn't create new object on recomposition
    val outline = remember(rect, shape) {
        shape.createOutline(rect.size, layoutDirection, density)
    }

    Canvas(modifier = modifier) {
        val strokeWidthPx = strokeWidth.toPx()


        with(drawContext.canvas.nativeCanvas) {
            val checkPoint = saveLayer(null, null)

            // Destination
            drawRect(Color(0x77000000))

            // Source
            translate(left = rect.left, top = rect.top) {
                drawOutline(
                    outline = outline,
                    color = Color.Transparent,
                    blendMode = BlendMode.SrcOut
                )
            }

            if (drawGrid) {
                drawGrid(
                    rect = rect,
                    strokeWidth = strokeWidthPx,
                    color = overlayColor
                )
            }

            restoreToCount(checkPoint)
        }

        if (drawOverlay) {
            drawRect(
                topLeft = rect.topLeft,
                size = rect.size,
                color = overlayColor,
                style = Stroke(width = strokeWidthPx)
            )

            if (drawHandles) {
                pathHandles.apply {
                    reset()

                    if (rect != Rect.Zero) {
                        // Top left lines
                        moveTo(rect.topLeft.x, rect.topLeft.y + handleSize)
                        lineTo(rect.topLeft.x, rect.topLeft.y)
                        lineTo(rect.topLeft.x + handleSize, rect.topLeft.y)

                        // Top right lines
                        moveTo(rect.topRight.x - handleSize, rect.topRight.y)
                        lineTo(rect.topRight.x, rect.topRight.y)
                        lineTo(rect.topRight.x, rect.topRight.y + handleSize)

                        // Bottom right lines
                        moveTo(rect.bottomRight.x, rect.bottomRight.y - handleSize)
                        lineTo(rect.bottomRight.x, rect.bottomRight.y)
                        lineTo(rect.bottomRight.x - handleSize, rect.bottomRight.y)

                        // Bottom left lines
                        moveTo(rect.bottomLeft.x + handleSize, rect.bottomLeft.y)
                        lineTo(rect.bottomLeft.x, rect.bottomLeft.y)
                        lineTo(rect.bottomLeft.x, rect.bottomLeft.y - handleSize)
                    }
                }

                drawPath(
                    path = pathHandles,
                    color = handleColor,
                    style = Stroke(
                        width = strokeWidthPx * 2,
                        cap = StrokeCap.Round,
                        join = StrokeJoin.Round
                    )
                )
            }
        }
    }
}

/**
 * Draw image to be cropped
 */
@Composable
internal fun ImageOverlay(
    modifier: Modifier,
    imageBitmap: ImageBitmap,
    imageWidth: Int,
    imageHeight: Int
) {
    Canvas(modifier = modifier) {

        val canvasWidth = size.width.roundToInt()
        val canvasHeight = size.height.roundToInt()

        drawImage(
            image = imageBitmap,
            srcSize = IntSize(imageBitmap.width, imageBitmap.height),
            dstSize = IntSize(imageWidth, imageHeight),
            dstOffset = IntOffset(
                x = (canvasWidth - imageWidth) / 2,
                y = (canvasHeight - imageHeight) / 2
            )
        )
    }
}
