package com.smarttoolfactory.cropper.settings.frames.edit

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.drawOutline
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.res.imageResource
import androidx.compose.ui.unit.Density
import com.smarttoolfactory.cropper.R
import com.smarttoolfactory.cropper.model.*
import com.smarttoolfactory.cropper.util.drawBlockWithCheckerAndLayer

@Composable
fun CropFrameEditDialog(
    aspectRatio: AspectRatio,
    index: Int = 0,
    cropFrame: CropFrame,
    onConfirm: (CropFrame) -> Unit,
    onDismiss: () -> Unit
) {

    val dstBitmap = ImageBitmap.imageResource(id = R.drawable.landscape2)

    val outlineType = cropFrame.outlineType

    var outline: CropOutline by remember {
        mutableStateOf(cropFrame.cropOutlineContainer.outlines[index])
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        text = {
            when (outlineType) {
                OutlineType.RoundedRect -> {

                    val shape = outline as RoundedCornerCropShape

                    RoundedCornerCropShapeEdit(
                        aspectRatio = aspectRatio,
                        dstBitmap = dstBitmap,
                        title = outline.title,
                        roundedCornerCropShape = shape
                    ) {
                        outline = it
                    }
                }
                OutlineType.CutCorner -> {
                    val shape = outline as CutCornerCropShape

                    CutCornerCropShapeEdit(
                        aspectRatio = aspectRatio,
                        dstBitmap = dstBitmap,
                        title = outline.title,
                        cutCornerCropShape = shape
                    ) {
                        outline = it
                    }
                }
                OutlineType.Oval -> {

                    val shape = outline as OvalCropShape

                    OvalCropShapeEdit(
                        aspectRatio = aspectRatio,
                        dstBitmap = dstBitmap,
                        title = outline.title,
                        ovalCropShape = shape
                    ) {
                        outline = it
                    }
                }
                OutlineType.Polygon -> {

                    val shape = outline as PolygonCropShape

                    PolygonCropShapeEdit(
                        aspectRatio = aspectRatio,
                        dstBitmap = dstBitmap,
                        title = outline.title,
                        polygonCropShape = shape
                    ) {
                        outline = it
                    }
                }
                else -> Unit
            }
        },
        confirmButton = {
            Button(onClick = {

                val newOutlines: List<CropOutline> = cropFrame.cropOutlineContainer.outlines
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

internal fun Modifier.drawOutlineWithCache(
    aspectRatio: AspectRatio,
    shape: Shape,
    density: Density,
    dstBitmap: ImageBitmap
) = this.then(
    Modifier.drawWithCache {

        val coefficient = .9f

        val width = size.width
        val height = size.height

        val value = aspectRatio.value

        val shapeSize = if (aspectRatio == AspectRatio.Unspecified) {
            Size(width * coefficient, height * coefficient)
        } else if (value > 1) {
            Size(
                width = coefficient * width,
                height = coefficient * width / value
            )
        } else {
            Size(width = coefficient * height * value, height = coefficient * height)
        }

        val left = (width - shapeSize.width) / 2
        val top = (height - shapeSize.height) / 2

        val outline = shape.createOutline(
            size = shapeSize,
            layoutDirection = layoutDirection,
            density = density
        )

        onDrawWithContent {
            drawBlockWithCheckerAndLayer(dstBitmap) {
                translate(left = left, top = top) {
                    drawOutline(
                        outline = outline,
                        color = Color.Red,
                    )
                }
            }
        }
    }
)

@Suppress("UNCHECKED_CAST")
private fun getOutlineContainer(
    outlineType: OutlineType,
    index: Int,
    outlines: List<CropOutline>
): CropOutlineContainer<out CropOutline> {
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

        OutlineType.Oval -> {
            OvalOutlineContainer(
                selectedIndex = index,
                outlines = outlines as List<OvalCropShape>
            )
        }

        OutlineType.Polygon -> {
            PolygonOutlineContainer(
                selectedIndex = index,
                outlines = outlines as List<PolygonCropShape>
            )
        }

        OutlineType.Custom -> {
            PolygonOutlineContainer(
                selectedIndex = index,
                outlines = outlines as List<PolygonCropShape>
            )
        }

        OutlineType.ImageMask -> {
            ImageMaskOutlineContainer(
                selectedIndex = index,
                outlines = outlines as List<ImageMaskOutline>
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
