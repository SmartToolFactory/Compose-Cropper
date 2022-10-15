package com.smarttoolfactory.composecropper.preferences.frames.edit

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CutCornerShape
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import com.smarttoolfactory.composecropper.preferences.CropTextField
import com.smarttoolfactory.composecropper.preferences.SliderWithValueSelection
import com.smarttoolfactory.cropper.model.AspectRatio
import com.smarttoolfactory.cropper.model.CornerRadiusProperties
import com.smarttoolfactory.cropper.model.CutCornerCropShape
import com.smarttoolfactory.cropper.util.drawOutlineWithBlendModeAndChecker
import kotlin.math.roundToInt

@Composable
internal fun CutCornerCropShapeEdit(
    aspectRatio: AspectRatio,
    dstBitmap: ImageBitmap,
    title: String,
    cutCornerCropShape: CutCornerCropShape,
    onChange: (CutCornerCropShape) -> Unit
) {

    var newTitle by remember {
        mutableStateOf(title)
    }

    val cornerRadius = remember {
        cutCornerCropShape.cornerRadius
    }

    var topStartPercent by remember {
        mutableStateOf(
            cornerRadius.topStartPercent.toFloat()
        )
    }

    var topEndPercent by remember {
        mutableStateOf(
            cornerRadius.topEndPercent.toFloat()
        )
    }

    var bottomStartPercent by remember {
        mutableStateOf(
            cornerRadius.bottomStartPercent.toFloat()
        )
    }

    var bottomEndPercent by remember {
        mutableStateOf(
            cornerRadius.bottomEndPercent.toFloat()
        )
    }

    val shape by remember {
        derivedStateOf {
            CutCornerShape(
                topStartPercent = topStartPercent.toInt(),
                topEndPercent = topEndPercent.toInt(),
                bottomStartPercent = bottomStartPercent.toInt(),
                bottomEndPercent = bottomEndPercent.toInt()
            )
        }
    }

    onChange(
        cutCornerCropShape.copy(
            cornerRadius = CornerRadiusProperties(
                topStartPercent = topStartPercent.toInt(),
                topEndPercent = topEndPercent.toInt(),
                bottomStartPercent = bottomStartPercent.toInt(),
                bottomEndPercent = bottomEndPercent.toInt()
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

        CropTextField(
            value = newTitle,
            onValueChange = { newTitle = it }
        )

        Spacer(modifier = Modifier.height(10.dp))

        SliderWithValueSelection(
            value = topStartPercent,
            title = "Top Start",
            text = "${(topStartPercent * 10f).roundToInt() / 10f}%",
            onValueChange = { topStartPercent = it },
            valueRange = 0f..100f
        )
        SliderWithValueSelection(
            value = topEndPercent,
            title = "Top End",
            text = "${(topEndPercent * 10f).roundToInt() / 10f}%",
            onValueChange = { topEndPercent = it },
            valueRange = 0f..100f
        )
        SliderWithValueSelection(
            value = bottomStartPercent,
            title = "Bottom Start",
            text = "${(bottomStartPercent * 10f).roundToInt() / 10f}%",
            onValueChange = { bottomStartPercent = it },
            valueRange = 0f..100f
        )
        SliderWithValueSelection(
            value = bottomEndPercent,
            title = "Bottom End",
            text = "${(bottomEndPercent * 10f).roundToInt() / 10f}%",
            onValueChange = { bottomEndPercent = it },
            valueRange = 0f..100f
        )
    }
}
