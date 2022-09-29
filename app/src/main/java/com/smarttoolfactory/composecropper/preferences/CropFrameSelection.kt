package com.smarttoolfactory.composecropper.preferences

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.smarttoolfactory.animatedlist.AnimatedInfiniteLazyRow
import com.smarttoolfactory.animatedlist.model.AnimationProgress
import com.smarttoolfactory.cropper.model.AspectRatio
import com.smarttoolfactory.cropper.model.CropFrame
import com.smarttoolfactory.cropper.model.OutlineType
import com.smarttoolfactory.cropper.settings.CropFrameFactory
import com.smarttoolfactory.cropper.settings.CropOutlineProperty
import com.smarttoolfactory.cropper.settings.frames.edit.CropFrameListDialog
import com.smarttoolfactory.cropper.widget.CropFrameDisplayCard

/**
 * Crop frame selection
 */
@Composable
fun CropFrameSelection(
    aspectRatio: AspectRatio,
    cropFrameFactory: CropFrameFactory,
    cropOutlineProperty: CropOutlineProperty,
    conCropOutlinePropertyChange: (CropOutlineProperty) -> Unit
) {

    var showEditDialog by remember { mutableStateOf(false) }

    var cropFrame by remember {
        mutableStateOf(
            cropFrameFactory.getCropFrame(cropOutlineProperty.outlineType)
        )
    }

    if (showEditDialog) {
        CropFrameListDialog(
            aspectRatio = aspectRatio,
            cropFrame = cropFrame,
            onDismiss = {
                cropFrame = it
                cropFrameFactory.editCropFrame(cropFrame)

                conCropOutlinePropertyChange(
                    CropOutlineProperty(
                        it.outlineType,
                        it.cropOutlineContainer.selectedItem
                    )
                )
                showEditDialog = false
            }
        )
    }

    val initialIndex = remember {
        OutlineType.values().indexOfFirst {
            it == cropOutlineProperty.outlineType
        }
    }

    CropFrameSelectionList(
        modifier = Modifier.fillMaxWidth(),
        cropFrames = cropFrameFactory.getCropFrames(),
        initialSelectedIndex = initialIndex,
        onClick = {
            cropFrame = it
            showEditDialog = true
        },
        onCropFrameChange = {
            conCropOutlinePropertyChange(
                CropOutlineProperty(
                    it.outlineType,
                    it.cropOutlineContainer.selectedItem
                )
            )
        }
    )
}

/**
 * Animated list for selecting [CropFrame]
 */
@Composable
private fun CropFrameSelectionList(
    modifier: Modifier = Modifier,
    initialSelectedIndex: Int = 2,
    cropFrames: List<CropFrame>,
    onClick: (CropFrame) -> Unit,
    onCropFrameChange: (CropFrame) -> Unit
) {

    var currentIndex by remember { mutableStateOf(initialSelectedIndex) }

    AnimatedInfiniteLazyRow(
        modifier = modifier.padding(horizontal = 10.dp),
        items = cropFrames,
        inactiveItemPercent = 80,
        initialFirstVisibleIndex = initialSelectedIndex - 2,
    ) { animationProgress: AnimationProgress, _, item: CropFrame, width: Dp ->

        val scale = animationProgress.scale
        val color = animationProgress.color

        val selectedLocalIndex = animationProgress.itemIndex
        val cropOutline = item.cropOutlineContainer.selectedItem

        val editable = item.editable

        CropFrameDisplayCard(
            modifier = Modifier.width(width),
            editable = editable,
            scale = scale,
            outlineColor = color,
            title = cropOutline.title,
            cropOutline = cropOutline
        ) {
            onClick(item)
        }

        if (currentIndex != selectedLocalIndex) {
            currentIndex = selectedLocalIndex
            onCropFrameChange(cropFrames[selectedLocalIndex])
        }
    }
}
