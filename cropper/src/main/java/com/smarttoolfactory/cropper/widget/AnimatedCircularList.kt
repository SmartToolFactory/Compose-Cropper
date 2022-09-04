@file:OptIn(ExperimentalSnapperApi::class)

package com.smarttoolfactory.cropper.widget

import android.animation.ArgbEvaluator
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyItemScope
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import dev.chrisbanes.snapper.ExperimentalSnapperApi
import dev.chrisbanes.snapper.rememberSnapperFlingBehavior
import kotlin.math.abs
import kotlin.math.absoluteValue

/**
 *  Infinite list with color and scale animation for selecting aspect ratio
 *
 * @param items the data list
 * @param visibleItemCount count of items that are visible at any time
 * @param spaceBetweenItems padding between 2 items
 * @param contentPadding a padding around the whole content. This will add padding for the
 * content after it has been clipped, which is not possible via [modifier] param. You can use it
 * to add a padding before the first item or after the last one.
 * @param activeColor color of selected item
 * @param inactiveColor color of items are not selected
 * @param key a factory of stable and unique keys representing the item. Using the same key
 * for multiple items in the list is not allowed. Type of the key should be saveable
 * via Bundle on Android. If null is passed the position in the list will represent the key.
 * When you specify the key the scroll position will be maintained based on the key, which
 * means if you add/remove items before the current visible item the item with the given key
 * will be kept as the first visible one.
 * @param contentType a factory of the content types for the item. The item compositions of
 * the same type could be reused more efficiently. Note that null is a valid type and items of such
 * type will be considered compatible.
 * @param itemContent the content displayed by a single item
 */
@Composable
fun <T> AnimatedCircularList(
    modifier: Modifier = Modifier,
    items: List<T>,
    visibleItemCount: Int = 5,
    spaceBetweenItems: Dp = 4.dp,
    contentPadding: PaddingValues = PaddingValues(0.dp),
    activeColor: Color = Color.Cyan,
    inactiveColor: Color = Color.Gray,
    inactiveItemScale: Float = .85f,
    key: ((index: Int) -> Any)? = null,
    contentType: (index: Int) -> Any? = { null },
    itemContent: @Composable LazyItemScope.(AnimationData, Int, Dp) -> Unit,
) {

    val lazyListState = rememberLazyListState(Int.MAX_VALUE / 2)
    val flingBehavior = rememberSnapperFlingBehavior(
        lazyListState = lazyListState
    )

    val argbEvaluator = remember { ArgbEvaluator() }

    val selectedColor = remember(activeColor) { activeColor.toArgb() }
    val unSelectedColor = remember(inactiveColor) { inactiveColor.toArgb() }

    // Index of center  in circular list
    val indexOfCenter = Int.MAX_VALUE / 2 + visibleItemCount / 2

    // number of items
    val totalItemCount = items.size

    BoxWithConstraints(modifier = modifier.padding(contentPadding)) {

        val rowWidth = constraints.maxWidth

        val density = LocalDensity.current
        val spaceBetween = density.run { spaceBetweenItems.toPx() }

        val itemWidth = (rowWidth - spaceBetween * (visibleItemCount - 1)) / visibleItemCount
        val itemWidthDp = density.run { itemWidth.toDp() }

        var selectedIndex by remember {
            mutableStateOf(-1)
        }

        LazyRow(
            modifier = modifier,
            state = lazyListState,
            horizontalArrangement = Arrangement.spacedBy(spaceBetweenItems),
            flingBehavior = flingBehavior
        ) {

            items(
                count = Int.MAX_VALUE, key = key, contentType = contentType
            ) { index ->

                val animationData by remember {
                    derivedStateOf {
                        val animationData = getAnimationData(
                            lazyListState = lazyListState,
                            argbEvaluator = argbEvaluator,
                            indexOfCenter = indexOfCenter,
                            index = index,
                            selectorIndex = selectedIndex,
                            listWidth = rowWidth,
                            itemWidth = itemWidth,
                            visibleItemCount = visibleItemCount,
                            totalItemCount = totalItemCount,
                            lowerBound = inactiveItemScale,
                            inactiveColor = unSelectedColor,
                            activeColor = selectedColor
                        )

                        selectedIndex = animationData.listIndex
                        animationData
                    }
                }
                itemContent(animationData, index, itemWidthDp)
            }
        }
    }
}

