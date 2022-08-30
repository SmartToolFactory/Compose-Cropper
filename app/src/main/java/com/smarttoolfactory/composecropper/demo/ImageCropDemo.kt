@file:OptIn(ExperimentalMaterialApi::class, ExperimentalMaterialApi::class)

package com.smarttoolfactory.composecropper.demo

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Crop
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.IconButton
import com.smarttoolfactory.composecropper.ContentScaleSelectionMenu
import com.smarttoolfactory.composecropper.ImageSelectionButton
import com.smarttoolfactory.composecropper.R
import com.smarttoolfactory.cropper.ImageCropper
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun ImageCropDemo() {


    val bottomSheetScaffoldState: BottomSheetScaffoldState = rememberBottomSheetScaffoldState(
        bottomSheetState = BottomSheetState(BottomSheetValue.Collapsed)
    )

    BottomSheetScaffold(
        scaffoldState = bottomSheetScaffoldState,
        sheetElevation = 8.dp,
        sheetShape = RoundedCornerShape(
            bottomStart = 0.dp,
            bottomEnd = 0.dp,
            topStart = 16.dp,
            topEnd = 16.dp
        ),

        sheetGesturesEnabled = true,
        sheetContent = {
            SheetContent()
        },
        drawerElevation = 16.dp,
        drawerGesturesEnabled = true,
        // This is the height in collapsed state
        sheetPeekHeight = 0.dp
    ) {
        MainContent(bottomSheetScaffoldState)
    }

}

@Composable
private fun SheetContent() {

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.primaryContainer)
            .height(400.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = "Under Construction")
    }
}

@Composable
private fun MainContent(bottomSheetScaffoldState: BottomSheetScaffoldState) {

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

    var contentScale by remember { mutableStateOf(ContentScale.Fit) }

    val coroutineScope = rememberCoroutineScope()

    var crop by remember { mutableStateOf(false) }

    var showDialog by remember { mutableStateOf(false) }


    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
        ) {

            ContentScaleSelectionMenu(contentScale) {
                contentScale = it
            }

            ImageCropper(
                modifier = modifier,
                imageBitmap = imageBitmap,
                contentScale = contentScale,
                contentDescription = "Image Cropper",
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
                        coroutineScope.launch {
                            if (bottomSheetScaffoldState.bottomSheetState.isExpanded) {
                                bottomSheetScaffoldState.bottomSheetState.collapse()
                            } else {
                                bottomSheetScaffoldState.bottomSheetState.expand()
                            }
                        }
                    }
                ) {
                    Icon(
                        Icons.Filled.Edit,
                        contentDescription = "Localized description",
                    )
                }

                IconButton(
                    onClick = { crop = true }) {
                    Icon(Icons.Filled.Crop, contentDescription = "Localized description")
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