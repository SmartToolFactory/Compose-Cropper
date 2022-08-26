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
    val aspectRatio: Float
)