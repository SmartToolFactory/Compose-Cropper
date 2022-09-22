package com.smarttoolfactory.cropper.util

import android.graphics.Matrix
import androidx.compose.foundation.shape.GenericShape
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.asAndroidPath
import androidx.compose.ui.unit.LayoutDirection
import com.smarttoolfactory.cropper.model.AspectRatio
import kotlin.math.cos
import kotlin.math.sin


/**
 * Creates a polygon with number of [sides] centered at ([cx],[cy]) with [radius].
 * ```
 *  To generate regular polygons (i.e. where each interior angle is the same),
 *  polar coordinates are extremely useful. You can calculate the angle necessary
 *  to produce the desired number of sides (as the interior angles total 360º)
 *  and then use multiples of this angle with the same radius to describe each point.
 * val x = radius * Math.cos(angle);
 * val y = radius * Math.sin(angle);
 *
 * For instance to draw triangle loop thrice with angle
 * 0, 120, 240 degrees in radians and draw lines from each coordinate.
 * ```
 */
fun createPolygonPath(cx: Float, cy: Float, sides: Int, radius: Float): Path {

    val angle = 2.0 * Math.PI / sides

    return Path().apply {
        moveTo(
            cx + (radius * cos(0.0)).toFloat(),
            cy + (radius * sin(0.0)).toFloat()
        )
        for (i in 1 until sides) {
            lineTo(
                cx + (radius * cos(angle * i)).toFloat(),
                cy + (radius * sin(angle * i)).toFloat()
            )
        }
        close()
    }
}


/**
 * Create a polygon shape
 */
fun createPolygonShape(sides: Int, degrees: Float = 0f): GenericShape {
    return GenericShape { size: Size, _: LayoutDirection ->

        val radius = size.width.coerceAtMost(size.height) / 2
        addPath(
            createPolygonPath(
                cx = size.width / 2,
                cy = size.height / 2,
                sides = sides,
                radius = radius
            )
        )
        val matrix = Matrix()
        matrix.postRotate(degrees, size.width / 2, size.height / 2)
        this.asAndroidPath().transform(matrix)
    }
}

/**
 * Creates a [Rect] shape with given aspect ratio.
 */
fun createRectShape(aspectRatio: AspectRatio): GenericShape {
    return GenericShape { size: Size, _: LayoutDirection ->
        val value = aspectRatio.value

        val width = size.width
        val height = size.height
        val shapeSize =
            if (aspectRatio == AspectRatio.Unspecified) Size(width, height)
            else if (value > 1) Size(width = width, height = width / value)
            else Size(width = height * value, height = height)

        addRect(Rect(offset = Offset.Zero, size = shapeSize))
    }
}