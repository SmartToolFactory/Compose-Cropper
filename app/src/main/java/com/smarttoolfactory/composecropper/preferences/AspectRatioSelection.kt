package com.smarttoolfactory.composecropper.preferences

import androidx.compose.foundation.layout.width
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.Dp
import com.smarttoolfactory.animatedlist.AnimatedInfiniteLazyRow
import com.smarttoolfactory.animatedlist.model.AnimationProgress
import com.smarttoolfactory.cropper.model.AspectRatioModel
import com.smarttoolfactory.cropper.model.aspectRatios
import com.smarttoolfactory.cropper.widget.ShapeSelection

@Composable
internal fun AnimatedAspectRatioSelection(
    modifier: Modifier = Modifier,
    initialSelectedIndex: Int = 2,
    onAspectRatioChange: (AspectRatioModel) -> Unit
) {

    var currentIndex by remember { mutableStateOf(initialSelectedIndex) }

    AnimatedInfiniteLazyRow(
        modifier = modifier,
        items = aspectRatios,
        initialFirstVisibleIndex = initialSelectedIndex - 2
    ) { animationProgress: AnimationProgress, index: Int, item: AspectRatioModel, width: Dp ->

        val scale = animationProgress.scale
        val color = animationProgress.color
        val selectedLocalIndex = animationProgress.itemIndex

        ShapeSelection(modifier = Modifier
            .graphicsLayer {
                scaleY = scale
            }
            .width(width),
            color = color,
            shapeModel = item
        )

        if (currentIndex != selectedLocalIndex) {
            currentIndex = selectedLocalIndex
            onAspectRatioChange(aspectRatios[selectedLocalIndex])
        }
    }
}