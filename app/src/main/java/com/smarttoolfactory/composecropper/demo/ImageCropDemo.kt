@file:OptIn(ExperimentalMaterialApi::class, ExperimentalMaterialApi::class)

package com.smarttoolfactory.composecropper.demo

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Brush
import androidx.compose.material.icons.filled.Crop
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.imageResource
import androidx.compose.ui.unit.dp
import com.smarttoolfactory.composecropper.ImageSelectionButton
import com.smarttoolfactory.composecropper.R
import com.smarttoolfactory.composecropper.preferences.CropStyleSelectionMenu
import com.smarttoolfactory.composecropper.preferences.PropertySelectionSheet

import com.smarttoolfactory.cropper.ImageCropper
import com.smarttoolfactory.cropper.settings.CropDefaults
import com.smarttoolfactory.cropper.settings.CropProperties
import com.smarttoolfactory.cropper.settings.CropStyle
import kotlinx.coroutines.launch

internal enum class SelectionPage {
    Properties, Style
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun ImageCropDemo() {


    val bottomSheetScaffoldState: BottomSheetScaffoldState = rememberBottomSheetScaffoldState(
        bottomSheetState = BottomSheetState(BottomSheetValue.Collapsed)
    )

    var cropProperties by remember { mutableStateOf(CropDefaults.properties()) }
    var cropStyle by remember { mutableStateOf(CropDefaults.style()) }
    val coroutineScope = rememberCoroutineScope()

    var selectionPage by remember { mutableStateOf(SelectionPage.Properties) }

    BottomSheetScaffold(
        scaffoldState = bottomSheetScaffoldState,
        sheetElevation = 16.dp,
        sheetShape = RoundedCornerShape(
            bottomStart = 0.dp,
            bottomEnd = 0.dp,
            topStart = 28.dp,
            topEnd = 28.dp
        ),

        sheetGesturesEnabled = true,
        sheetContent = {

            if (selectionPage == SelectionPage.Properties) {
                PropertySelectionSheet(
                    cropProperties = cropProperties,
                    onCropPropertiesChange = {
                        cropProperties = it
                    }
                )
            } else {
                CropStyleSelectionMenu(
                    cropStyle = cropStyle,
                    onCropStyleChange = {
                        cropStyle = it
                    }
                )
            }
        },

        // This is the height in collapsed state
        sheetPeekHeight = 0.dp
    ) {
        MainContent(
            cropProperties,
            cropStyle,
        ) {
            selectionPage = it

            coroutineScope.launch {
                if (bottomSheetScaffoldState.bottomSheetState.isExpanded) {
                    bottomSheetScaffoldState.bottomSheetState.collapse()
                } else {
                    bottomSheetScaffoldState.bottomSheetState.expand()
                }
            }
        }
    }
}

@Composable
private fun MainContent(
    cropProperties: CropProperties,
    cropStyle: CropStyle,
    onSelectionPageMenuClicked: (SelectionPage) -> Unit
) {

    val imageBitmapLarge = ImageBitmap.imageResource(
        LocalContext.current.resources,
        R.drawable.landscape1
    )

    var imageBitmap by remember { mutableStateOf(imageBitmapLarge) }
    var croppedImage by remember { mutableStateOf<ImageBitmap?>(null) }

    val modifier = Modifier
        .background(Color.LightGray)
        .fillMaxWidth()
        .aspectRatio(3 / 4f)

    var crop by remember { mutableStateOf(false) }
    var showDialog by remember { mutableStateOf(false) }


    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
        ) {

            ImageCropper(
                modifier = modifier,
                imageBitmap = imageBitmap,
                contentDescription = "Image Cropper",
                cropStyle = cropStyle,
                cropProperties = cropProperties,
                crop = crop,
            ) {
                croppedImage = it
                crop = false
                showDialog = true
            }
        }

        BottomAppBar(
            modifier = Modifier.align(Alignment.BottomStart),
            actions = {


                IconButton(
                    onClick = {
                        onSelectionPageMenuClicked(SelectionPage.Properties)
                    }
                ) {
                    Icon(
                        Icons.Filled.Settings,
                        contentDescription = "Settings",
                    )

                }
                IconButton(
                    onClick = {
                        onSelectionPageMenuClicked(SelectionPage.Style)
                    }
                ) {
                    Icon(Icons.Filled.Brush, contentDescription = "Style")
                }

                IconButton(
                    onClick = { crop = true }) {
                    Icon(Icons.Filled.Crop, contentDescription = "Crop Image")
                }
            },
            floatingActionButton = {
                ImageSelectionButton(
                    elevation = FloatingActionButtonDefaults.elevation(defaultElevation = 0.dp),
                    onImageSelected = { bitmap: ImageBitmap ->
                        imageBitmap = bitmap
                    }
                )
            }
        )
    }

    if (showDialog) {
        croppedImage?.let {
            ShowCroppedImageDialog(imageBitmap = it) {
                showDialog = !showDialog
                croppedImage = null
            }
        }
    }
}

@Composable
private fun ShowCroppedImageDialog(imageBitmap: ImageBitmap, onDismissRequest: () -> Unit) {
    androidx.compose.material3.AlertDialog(
        onDismissRequest = onDismissRequest,
        text = {
            Image(
                modifier = Modifier.fillMaxWidth(),
                contentScale = ContentScale.Fit,
                bitmap = imageBitmap,
                contentDescription = "result"
            )
        },
        confirmButton = {
            TextButton(
                onClick = {
                    onDismissRequest()
                }
            ) {
                Text("Confirm")
            }
        },
        dismissButton = {
            TextButton(
                onClick = {
                    onDismissRequest()
                }
            ) {
                Text("Dismiss")
            }
        }
    )
}