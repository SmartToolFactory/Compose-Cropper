@file:OptIn(ExperimentalSnapperApi::class)

package com.smarttoolfactory.cropper.widget

import android.animation.ArgbEvaluator
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.smarttoolfactory.cropper.model.AnimationProgress
import com.smarttoolfactory.cropper.widget.Orientation.Horizontal
import com.smarttoolfactory.cropper.widget.Orientation.Vertical
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
    initialFistVisibleIndex: Int = Int.MAX_VALUE / 2,
    lazyListState: LazyListState = rememberLazyListState(initialFistVisibleIndex),
    visibleItemCount: Int = 5,
    activeItemSize: Dp,
    inactiveItemSize: Dp,
    spaceBetweenItems: Dp = 4.dp,
    selectorIndex: Int = visibleItemCount / 2,
    contentPadding: PaddingValues = PaddingValues(0.dp),
    activeColor: Color = Color.Cyan,
    inactiveColor: Color = Color.Gray,
    orientation: Orientation = Horizontal,
    key: ((index: Int) -> Any)? = null,
    contentType: (index: Int) -> Any? = { null },
    itemContent: @Composable LazyItemScope.(
        animationProgress: AnimationProgress, index: Int, size: Dp
    ) -> Unit
) {
    val inactiveItemScale = activeItemSize.value / inactiveItemSize.value

    AnimatedCircularList(
        modifier = Modifier.fillMaxWidth(),
        items = items,
        initialFistVisibleIndex = initialFistVisibleIndex,
        lazyListState = lazyListState,
        visibleItemCount = visibleItemCount,
        spaceBetweenItems = spaceBetweenItems,
        selectorIndex = selectorIndex,
        contentPadding = contentPadding,
        activeColor = activeColor,
        inactiveColor = inactiveColor,
        inactiveItemScale = inactiveItemScale,
        orientation = orientation,
        key = key,
        contentType = contentType,
        itemContent = itemContent,
    )
}

/**
 * Orientation of the list. When [Horizontal] is selected [LazyRow], when [Vertical] is selected
 * [LazyColumn] is used for displaying items
 */
enum class Orientation {
    Horizontal, Vertical
}

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
@OptIn(ExperimentalSnapperApi::class)
@Composable
fun <T> AnimatedCircularList(
    modifier: Modifier = Modifier,
    items: List<T>,
    initialFistVisibleIndex: Int = Int.MAX_VALUE / 2,
    lazyListState: LazyListState = rememberLazyListState(initialFistVisibleIndex),
    visibleItemCount: Int = 5,
    spaceBetweenItems: Dp = 4.dp,
    selectorIndex: Int = visibleItemCount / 2,
    contentPadding: PaddingValues = PaddingValues(0.dp),
    activeColor: Color = Color.Cyan,
    inactiveColor: Color = Color.Gray,
    inactiveItemScale: Float = .85f,
    orientation: Orientation = Horizontal,
    key: ((index: Int) -> Any)? = null,
    contentType: (index: Int) -> Any? = { null },
    itemContent: @Composable LazyItemScope.(
        animationProgress: AnimationProgress, index: Int, size: Dp
    ) -> Unit,
) {

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

        val availableSpace = constraints.maxWidth.toFloat()
        val density = LocalDensity.current
        val spaceBetweenItemsPx = density.run { spaceBetweenItems.toPx() }

        val itemWidth = (availableSpace - spaceBetweenItemsPx * (visibleItemCount - 1)) / visibleItemCount
        val itemWidthDp = density.run { itemWidth.toDp() }

        val content: LazyListScope.() -> Unit = {
            items(
                count = Int.MAX_VALUE, key = key, contentType = contentType
            ) { globalIndex ->
                AnimatedItems(
                    lazyListState = lazyListState,
                    argbEvaluator = argbEvaluator,
                    initialFistVisibleIndex = initialFistVisibleIndex,
                    indexOfSelector = indexOfSelector,
                    globalIndex = globalIndex,
                    availableSpace = availableSpace,
                    itemWidth = itemWidthDp,
                    spaceBetweenItems = spaceBetweenItemsPx,
                    visibleItemCount = visibleItemCount,
                    totalItemCount = totalItemCount,
                    inactiveItemScale = inactiveItemScale,
                    inactiveColor = unSelectedColor,
                    activeColor = selectedColor
                ) { animationProgress: AnimationProgress, width: Dp ->
                    itemContent(animationProgress, globalIndex % totalItemCount, width)
                }
            }
        }

        if (orientation == Horizontal) {
            LazyRow(
                modifier = modifier,
                state = lazyListState,
                horizontalArrangement = Arrangement.spacedBy(spaceBetweenItems),
                flingBehavior = flingBehavior
            ) {
                content()
            }
        } else {
            LazyColumn(
                modifier = modifier,
                state = lazyListState,
                verticalArrangement = Arrangement.spacedBy(spaceBetweenItems),
                flingBehavior = flingBehavior
            ) {
                content()
            }
        }
    }
}

