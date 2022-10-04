package com.smarttoolfactory.composecropper.preferences.frames.edit

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.GenericShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Slider
import androidx.compose.material3.TextField
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import com.smarttoolfactory.cropper.model.AspectRatio
import com.smarttoolfactory.cropper.model.OvalCropShape
import com.smarttoolfactory.cropper.util.drawOutlineWithBlendModeAndChecker


@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun OvalCropShapeEdit(
    aspectRatio: AspectRatio,
    dstBitmap: ImageBitmap,
    title: String,
    ovalCropShape: OvalCropShape,
    onChange: (OvalCropShape) -> Unit
) {

    var newTitle by remember {
        mutableStateOf(title)
    }

    val ovalProperties = remember {
        ovalCropShape.ovalProperties
    }

    var startAngle by remember {
        mutableStateOf(
            ovalProperties.startAngle
        )
    }

    var sweepAngle by remember {
        mutableStateOf(
            ovalProperties.sweepAngle
        )
    }

    var offsetX by remember {
        mutableStateOf(
            ovalProperties.offset.x
        )
    }

    var offsetY by remember {
        mutableStateOf(
            ovalProperties.offset.y
        )
    }

    val shape by remember(startAngle, sweepAngle) {
        derivedStateOf {
            GenericShape { size: Size, _: LayoutDirection ->
                val width = size.width
                val height = size.height
                val diameter = width.coerceAtMost(height)
                val left = (width - diameter) / 2
                val top = (height - diameter) / 2

                val rect = Rect(offset = Offset(left, top), size = Size(diameter, diameter))

                if (sweepAngle == 360f) {
                    addOval(rect)
                } else {
                    moveTo(size.width / 2, size.height / 2)
                    arcTo(rect, startAngle, sweepAngle, false)

                }

                close()
            }
        }
    }

    onChange(
        ovalCropShape.copy(
            ovalProperties = ovalProperties.copy(
                startAngle = startAngle,
                sweepAngle = sweepAngle
            ),
            title = newTitle,
            shape = shape
        )
    )

    Column {

        val density = LocalDensity.current
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(4 / 3f)
                .clipToBounds()
                .drawOutlineWithBlendModeAndChecker(
                    aspectRatio,
                    shape,
                    density,
                    dstBitmap
                )
        )

        TextField(
            value = newTitle,
            onValueChange = { newTitle = it }
        )

        Spacer(modifier = Modifier.height(10.dp))

        Slider(
            value = startAngle,
            onValueChange = { startAngle = it },
            valueRange = 0f..360f
        )
        Slider(
            value = sweepAngle,
            onValueChange = { sweepAngle = it },
            valueRange = 0f..360f
        )
        Slider(
            value = offsetX,
            onValueChange = { offsetX = it },
            valueRange = 0f..100f
        )
        Slider(
            value = offsetY,
            onValueChange = { offsetY = it },
            valueRange = 0f..100f
        )

    }
}
