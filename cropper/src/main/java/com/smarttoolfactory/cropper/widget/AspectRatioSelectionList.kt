package com.smarttoolfactory.cropper.widget

import android.animation.ArgbEvaluator
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalDensity
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
    modifier: Modifier = Modifier
) {
    val lazyListState = rememberLazyListState(Int.MAX_VALUE / 2)
    val flingBehavior = rememberSnapperFlingBehavior(
        lazyListState = lazyListState
    )

    BoxWithConstraints(
        modifier = Modifier
            .background(Color.Gray)
            .padding(top = 30.dp)
    ) {

        val space = 4.dp
        val itemCount = 5

        val width = constraints.maxWidth
        val center = width / 2

        val density = LocalDensity.current
        val spaceBetween = density.run { space.toPx() }

        val itemWidth = (width - spaceBetween * (itemCount - 1)) / itemCount
        val itemWidthDp = density.run { itemWidth.toDp() }

        var selectedIndex by remember {
            mutableStateOf((Int.MAX_VALUE % aspectRatios.size) / 2)
        }

        val argbEvaluator = remember { ArgbEvaluator() }


        val visibleItems by remember {
            derivedStateOf {
                lazyListState.layoutInfo.visibleItemsInfo
            }
        }

        val currentIndex by remember {
            derivedStateOf {
                var distance = Int.MAX_VALUE

                visibleItems.forEach {
                    val x = abs(it.offset - center + itemWidth / 2)
                    if (x < distance) {
                        distance = x.toInt()
                        selectedIndex = it.index
                    }
                }

                selectedIndex
            }
        }

        Column(Modifier) {
            LazyRow(
                modifier = modifier,
                state = lazyListState,
                horizontalArrangement = Arrangement.spacedBy(space),
                flingBehavior = flingBehavior
            ) {
                items(Int.MAX_VALUE) { i ->

                    // Get which item in list this is
                    val index = i % aspectRatios.size

                    // Get offset this item relative to start of the LazyRow
                    // Sometime in scroll direction one item that is not visible yet
                    // is sub-composed because of that visible items might not match
                    // with current one and returns null.
                    // when last item is 8th, 9th item gets composed and we see index 9 here
                    // while visible last item is 8
                    val itemOffset = (visibleItems.firstOrNull { it.index == i }?.offset ?: 0)

                    val pageOffset = ((itemOffset - center + itemWidth / 2) / center)
                        .absoluteValue
                        .coerceIn(0f, 1f)


                    val lowerBound = .9f
                    val lowerBoundToEndInterval = 1 - lowerBound

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

                    val color: Int = argbEvaluator.evaluate(
                        colorScale,
                        android.graphics.Color.GRAY,
                        android.graphics.Color.CYAN
                    ) as Int


                    ShapeSelection(
                        modifier = Modifier
                            .graphicsLayer {
                                scaleY = scale
                                alpha = scale
                            }
                            .width(itemWidthDp),
                        color = Color(color),
                        shapeModel = aspectRatios[index]
                    )
                }
            }
        }
    }
}