@Composable
private fun LazyItemScope.AnimatedItems(
    lazyListState: LazyListState,
    argbEvaluator: ArgbEvaluator,
    initialFistVisibleIndex: Int,
    indexOfSelector: Int,
    globalIndex: Int,
    availableSpace: Float,
    itemWidth: Dp,
    spaceBetweenItems: Float,
    visibleItemCount: Int,
    totalItemCount: Int,
    inactiveItemScale: Float,
    inactiveColor: Int,
    activeColor: Int,
    itemContent: @Composable LazyItemScope.(animationProgress: AnimationProgress, size: Dp) -> Unit

) {
    var selectedIndex by remember {
        mutableStateOf(-1)
    }

    val itemWidthPx = LocalDensity.current.run { itemWidth.toPx() }

    val animationData by remember {
        derivedStateOf {
            val animationData = getAnimationData(
                lazyListState = lazyListState,
                argbEvaluator = argbEvaluator,
                initialFistVisibleIndex = initialFistVisibleIndex,
                indexOfSelector = indexOfSelector,
                globalIndex = globalIndex,
                selectedIndex = selectedIndex,
                availableSpace = availableSpace,
                itemSize = itemWidthPx,
                spaceBetweenItems = spaceBetweenItems,
                visibleItemCount = visibleItemCount,
                totalItemCount = totalItemCount,
                inactiveScale = inactiveItemScale,
                inactiveColor = inactiveColor,
                activeColor = activeColor
            )

            selectedIndex = animationData.globalItemIndex
            animationData
        }
    }
    itemContent(animationData, itemWidth)
}

/**
 * get color, scale and selected index for scroll progress for infinite or circular list with
 * [Int.MAX_VALUE] global index count
 *
 * @param lazyListState A state object that can be hoisted to control and observe scrolling
 * @param argbEvaluator evaluator can be used to perform type interpolation between
 * integer values that represent ARGB colors
 * @param initialFistVisibleIndex index of item that is at the beginning of the list initially
 * @param indexOfSelector global index of element of selector of infinite items. Item with
 * this index is selected item
 * @param globalIndex index of current item. This index changes for every item in list
 * that calls this function
 * @param selectedIndex global index of currently selected item. This index changes only
 * when selected item is changed due to scroll
 * @param availableSpace space that is reserved for items and space between items. This
 * param is list width/height minus padding values in respective axis.
 * @param itemSize width/height of each item
 * @param spaceBetweenItems space between each item
 * @param visibleItemCount count of visible items on screen
 * @param totalItemCount count of items that are displayed in circular list
 * @param inactiveScale lower scale that items can be scaled to. It should be less than 1f
 * @param inactiveColor color of items when they are not selected
 * @param activeColor color of item that is selected
 */
