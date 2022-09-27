package com.smarttoolfactory.cropper.util

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.unit.dp


fun DrawScope.drawGrid(rect: Rect, strokeWidth: Float, color: Color) {

    val width = rect.width
    val height = rect.height
    val gridWidth = width / 3
    val gridHeight = height / 3

    // Horizontal lines
    for (i in 1..2) {
        drawLine(
            color = color,
            start = Offset(rect.left, rect.top + i * gridHeight),
            end = Offset(rect.right, rect.top + i * gridHeight),
            strokeWidth = strokeWidth
        )
    }

    // Vertical lines
    for (i in 1..2) {
        drawLine(
            color,
            start = Offset(rect.left + i * gridWidth, rect.top),
            end = Offset(rect.left + i * gridWidth, rect.bottom),
            strokeWidth = strokeWidth
        )
    }
}


fun DrawScope.drawChecker() {
    val width = this.size.width
    val height = this.size.height

    val checkerWidth = 10.dp.toPx()

    val checkerHeight = 10.dp.toPx()

    val horizontalSteps = (width / checkerWidth).toInt()
    val verticalSteps = (height / checkerHeight).toInt()

    for (y in 0..verticalSteps) {
        for (x in 0..horizontalSteps) {
            val isGrayTile = ((x + y) % 2 == 1)
            drawRect(
                color = if (isGrayTile) Color.LightGray else Color.White,
                topLeft = Offset(x * checkerWidth, y * checkerHeight),
                size = Size(checkerWidth, checkerHeight)
            )
        }
    }
}