/**
 * get color, scale and selected index for scroll progress for infinite or circular list with
 * [Int.MAX_VALUE] global index count
 *
 * @param lazyListState A state object that can be hoisted to control and observe scrolling
 * @param argbEvaluator evaluator can be used to perform type interpolation between
 * integer values that represent ARGB colors
 * @param indexOfCenter global index of element at the center of infinite items
 * @param index index of current item that is being placed and calls this function
 * @param selectorIndex global index of selected item
 * @param listWidth width of the [LazyRow]
 * @param itemWidth width of each item of [LazyRow]
 * @param visibleItemCount count of visible items on screen
 * @param totalItemCount count of items that are displayed in circular list
 * @param lowerBound lower scale that items can be scaled to. It should be less than 1f
 * @param inactiveColor color of items when they are not selected
 * @param activeColor color of item that is selected
 */
private fun getAnimationData(
    lazyListState: LazyListState,
    argbEvaluator: ArgbEvaluator,
    indexOfCenter: Int,
    index: Int,
    selectorIndex: Int,
    listWidth: Int,
    itemWidth: Float,
    visibleItemCount: Int,
    totalItemCount: Int,
    lowerBound: Float,
    inactiveColor: Int,
    activeColor: Int,
): AnimationData {

    val lowerBoundToEndInterval = 1 - lowerBound

    val centerOfList = listWidth / 2
    val selectorPosX = centerOfList - itemWidth / 2

    val visibleItems = lazyListState.layoutInfo.visibleItemsInfo

    // Get offset this item relative to start of the LazyRow
    // Sometime in scroll direction one item that is not visible yet
    // is sub-composed because of that visible items might not match
    // with current one and returns null.
    // when last item is 8th, 9th item gets composed and we see index 9 here
    // while visible last item is 8
    val itemOffset = (visibleItems.firstOrNull { it.index == index }?.offset ?: 0)


    // Absolute value of fraction of Offset of items based on length of LazyRow.
    // Item in center has 0f, this number grows bigger on both sides of center
    val pageOffset = ((itemOffset - selectorPosX) / centerOfList).absoluteValue.coerceIn(0f, 1f)


    // When offset of an element is in range of. For a list with 5 visible items
    // it's 0.4f. Which means (half width) + center item width + (half width) is region
    // for scaling and color change happens. When an item is in this region it's eligible
    // for animation based on distance to selector position
    val scaleRegion = 2f / visibleItemCount

    // Current item is not close to center item or one half of left or right
    // item

    var scale = if (pageOffset > scaleRegion) {
        lowerBound
    } else {
        // Now item is in scale region. Check where exactly it is in this region for animation
        val fraction = (scaleRegion - pageOffset) / scaleRegion
        // scale based on lower bound and 1f.
        // If lower bound .9f and fraction is 50% our scale is .9f + .1f*50/100 = .95f
        lowerBound + fraction * lowerBoundToEndInterval
    }.coerceIn(lowerBound, 1f)


    // Scale for color when scale is at lower bound color scale is zero
    // when scale reaches upper bound(1f) color scale is 1f which is target color
    // when argEvaluator evaluates color
    var colorScale = (scale - lowerBound) / lowerBoundToEndInterval

    var distance = Int.MAX_VALUE

    var newSelectorIndex = selectorIndex

    visibleItems.forEach {
        val x = abs(it.offset - selectorPosX)
        if (x < distance) {
            distance = x.toInt()
            newSelectorIndex = it.index
        }
    }


    // Index of item in list. If list has 7 item. initially we item index is 3
    // When selector changes we get what it(in infinite list) corresponds to in item list
    val itemIndex = if (newSelectorIndex > 0) {
        newSelectorIndex % totalItemCount
    } else {
        indexOfCenter % totalItemCount
    }

    // Selector index -1 because visibleItems are returned asynchronously because of
    // that not available on Composition. So we set initial values for these values to not
    // jump unexpectedly
    if (newSelectorIndex == -1 && index == Int.MAX_VALUE / 2 + visibleItemCount / 2) {
        scale = 1f
        colorScale = 1f
    }

    val color: Int = argbEvaluator.evaluate(
        colorScale, inactiveColor, activeColor
    ) as Int

    return AnimationData(
        scale = scale, color = Color(color), listIndex = newSelectorIndex, itemIndex = itemIndex
    )
}