private fun getAnimationData(
    lazyListState: LazyListState,
    argbEvaluator: ArgbEvaluator,
    initialFistVisibleIndex: Int,
    indexOfSelector: Int,
    globalIndex: Int,
    selectedIndex: Int,
    availableSpace: Float,
    itemSize: Float,
    spaceBetweenItems: Float,
    visibleItemCount: Int,
    totalItemCount: Int,
    inactiveScale: Float,
    inactiveColor: Int,
    activeColor: Int,
): AnimationProgress {

    val visibleItems = lazyListState.layoutInfo.visibleItemsInfo
    val currentItem: LazyListItemInfo? = visibleItems.firstOrNull { it.index == globalIndex }

    // Position of left of selector item
    // Selector is item that is considered as selected
    val selectorPosition = indexOfSelector * (itemSize + spaceBetweenItems)

    // Convert global indexes to indexes in range of 0..visibleItemCount
    val localIndex = (globalIndex + visibleItemCount / 2) % visibleItemCount

    // Get offset of each item relative to start of list x=0 position
    val itemOffset =
        currentItem?.offset
            ?: (localIndex * itemSize + localIndex * spaceBetweenItems).toInt()

    // Current item is not close to center item or one half of left or right
    // item
    val scale = getScale(
        selectedIndex = selectedIndex,
        globalIndex = globalIndex,
        initialFistVisibleIndex = initialFistVisibleIndex,
        indexOfSelector = indexOfSelector,
        inactiveScale = inactiveScale,
        itemSize = itemSize,
        spaceBetweenItems = spaceBetweenItems,
        selectorPosition = selectorPosition,
        itemOffset = itemOffset
    )

    // This is the fraction between lower bound and 1f. If lower bound is .9f we have
    // range of .9f..1f for scale calculation
    val scalingInterval = 1f - inactiveScale

    // Scale for color when scale is at lower bound color scale is zero
    // when scale reaches upper bound(1f) color scale is 1f which is target color
    // when argEvaluator evaluates color
    val colorScale = (scale - inactiveScale) / scalingInterval

    var distance = Int.MAX_VALUE

    var globalSelectedIndex = selectedIndex

    visibleItems.forEach {
        val itemDistanceToSelector = (it.offset - selectorPosition - itemSize / 2).absoluteValue
        if (itemDistanceToSelector < distance) {
            distance = itemDistanceToSelector.toInt()
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


    val color: Int = argbEvaluator.evaluate(
        colorScale, inactiveColor, activeColor
    ) as Int

    return AnimationProgress(
        scale = scale,
        color = Color(color),
        itemOffset = itemOffset,
        itemFraction = itemOffset / availableSpace,
        globalItemIndex = globalSelectedIndex,
        itemIndex = itemIndex
    )
}

/**
 * get scale based on whether it's initial run of list,
 * [LazyListState]'S [LazyListLayoutInfo.visibleItemsInfo]  list is empty,
 * or current scroll state of list.
 *
 * @param selectedIndex global index of currently selected item. This index changes only
 * when selected item is changed due to scroll
 * @param globalIndex index of current item. This index changes for every item in list
 * that calls this function
 * @param initialFistVisibleIndex index of item that is at the beginning of the list initially
 * @param indexOfSelector global index of element of selector of infinite items. Item with
 * this index is selected item
 * @param inactiveScale lower scale that items can be scaled to. It should be less than 1f
 * @param itemSize width/height of each item
 * @param spaceBetweenItems space between each item
 * @param selectorPosition position of selector or selected item
 * @param itemOffset offset of item from start of the list's x or y zero position
 */
private fun getScale(
    selectedIndex: Int,
    globalIndex: Int,
    initialFistVisibleIndex: Int,
    indexOfSelector: Int,
    inactiveScale: Float,
    itemSize: Float,
    spaceBetweenItems: Float,
    selectorPosition: Float,
    itemOffset: Int
): Float {
    // Current item is not close to center item or one half of left or right / item
    // visible items are not initialized and it's selector index
    return if (selectedIndex == -1 && globalIndex == initialFistVisibleIndex + indexOfSelector) {
        1f
        // visible items are not initialized and any item other than selector
    } else if (selectedIndex == -1) {
        inactiveScale
    } else {

        // Check how far this item is to selector index.
        val distanceToSelector = (selectorPosition - itemOffset).absoluteValue
        val scaleRegionWidth = (itemSize + spaceBetweenItems)

        calculateScale(
            distanceToSelector,
            scaleRegionWidth,
            inactiveScale
        )
    }
}

/**
 * Calculate scale that is inside scale region based on [minimum]
 */
private fun calculateScale(
    distanceToSelector: Float,
    scaleRegionWidth: Float,
    minimum: Float
): Float {
    return if (distanceToSelector < scaleRegionWidth) {
        // Now item is in scale region. Check where exactly it is in this region for animation
        val fraction = (scaleRegionWidth - distanceToSelector) / scaleRegionWidth
        // scale based on lower bound and 1f.
        // If lower bound .9f and fraction is 50% our scale is .9f + .1f*50/100 = .95f
        minimum + fraction * (1 - minimum)
    } else {
        minimum

    }.coerceIn(minimum, 1f)
}

