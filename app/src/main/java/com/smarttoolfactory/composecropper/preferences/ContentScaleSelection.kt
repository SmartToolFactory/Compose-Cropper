package com.smarttoolfactory.composecropper.preferences

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

val contentScaleOptions =
    listOf("None", "Fit", "Crop", "FillBounds", "FillWidth", "FillHeight", "Inside")

@Composable
internal fun ContentScaleDialogSelection(
    contentScale: ContentScale,
    onContentScaleChanged: (ContentScale) -> Unit
){

    var showDialog by remember { mutableStateOf(false) }

    val index = when (contentScale) {
        ContentScale.None -> 0
        ContentScale.Fit -> 1
        ContentScale.Crop -> 2
        ContentScale.FillBounds -> 3
        ContentScale.FillWidth -> 4
        ContentScale.FillHeight -> 5
        else -> 6
    }

    Text(
        text = contentScaleOptions[index],
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
            title = "Content Scale",
            options = contentScaleOptions,
            value = index,
            onDismiss = { showDialog = false },
            onConfirm = {

                val scale = when (it) {
                    0 -> ContentScale.None
                    1 -> ContentScale.Fit
                    2 -> ContentScale.Crop
                    3 -> ContentScale.FillBounds
                    4 -> ContentScale.FillWidth
                    5 -> ContentScale.FillHeight
                    else -> ContentScale.Inside
                }
                onContentScaleChanged(scale)
                showDialog = false
            }
        )
    }
}

@Composable
internal fun ContentScaleExposedSelection(
    contentScale: ContentScale,
    onContentScaleChanged: (ContentScale) -> Unit
) {
    var index = when (contentScale) {
        ContentScale.None -> 0
        ContentScale.Fit -> 1
        ContentScale.Crop -> 2
        ContentScale.FillBounds -> 3
        ContentScale.FillWidth -> 4
        ContentScale.FillHeight -> 5
        else -> 6
    }

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .padding(2.dp)
    ) {
        ExposedSelectionMenu(
            modifier = Modifier.fillMaxWidth(),
            index = index,
            title = "ContentScale",
            options = contentScaleOptions,
            onSelected = {
                index = it
                val scale = when (index) {
                    0 -> ContentScale.None
                    1 -> ContentScale.Fit
                    2 -> ContentScale.Crop
                    3 -> ContentScale.FillBounds
                    4 -> ContentScale.FillWidth
                    5 -> ContentScale.FillHeight
                    else -> ContentScale.Inside
                }

                onContentScaleChanged(scale)
            }
        )
    }
}
