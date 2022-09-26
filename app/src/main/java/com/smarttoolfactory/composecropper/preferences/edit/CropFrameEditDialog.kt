package com.smarttoolfactory.composecropper.preferences

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.smarttoolfactory.cropper.model.*

@Composable
fun CropFrameEditDialog(
    cropFrame: CropFrame,
    onConfirm: (CropFrame) -> Unit,
    onDismiss: () -> Unit
) {

    val index = 0

    val outlineType = cropFrame.outlineType
    var outline: CropOutline by remember {
        mutableStateOf(cropFrame.cropOutlineContainer.outlines[index])
    }

    AlertDialog(onDismissRequest = onDismiss,
        text = {
            if (outlineType == OutlineType.RoundedRect) {
                val shape = outline as RoundedCornerCropShape

                RoundedRectEdit(
                    roundedCornerCropShape = shape
                ) {
                    outline = it

                }
            }
        },
        confirmButton = {
            Button(onClick = {

                val newOutlines: List<CropOutline> = cropFrame.cropOutlineContainer
                    .outlines
                    .toMutableList()
                    .apply {
                        set(index, outline)
                    }
                    .toList()


                val newCropFrame = cropFrame.copy(
                    cropOutlineContainer = getOutlineContainer(outlineType, index, newOutlines)
                )

                onConfirm(newCropFrame)
            }) {
                Text("Confirm")
            }
        },
        dismissButton = {
            Button(onClick = { onDismiss() }) {
                Text(text = "Dismiss")
            }
        }
    )
}

@Composable
private fun RoundedRectEdit(
    roundedCornerCropShape: RoundedCornerCropShape,
    onChange: (RoundedCornerCropShape) -> Unit
) {

    val cornerRadius = remember {
        roundedCornerCropShape.cornerRadius
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
            RoundedCornerShape(
                topStartPercent = topStartPercent.toInt(),
                topEndPercent = topEndPercent.toInt(),
                bottomStartPercent = bottomStartPercent.toInt(),
                bottomEndPercent = bottomEndPercent.toInt()
            )
        }
    }

    onChange(
        roundedCornerCropShape.copy(
            cornerRadius = CornerRadiusProperties(
                topStartPercent = topStartPercent.toInt(),
                topEndPercent = topEndPercent.toInt(),
                bottomStartPercent = bottomStartPercent.toInt(),
                bottomEndPercent = bottomEndPercent.toInt()
            ),
            shape = shape
        )
    )

    Column {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(4 / 3f)
                .border(4.dp, MaterialTheme.colorScheme.tertiary, shape)
        )

        Slider(
            value = topStartPercent,
            onValueChange = { topStartPercent = it },
            valueRange = 0f..100f
        )
        Slider(
            value = topEndPercent,
            onValueChange = { topEndPercent = it },
            valueRange = 0f..100f
        )
        Slider(
            value = bottomStartPercent,
            onValueChange = { bottomStartPercent = it },
            valueRange = 0f..100f
        )
        Slider(
            value = bottomEndPercent,
            onValueChange = { bottomEndPercent = it },
            valueRange = 0f..100f
        )
    }
}

@Suppress("UNCHECKED_CAST")
private fun getOutlineContainer(
    outlineType: OutlineType,
    index: Int,
    outlines: List<CropOutline>
): CropOutlineContainer<out CropShape> {
    return when (outlineType) {
        OutlineType.RoundedRect -> {
            RoundedRectOutlineContainer(
                selectedIndex = index,
                outlines = outlines as List<RoundedCornerCropShape>
            )
        }
        OutlineType.CutCorner -> {
            CutCornerRectOutlineContainer(
                selectedIndex = index,
                outlines = outlines as List<CutCornerCropShape>
            )
        }
        else -> {
            RectOutlineContainer(
                selectedIndex = index,
                outlines = outlines as List<RectCropShape>
            )
        }
    }
}