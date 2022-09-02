package com.smarttoolfactory.cropper.model

import androidx.compose.runtime.Immutable

/**
 * Value class for containing aspect ratio
 * and [AspectRatio.Unspecified] for comparing
 */
@Immutable
data class AspectRatio(val value: Float) {
    companion object {
        val Unspecified = AspectRatio(-1f)
    }
}