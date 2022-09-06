package com.smarttoolfactory.cropper.model

import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.Color

/**
 * Class that contains animated list animation properties
 * @param scale scale of the current item
 * @param color of the current item
 * @param itemOffset offset in px of current item from start of list
 * @param itemFraction offset in percent of current item from start of list relative to
 * width or height minus padding values in this axis
 * @param globalItemIndex index of current item. If it's returned from infinite list
 * it's real value of this index
 * @param itemIndex index of current item in range of total item count. It can be in range
 * of 0..item count -1
 */
@Immutable
data class AnimationProgress(
    val scale: Float,
    val color: Color,
    val itemOffset: Int,
    val itemFraction: Float,
    val globalItemIndex: Int,
    val itemIndex: Int
)