@file:OptIn(ExperimentalSnapperApi::class)

package com.smarttoolfactory.cropper.widget

import android.animation.ArgbEvaluator
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import dev.chrisbanes.snapper.ExperimentalSnapperApi
import dev.chrisbanes.snapper.rememberSnapperFlingBehavior
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
    selectorIndex: Int = visibleItemCount / 2,
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

    // Index of selector(item that is selected)  in circular list
    val indexOfSelector = selectorIndex.coerceIn(0, visibleItemCount - 1)

    // number of items
    val totalItemCount = items.size

    BoxWithConstraints(modifier = modifier.padding(contentPadding)) {

        val rowWidth = constraints.maxWidth

        val density = LocalDensity.current
        val spaceBetweenItemsPx = density.run { spaceBetweenItems.toPx() }

        val itemWidth = (rowWidth - spaceBetweenItemsPx * (visibleItemCount - 1)) / visibleItemCount
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
            ) { globalIndex ->

                val animationData by remember {
                    derivedStateOf {
                        val animationData = getAnimationData(
                            lazyListState = lazyListState,
                            argbEvaluator = argbEvaluator,
                            indexOfSelector = indexOfSelector,
                            globalIndex = globalIndex,
                            selectedIndex = selectedIndex,
                            itemWidth = itemWidth,
                            spaceBetweenItems = spaceBetweenItemsPx,
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
                itemContent(animationData, globalIndex, itemWidthDp)
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
 * @param indexOfSelector global index of element of selector of infinite items. Item with
 * this index is selected item
 * @param globalIndex index of current item. This index changes for every item in list
 * that calls this function
 * @param selectedIndex global index of currently selected item. This index changes only
 * when selected item is changed due to scroll
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
    indexOfSelector: Int,
    globalIndex: Int,
    selectedIndex: Int,
    itemWidth: Float,
    spaceBetweenItems: Float,
    visibleItemCount: Int,
    totalItemCount: Int,
    lowerBound: Float,
    inactiveColor: Int,
    activeColor: Int,
): AnimationData {

    // Half width of an item
    val halfItemWidth = itemWidth / 2

    // Position of left of selector item
    // Selector is item that is considered as selected
    val selectorPosX = indexOfSelector * (itemWidth + spaceBetweenItems)

    val visibleItems = lazyListState.layoutInfo.visibleItemsInfo
    val currentItem: LazyListItemInfo? = visibleItems.firstOrNull { it.index == globalIndex }

    // Convert global indexes to indexes in range of 0..visibleItemCount
    val localIndex = (globalIndex + visibleItemCount / 2) % visibleItemCount

    // Get offset of each item relative to start of list x=0 position
    val itemOffset =
        currentItem?.offset
            ?: (localIndex * itemWidth + localIndex * spaceBetweenItems).toInt()

    // Check how far this item is to selector index.
    val distanceToSelector = (selectorPosX - itemOffset).absoluteValue


    val scaleRegionWidth = (itemWidth + spaceBetweenItems)

    // Current item is not close to center item or one half of left or right
    // item
    var scale = calculateScale(
        distanceToSelector,
        scaleRegionWidth,
        lowerBound
    )


    // This is the fraction between lower bound and 1f. If lower bound is .9f we have
    // range of .9f..1f for scale calculation
    val lowerBoundToEndInterval = 1 - lowerBound

    // Scale for color when scale is at lower bound color scale is zero
    // when scale reaches upper bound(1f) color scale is 1f which is target color
    // when argEvaluator evaluates color
    var colorScale = (scale - lowerBound) / lowerBoundToEndInterval

    var distance = Int.MAX_VALUE

    var globalSelectedIndex = selectedIndex

    visibleItems.forEach {
        val x = (it.offset - selectorPosX - halfItemWidth).absoluteValue
        if (x < distance) {
            distance = x.toInt()
            globalSelectedIndex = it.index
        }
    }

    // Index of item in list. If list has 7 item. initially we item index is 3
    // When selector changes we get what it(in infinite list) corresponds to in item list
    val itemIndex = if (globalSelectedIndex > 0) {
        globalSelectedIndex % totalItemCount
    } else {
        indexOfSelector
    }

    // Selector index -1 because visibleItems are returned asynchronously because of
    // that they are not in Composition. So we set initial value for selected item to not
    // jump unexpectedly from unselected to selected value
    if (globalSelectedIndex == -1 && globalIndex == Int.MAX_VALUE / 2 + indexOfSelector) {
        scale = 1f
        colorScale = 1f
    }

    val color: Int = argbEvaluator.evaluate(
        colorScale, inactiveColor, activeColor
    ) as Int

    return AnimationData(
        scale = scale, color = Color(color), listIndex = globalSelectedIndex, itemIndex = itemIndex
    )
}

private fun calculateScale(
    distanceToSelector: Float,
    scaleRegionWidth: Float,
    lowerBound: Float
): Float {
    return if (distanceToSelector < scaleRegionWidth) {
        // Now item is in scale region. Check where exactly it is in this region for animation
        val fraction = (scaleRegionWidth - distanceToSelector) / scaleRegionWidth
        // scale based on lower bound and 1f.
        // If lower bound .9f and fraction is 50% our scale is .9f + .1f*50/100 = .95f
        lowerBound + fraction * (1 - lowerBound)
    } else {
        lowerBound

    }.coerceIn(lowerBound, 1f)
}
