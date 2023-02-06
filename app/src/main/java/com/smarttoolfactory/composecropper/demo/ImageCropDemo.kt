@file:OptIn(ExperimentalMaterialApi::class, ExperimentalMaterialApi::class)

package com.smarttoolfactory.composecropper.demo

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.imageResource
import androidx.compose.ui.unit.dp
import com.smarttoolfactory.colorpicker.widget.drawChecker
import com.smarttoolfactory.composecropper.ImageSelectionButton
import com.smarttoolfactory.composecropper.R
import com.smarttoolfactory.composecropper.preferences.CropStyleSelectionMenu
import com.smarttoolfactory.composecropper.preferences.PropertySelectionSheet
import com.smarttoolfactory.composecropper.ui.theme.ComposeCropperTheme
import com.smarttoolfactory.cropper.ImageCropper
import com.smarttoolfactory.cropper.model.OutlineType
import com.smarttoolfactory.cropper.model.RectCropShape
import com.smarttoolfactory.cropper.settings.*
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

    val defaultImage1 = ImageBitmap.imageResource(id = R.drawable.squircle)
    val defaultImage2 = ImageBitmap.imageResource(id = R.drawable.cloud)
    val defaultImage3 = ImageBitmap.imageResource(id = R.drawable.sun)

    val cropFrameFactory = remember {
        CropFrameFactory(
            listOf(
                defaultImage1,
                defaultImage2,
                defaultImage3
            )
        )
    }

    val handleSize: Float = LocalDensity.current.run { 20.dp.toPx() }

    var cropProperties by remember {
        mutableStateOf(
            CropDefaults.properties(
                cropOutlineProperty = CropOutlineProperty(
                    OutlineType.Rect,
                    RectCropShape(0, "Rect")
                ),
                handleSize = handleSize
            )
        )
    }
    var cropStyle by remember { mutableStateOf(CropDefaults.style()) }
    val coroutineScope = rememberCoroutineScope()

    var selectionPage by remember { mutableStateOf(SelectionPage.Properties) }


    val theme by remember {
        derivedStateOf {
            cropStyle.cropTheme
        }
    }

    ComposeCropperTheme(
        darkTheme = when(theme){
            CropTheme.Dark ->true
            CropTheme.Light->false
            else -> isSystemInDarkTheme()
        }
    ) {
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
                        cropFrameFactory = cropFrameFactory,
                        cropProperties = cropProperties,
                        onCropPropertiesChange = {
                            cropProperties = it
                        }
                    )
                } else {
                    CropStyleSelectionMenu(
                        cropType = cropProperties.cropType,
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
}

@Composable
private fun MainContent(
    cropProperties: CropProperties,
    cropStyle: CropStyle,
    onSelectionPageMenuClicked: (SelectionPage) -> Unit
) {

    val imageBitmapLarge = ImageBitmap.imageResource(
        LocalContext.current.resources,
        R.drawable.landscape5
    )

    var imageBitmap by remember { mutableStateOf(imageBitmapLarge) }
    var croppedImage by remember { mutableStateOf<ImageBitmap?>(null) }

    var crop by remember { mutableStateOf(false) }
    var showDialog by remember { mutableStateOf(false) }
    var isCropping by remember { mutableStateOf(false) }

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
                onCropStart = {
                    isCropping = true
                }
            ) {
                croppedImage = it
                isCropping = false
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

        if (isCropping) {
            CircularProgressIndicator()
        }
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
                modifier = Modifier
                    .drawChecker(RoundedCornerShape(8.dp))
                    .fillMaxWidth()
                    .aspectRatio(1f),
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