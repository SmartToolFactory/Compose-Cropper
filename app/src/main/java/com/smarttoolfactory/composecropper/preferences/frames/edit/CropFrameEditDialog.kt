package com.smarttoolfactory.composecropper.preferences.frames.edit

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.*
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.res.imageResource
import com.smarttoolfactory.cropper.R
import com.smarttoolfactory.cropper.model.*

@Composable
fun CropFrameEditDialog(
    aspectRatio: AspectRatio,
    index: Int,
    cropFrame: CropFrame,
    onConfirm: (CropFrame) -> Unit,
    onDismiss: () -> Unit
) {

    val dstBitmap = ImageBitmap.imageResource(id = R.drawable.landscape2)

    val outlineType = cropFrame.outlineType

    var outline: CropOutline by remember {
        mutableStateOf(cropFrame.outlines[index])
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
                OutlineType.ImageMask -> {
                    val imageMaskOutline = outline as ImageMaskOutline
                    ImageMaskEdit(imageMaskOutline) {
                        outline = it
                    }
                }
                OutlineType.Custom -> {
                    val customPathOutline = outline as CustomPathOutline
                    CustomPathEdit(
                        aspectRatio = aspectRatio,
                        dstBitmap = dstBitmap,
                        customPathOutline = customPathOutline,
                    ) {
                        outline = it
                    }
                }
                else -> Unit
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                val newOutlines: List<CropOutline> = cropFrame.outlines
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
                Text("Accept")
            }
        },
        dismissButton = {
            TextButton(
                onClick = { onDismiss() }
            ) {
                Text(text = "Cancel")
            }
        }
    )
}
