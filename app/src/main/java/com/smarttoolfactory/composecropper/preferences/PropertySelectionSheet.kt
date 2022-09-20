package com.smarttoolfactory.composecropper.preferences

import androidx.compose.runtime.Composable
import com.smarttoolfactory.cropper.settings.CropProperties

@Composable
internal fun PropertySelectionSheet(
    cropProperties: CropProperties,
    onCropPropertiesChange: (CropProperties) -> Unit
) {
    BaseSheet {
        CropPropertySelectionMenu(
            cropProperties = cropProperties,
            onCropPropertiesChange = onCropPropertiesChange
        )

        CropGestureSelectionMenu(
            cropProperties = cropProperties,
            onCropPropertiesChange = onCropPropertiesChange
        )
    }
}
