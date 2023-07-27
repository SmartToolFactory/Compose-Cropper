package com.smarttoolfactory.composecropper.preferences

import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.animateScrollBy
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.Dp
import com.smarttoolfactory.animatedlist.AnimatedInfiniteLazyRow
import com.smarttoolfactory.animatedlist.model.AnimationProgress
import com.smarttoolfactory.cropper.model.CropAspectRatio
import com.smarttoolfactory.cropper.model.aspectRatios
import com.smarttoolfactory.cropper.widget.AspectRatioSelectionCard
import kotlinx.coroutines.launch

@Composable
internal fun AnimatedAspectRatioSelection(
    modifier: Modifier = Modifier,
    initialSelectedIndex: Int = 2,
    onAspectRatioChange: (CropAspectRatio) -> Unit
) {

    var currentIndex by remember { mutableStateOf(initialSelectedIndex) }
    val coroutineScope = rememberCoroutineScope()

    AnimatedInfiniteLazyRow(
        modifier = modifier,
        items = aspectRatios,
        inactiveItemPercent = 80,
        initialFirstVisibleIndex = initialSelectedIndex - 2
    ) { animationProgress: AnimationProgress,
        index: Int,
        item: CropAspectRatio,
        width: Dp,
        lazyListState ->

        val scale = animationProgress.scale
        val color = animationProgress.color
        val selectedLocalIndex = animationProgress.itemIndex

        AspectRatioSelectionCard(modifier = Modifier
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
            }
            .clickable(
                interactionSource = remember {
                    MutableInteractionSource()
                },
                indication = null
            ) {
                coroutineScope.launch {
                    lazyListState.animateScrollBy(animationProgress.distanceToSelector)
                }
            }
            .width(width),
            contentColor = MaterialTheme.colorScheme.surface,
            color = color,
            cropAspectRatio = item
        )

        if (currentIndex != selectedLocalIndex) {
            currentIndex = selectedLocalIndex
            onAspectRatioChange(aspectRatios[selectedLocalIndex])
        }
    }
}