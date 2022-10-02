package com.smarttoolfactory.composecropper.demo

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.imageResource
import com.smarttoolfactory.composecropper.R
import com.smarttoolfactory.cropper.ImageCropper
import com.smarttoolfactory.cropper.model.OutlineType
import com.smarttoolfactory.cropper.model.RectCropShape
import com.smarttoolfactory.cropper.settings.CropDefaults
import com.smarttoolfactory.cropper.settings.CropOutlineProperty

@Composable
fun ImageCropDemoSimple() {

    val cropProperties by remember {
        mutableStateOf(
            CropDefaults.properties(
                cropOutlineProperty = CropOutlineProperty(
                    OutlineType.Rect,
                    RectCropShape(0, "Rect")
                )
            )
        )
    }
    val cropStyle by remember { mutableStateOf(CropDefaults.style()) }

    val imageBitmapLarge = ImageBitmap.imageResource(
        LocalContext.current.resources,
        R.drawable.cinnamon
    )

    val imageBitmap by remember { mutableStateOf(imageBitmapLarge) }
    val crop by remember { mutableStateOf(false) }


    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.DarkGray),
        contentAlignment = Alignment.Center
    ) {
        Column(modifier = Modifier.fillMaxSize()) {

            ImageCropper(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                imageBitmap = imageBitmap,
                contentDescription = "Image Cropper",
                cropStyle = cropStyle,
                cropProperties = cropProperties,
                crop = crop,
                onCropStart = {}
            ) {
            }
        }
    }
}