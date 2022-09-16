package com.smarttoolfactory.composecropper.properties

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.smarttoolfactory.cropper.CropType

@Composable
internal fun CropTypeExposedSelection(
    cropType: CropType,
    onCropTypeChange: (CropType) -> Unit
) {
    val cropTypeOptions =
        remember { listOf(CropType.Dynamic.toString(), CropType.Static.toString()) }

    val index = when (cropType) {
        CropType.Dynamic -> 0
        else -> 1
    }

    ExposedSelectionMenu(
        modifier = Modifier.fillMaxWidth(),
        index = index,
        title = "Crop Type",
        options = cropTypeOptions,
        onSelected = {
            val newCropType = when (it) {
                0 -> CropType.Dynamic
                else -> CropType.Static
            }

            onCropTypeChange(newCropType)
        }
    )
}

@Composable
internal fun CropTypeDialogSelection(
    cropType: CropType,
    onCropTypeChange: (CropType) -> Unit
) {

    val cropTypeOptions =
        remember { listOf(CropType.Dynamic.toString(), CropType.Static.toString()) }

    var showDialog by remember { mutableStateOf(false) }

    val index = when (cropType) {
        CropType.Dynamic -> 0
        else -> 1
    }

    Text(
        text = cropTypeOptions[index],
        fontSize = 18.sp,
        modifier = Modifier
            .fillMaxWidth()
            .clickable {
                showDialog = true
            }
            .padding(8.dp)

    )

    if (showDialog) {
        DialogWithMultipleSelection(
            title = "Crop Type",
            options = cropTypeOptions,
            value = index,
            onDismiss = { showDialog = false },
            onConfirm = {

                val cropTypeChange = when (it) {
                    0 -> CropType.Dynamic
                    else -> CropType.Static
                }
                onCropTypeChange(cropTypeChange)
                showDialog = false
            }
        )
    }

}

