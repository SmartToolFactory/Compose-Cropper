package com.smarttoolfactory.cropper.widget

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawOutline
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.smarttoolfactory.cropper.model.CropShape


@Composable
fun ShapeSelectionCard(
    modifier: Modifier = Modifier,
    color: Color,
    fontSize:TextUnit = 12.sp,
    cropShape: CropShape
) {
    Box(
        modifier = modifier
            .background(Color.White)
            .padding(4.dp)
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            val density = LocalDensity.current
            val layoutDirection = LocalLayoutDirection.current
            Box(modifier = Modifier
                .fillMaxWidth()
                .padding(4.dp)
                .aspectRatio(1f)
                .drawWithContent {

                    val outline = cropShape.shape.createOutline(
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
                        drawOutline(
                            outline = outline,
                            color = color,
                            style = Stroke(3.dp.toPx())
                        )
                    }
                }
            )
           if(cropShape.title.isNotEmpty()){
               Text(text = cropShape.title, color = color, fontSize = fontSize)
           }
        }
    }
}

@Composable
fun ShapeSelectionCard(
    modifier: Modifier = Modifier,
    isSelected: Boolean,
    activeColor: Color = Color.Red,
    inactiveColor: Color = Color.LightGray,
    fontSize:TextUnit = 14.sp,
    cropShape: CropShape
) {
    Box(
        modifier = modifier
            .shadow(1.dp, RoundedCornerShape(8.dp))
            .background(Color.White)
            .padding(4.dp)
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            val density = LocalDensity.current
            val layoutDirection = LocalLayoutDirection.current
            val color = if (isSelected) activeColor else inactiveColor
            Box(modifier = Modifier
                .fillMaxWidth()
                .padding(4.dp)
                .aspectRatio(1f)
                .drawWithContent {

                    val outline = cropShape.shape.createOutline(
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
                        drawOutline(
                            outline = outline,
                            color = color,
                            style = Stroke(3.dp.toPx())
                        )
                    }
                }
            )
            if(cropShape.title.isNotEmpty()){
                Text(text = cropShape.title, color = color, fontSize = fontSize)
            }        }
    }
}
