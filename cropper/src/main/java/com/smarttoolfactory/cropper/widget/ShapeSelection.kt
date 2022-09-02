package com.smarttoolfactory.cropper.widget

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawOutline
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.smarttoolfactory.cropper.model.ShapeModel

@Composable
fun ShapeSelection(isSelected: Boolean, shapeModel: ShapeModel) {
    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.padding(4.dp)) {
        val density = LocalDensity.current
        val layoutDirection = LocalLayoutDirection.current
        val color = if (isSelected) Color.Cyan else Color.LightGray
        Box(modifier = Modifier
            .border(3.dp, Color.Red)
            .size(70.dp)
            .padding(4.dp)
            .drawWithContent {

                val outline = shapeModel.shape.createOutline(
                    size = size,
                    layoutDirection = layoutDirection,
                    density = density
                )

                val width = size.width
                val height = size.height
                val outlineWidth = outline.bounds.width
                val outlineHeight = outline.bounds.height

                translate(
                    left = (width - outlineWidth) / 2,
                    top = (height - outlineHeight) / 2
                ) {
                    drawOutline(outline = outline, color = color, style = Stroke(3.dp.toPx()))
                }
            }
        )
        Text(text = shapeModel.title, color = color, fontSize = 14.sp)
    }
}