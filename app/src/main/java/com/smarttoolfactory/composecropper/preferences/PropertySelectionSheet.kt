package com.smarttoolfactory.composecropper.preferences

import androidx.compose.runtime.Composable
import com.smarttoolfactory.cropper.settings.CropFrameFactory
import com.smarttoolfactory.cropper.settings.CropProperties

@Composable
internal fun PropertySelectionSheet(
    cropFrameFactory:CropFrameFactory,
    cropProperties: CropProperties,
    onCropPropertiesChange: (CropProperties) -> Unit
) {
    BaseSheet {
        CropPropertySelectionMenu(
            cropFrameFactory =cropFrameFactory,
            cropProperties = cropProperties,
            onCropPropertiesChange = onCropPropertiesChange
        )

        CropGestureSelectionMenu(
            cropProperties = cropProperties,
            onCropPropertiesChange = onCropPropertiesChange
        )
    }
}
