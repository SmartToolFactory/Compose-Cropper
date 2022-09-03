package com.smarttoolfactory.cropper.widget

import android.animation.ArgbEvaluator
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.smarttoolfactory.cropper.model.aspectRatios
import dev.chrisbanes.snapper.ExperimentalSnapperApi
import dev.chrisbanes.snapper.rememberSnapperFlingBehavior
import kotlin.math.abs
import kotlin.math.absoluteValue

/**
 * Add infinite list with color and scale animation for selecting aspect ratio
 */
@OptIn(ExperimentalSnapperApi::class)
@Composable
fun AspectRatioSelectionList(
    modifier: Modifier = Modifier,
    visibleItemCount: Int = 5,
    spaceBetweenItems: Dp = 4.dp,
    contentPaddingValues: PaddingValues = PaddingValues(0.dp),
    activeColor: Color = Color.Cyan,
    inactiveColor: Color = Color.Gray,
    inactiveScale: Float = .85f,
    onSelectedItemChange: (Int) -> Unit
) {
    val lazyListState = rememberLazyListState(Int.MAX_VALUE / 2)
    val flingBehavior = rememberSnapperFlingBehavior(
        lazyListState = lazyListState
    )

    val argbEvaluator = remember { ArgbEvaluator() }

    val lowerBound = inactiveScale
    val space = spaceBetweenItems
    val itemCount = visibleItemCount

    val indexOfCenter = Int.MAX_VALUE / 2 + itemCount / 2

    BoxWithConstraints(modifier = modifier) {


        val width = constraints.maxWidth
        val center = width / 2

        val density = LocalDensity.current
        val spaceBetween = density.run { space.toPx() }

        val itemWidth = (width - spaceBetween * (itemCount - 1)) / itemCount
        val itemWidthDp = density.run { itemWidth.toDp() }

        var selectedIndex by remember {
            mutableStateOf(-1)
        }

        Column(Modifier) {
            LazyRow(
                modifier = modifier,
                state = lazyListState,
                horizontalArrangement = Arrangement.spacedBy(space),
                flingBehavior = flingBehavior
            ) {
                items(Int.MAX_VALUE) { i ->

                    val animationData by remember {
                        derivedStateOf {
                            val animationData = getAnimationData(
                                lazyListState,
                                i,
                                center,
                                itemWidth,
                                itemCount,
                                lowerBound = lowerBound
                            )

                            val itemIndex = if (selectedIndex > 0) {
                                selectedIndex % aspectRatios.size
                            } else {
                                indexOfCenter % aspectRatios.size
                            }

                            onSelectedItemChange(itemIndex)

                            animationData
                        }

                    }

                    selectedIndex = animationData.currentIndex

                    var scale = animationData.scale
                    var colorScale = animationData.colorScale

                    if (selectedIndex == -1 && i == Int.MAX_VALUE / 2 + itemCount / 2) {
                        scale = 1f
                        colorScale = 1f

                    }

                    val color: Int = argbEvaluator.evaluate(
                        colorScale, android.graphics.Color.GRAY, android.graphics.Color.CYAN
                    ) as Int


                    // Get which item in list this is
                    val index = i % aspectRatios.size

                    ShapeSelection(modifier = Modifier
                        .graphicsLayer {
                            scaleY = scale
                            alpha = scale
                        }
                        .width(itemWidthDp),
                        color = Color(color),
                        shapeModel = aspectRatios[index])
                }
            }
        }
    }
}

private fun getAnimationData(
    lazyListState: LazyListState,
    index: Int,
    center: Int,
    itemWidth: Float,
    itemCount: Int,
    lowerBound: Float,
): AnimationData {

    val lowerBoundToEndInterval = 1 - lowerBound

    val visibleItems = lazyListState.layoutInfo.visibleItemsInfo

    // Get offset this item relative to start of the LazyRow
    // Sometime in scroll direction one item that is not visible yet
    // is sub-composed because of that visible items might not match
    // with current one and returns null.
    // when last item is 8th, 9th item gets composed and we see index 9 here
    // while visible last item is 8
    val itemOffset = (visibleItems.firstOrNull { it.index == index }?.offset ?: 0)


    val pageOffset = ((itemOffset - center + itemWidth / 2) / center).absoluteValue.coerceIn(0f, 1f)


    // When offset of an element is in range of
    val scaleRegion = 2f / itemCount

    // Current item is not close to center item or one half of left or right
    // item

    val scale = if (pageOffset > scaleRegion) {
        lowerBound
    } else {
        val fraction = (scaleRegion - pageOffset) / scaleRegion
        lowerBound + fraction * lowerBoundToEndInterval
    }.coerceIn(lowerBound, 1f)


    // Scale for color when scale is at lower bound color scale is zero
    // when scale reaches upper bound(1f) color scale is 1f which is target color
    // when argEvaluator evaluates color
    val colorScale = (scale - lowerBound) / lowerBoundToEndInterval

    var distance = Int.MAX_VALUE
    var currentIndex = -1

    visibleItems.forEach {
        val x = abs(it.offset - center + itemWidth / 2)
        if (x < distance) {
            distance = x.toInt()
            currentIndex = it.index
        }
    }

    return AnimationData(scale = scale, colorScale = colorScale, currentIndex = currentIndex)
}

@Immutable
internal data class AnimationData(
    val scale: Float,
    val colorScale: Float,
    val currentIndex: Int
)