package com.smarttoolfactory.cropper.settings.frames.edit

import androidx.compose.foundation.layout.*
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Slider
import androidx.compose.material3.TextField
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import com.smarttoolfactory.cropper.model.AspectRatio
import com.smarttoolfactory.cropper.model.PolygonCropShape
import com.smarttoolfactory.cropper.util.createPolygonShape

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

    val shape by remember(sides, angle) {
        derivedStateOf {
            createPolygonShape(sides = sides, angle)
        }
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
                .drawOutlineWithCache(
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
            value = sides.toFloat(),
            onValueChange = { sides = it.toInt() },
            valueRange = 3f..15f,
            steps = 10
        )
        Slider(
            value = angle,
            onValueChange = { angle = it },
            valueRange = 0f..360f
        )


    }
}
