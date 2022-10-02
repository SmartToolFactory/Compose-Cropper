package com.smarttoolfactory.composecropper.demo

import android.graphics.Bitmap
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.imageResource
import androidx.compose.ui.unit.dp
import com.smarttoolfactory.composecropper.R

@Composable
fun CanvasDemo() {

    Column(modifier = Modifier.fillMaxSize()) {

        NativeCanvasSample1(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(4 / 3f)
        )

        NativeCanvasMaskSample()

        VectorSample()
    }

}


@Composable
fun NativeCanvasMaskSample() {


    val cropMaskBitmap = ImageBitmap.imageResource(id = R.drawable.squircle)

    val imagePaint = remember {
        Paint().apply {
            blendMode = BlendMode.SrcIn
        }
    }

    val paint = remember {
        Paint()
    }

    val imageBitmap = ImageBitmap
        .imageResource(id = R.drawable.cinnamon)
        .asAndroidBitmap()
        .copy(Bitmap.Config.ARGB_8888, true)!!
        .asImageBitmap()


    Canvas(image = imageBitmap).apply {

        saveLayer(nativeCanvas.clipBounds.toComposeRect(), imagePaint)
        // Destination
//        drawCircle(center = Offset(400f, 400f), radius = 300f, paint)
//        drawImage(cropMaskBitmap, topLeftOffset = Offset.Zero, paint)

        val matrix = android.graphics.Matrix()
        matrix.postScale(30f, 30f)
        favoritePath.asAndroidPath().transform(matrix)

        val left = favoritePath.getBounds().left
        val top = favoritePath.getBounds().top


        drawPath(favoritePath, paint)

        // Source
        drawImage(imageBitmap, topLeftOffset = Offset.Zero, imagePaint)

        restore()
    }

    Image(
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(4 / 3f)
            .border(1.dp, Color.Green),
        bitmap = imageBitmap,
        contentDescription = null
    )
}

@Composable
fun NativeCanvasSample1(modifier: Modifier) {

    val imageBitmap = ImageBitmap
        .imageResource(id = R.drawable.cinnamon)
        .asAndroidBitmap()
        .copy(Bitmap.Config.ARGB_8888, true)!!
        .asImageBitmap()

    BoxWithConstraints(modifier) {


        val imageWidth = constraints.maxWidth
        val imageHeight = constraints.maxHeight

        val bitmapWidth = imageBitmap.width
        val bitmapHeight = imageBitmap.height


        val canvas = Canvas(imageBitmap)

        val imagePaint = remember {
            Paint().apply {
                blendMode = BlendMode.SrcIn
            }
        }

        val paint = remember {
            Paint().apply {
                color = Color(0xff29B6F6)
            }
        }

        canvas.apply {
            val nativeCanvas = this.nativeCanvas
            val canvasWidth = nativeCanvas.width.toFloat()
            val canvasHeight = nativeCanvas.height.toFloat()

            println(
                "🔥 Canvas Width: $canvasWidth, canvasHeight: $canvasHeight, " +
                        "imageWidth: $imageWidth, imageHeight: $imageHeight\n" +
                        "bitmapWidth: $bitmapWidth, bitmapHeight: $bitmapHeight\n" +
                        "rect: ${nativeCanvas.clipBounds.toComposeRect()}"
            )
            saveLayer(nativeCanvas.clipBounds.toComposeRect(), imagePaint)

            drawCircle(
                center = Offset(canvasWidth / 2, canvasHeight / 2),
                radius = canvasHeight / 2,
                paint = paint
            )
            drawImage(image = imageBitmap, topLeftOffset = Offset.Zero, imagePaint)
            restore()


        }

        Image(
            modifier = Modifier
                .background(Color.LightGray)
                .border(2.dp, Color.Red),
            bitmap = imageBitmap,
            contentDescription = null,
            contentScale = ContentScale.FillBounds
        )

    }
}

@Composable
private fun VectorSample() {

    androidx.compose.foundation.Canvas(
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(4 / 3f)
            .border(3.dp,Color.Cyan)
    ) {

        val matrix = android.graphics.Matrix()
        matrix.postScale(30f, 30f)
        starPath.asAndroidPath().transform(matrix)

        val left = starPath.getBounds().left
        val top = starPath.getBounds().top

        translate(left = -left, top = -top) {
            drawPath(starPath, Color.Red)
        }

        drawRect(
            color = Color.Green,
            topLeft = starPath.getBounds().topLeft,
            size = starPath.getBounds().size,
            style = Stroke(2.dp.toPx())
        )

    }
}




val favoritePath = Path().apply {
    moveTo(12.0f, 21.35f)
    relativeLineTo(-1.45f, -1.32f)
    cubicTo(5.4f, 15.36f, 2.0f, 12.28f, 2.0f, 8.5f)
    cubicTo(2.0f, 5.42f, 4.42f, 3.0f, 7.5f, 3.0f)
    relativeCubicTo(1.74f, 0.0f, 3.41f, 0.81f, 4.5f, 2.09f)
    cubicTo(13.09f, 3.81f, 14.76f, 3.0f, 16.5f, 3.0f)
    cubicTo(19.58f, 3.0f, 22.0f, 5.42f, 22.0f, 8.5f)
    relativeCubicTo(0.0f, 3.78f, -3.4f, 6.86f, -8.55f, 11.54f)
    lineTo(12.0f, 21.35f)
    close()
}

val starPath = Path().apply {
    moveTo(12.0f, 17.27f)
    lineTo(18.18f, 21.0f)
    relativeLineTo(-1.64f, -7.03f)
    lineTo(22.0f, 9.24f)
    relativeLineTo(-7.19f, -0.61f)
    lineTo(12.0f, 2.0f)
    lineTo(9.19f, 8.63f)
    lineTo(2.0f, 9.24f)
    relativeLineTo(5.46f, 4.73f)
    lineTo(5.82f, 21.0f)
    close()
}