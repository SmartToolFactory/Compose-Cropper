package com.smarttoolfactory.composecropper.preferences

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.smarttoolfactory.animatedlist.AnimatedInfiniteLazyRow
import com.smarttoolfactory.animatedlist.model.AnimationProgress
import com.smarttoolfactory.cropper.model.CropShape
import com.smarttoolfactory.cropper.model.shapes
import com.smarttoolfactory.cropper.widget.ShapeSelectionCard

@Composable
fun AnimatedShapeSelection(
    modifier: Modifier = Modifier,
    initialSelectedIndex: Int = 2,
    onCropShapeChange: (CropShape) -> Unit
) {

    var currentIndex by remember { mutableStateOf(initialSelectedIndex) }

    AnimatedInfiniteLazyRow(
        modifier = modifier.padding(horizontal = 10.dp),
        items = shapes,
        inactiveItemPercent = 80,
        initialFirstVisibleIndex = initialSelectedIndex - 2
    ) { animationProgress: AnimationProgress, index: Int, item: CropShape, width: Dp ->

        val scale = animationProgress.scale
        val color = animationProgress.color
        val selectedLocalIndex = animationProgress.itemIndex

        ShapeSelectionCard(modifier = Modifier
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
            }
            .width(width),
            color = color,
            cropShape = item
        )

        if (currentIndex != selectedLocalIndex) {
            currentIndex = selectedLocalIndex
            onCropShapeChange(shapes[selectedLocalIndex])
        }
    }
}
