package com.smarttoolfactory.cropper.model

import androidx.annotation.DrawableRes
import androidx.compose.runtime.Immutable

/**
 * Model class for aspect ratio of overlay
 */
@Immutable
data class AspectRatioModel(
    val title: String,
    @DrawableRes val imgRes: Int,
    val aspectRatio: AspectRatio
)

/**
 * Value class for containing ascpect ratio
 * and [AspectRatio.Unspecified] for comparing
 */
@Immutable
data class AspectRatio(val value: Float) {
    companion object {
        val Unspecified = AspectRatio(-1f)
    }
}