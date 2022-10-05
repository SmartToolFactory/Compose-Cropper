package com.smarttoolfactory.composecropper.preferences.frames.edit

import androidx.compose.foundation.layout.*
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.TextField
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import com.smarttoolfactory.composecropper.preferences.SliderWithValueSelection
import com.smarttoolfactory.cropper.model.AspectRatio
import com.smarttoolfactory.cropper.model.PolygonCropShape
import com.smarttoolfactory.cropper.util.createPolygonShape
import com.smarttoolfactory.cropper.util.drawOutlineWithBlendModeAndChecker

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun PolygonCropShapeEdit(
    aspectRatio: AspectRatio,
    dstBitmap: ImageBitmap,
    title: String,
    polygonCropShape: PolygonCropShape,
    onChange: (PolygonCropShape) -> Unit
) {

    var newTitle by remember {
        mutableStateOf(title)
    }

    val polygonProperties = remember {
        polygonCropShape.polygonProperties
    }

    var sides by remember {
        mutableStateOf(
            polygonProperties.sides
        )
    }

    var angle by remember {
        mutableStateOf(
            polygonProperties.angle
        )
    }

    var shape by remember {
        mutableStateOf(
            polygonCropShape.shape
        )
    }

    onChange(
        polygonCropShape.copy(
            polygonProperties = polygonProperties.copy(
                sides = sides,
                angle = angle
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

        SliderWithValueSelection(
            value = sides.toFloat(),
            title="Sides",
            text = "$sides",
            onValueChange = {
                sides = it.toInt()
                shape = createPolygonShape(sides = sides, angle)
            },
            valueRange = 3f..15f
        )

        SliderWithValueSelection(
            value = angle,
            title="Angle",
            text = "${angle.toInt()}°",
            onValueChange = {
                angle = it
                shape = createPolygonShape(sides = sides, degrees = angle)
            },
            valueRange = 0f..360f
        )
    }
}
