package com.smarttoolfactory.cropper.settings

import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.Path
import com.smarttoolfactory.cropper.model.*

class CropFrameFactory(private val defaultImage: ImageBitmap) {

    private val cropFrameMap = linkedMapOf<OutlineType, CropFrame>()
    private var items = listOf<CropFrame>()

    fun getCropFrames(): List<CropFrame> {
        if (items.isEmpty()) {
            val temp = mutableListOf<CropFrame>()
            OutlineType.values().forEach {
                temp.add(getCropFrame(it))
            }
            items = temp
        }
        return items.toList()
    }

    fun getCropFrame(outlineType: OutlineType): CropFrame {
        return cropFrameMap[outlineType] ?: createDefaultFrame(outlineType)
    }

    private fun createDefaultFrame(outlineType: OutlineType): CropFrame {
        return when (outlineType) {
            OutlineType.Rect -> {
                CropFrame(
                    outlineType = outlineType,
                    editable = false,
                    cropOutlineContainer = createCropOutlineContainer(outlineType)
                )
            }

            OutlineType.RoundedRect -> {
                CropFrame(
                    outlineType = outlineType,
                    editable = true,
                    cropOutlineContainer = createCropOutlineContainer(outlineType)
                )
            }

            OutlineType.CutCorner -> {
                CropFrame(
                    outlineType = outlineType,
                    editable = true,
                    cropOutlineContainer = createCropOutlineContainer(outlineType)
                )
            }

            OutlineType.Oval -> {
                CropFrame(
                    outlineType = outlineType,
                    editable = true,
                    cropOutlineContainer = createCropOutlineContainer(outlineType)
                )
            }

            OutlineType.Polygon -> {
                CropFrame(
                    outlineType = outlineType,
                    editable = true,
                    cropOutlineContainer = createCropOutlineContainer(outlineType)
                )
            }


            OutlineType.Custom -> {
                CropFrame(
                    outlineType = outlineType,
                    editable = true,
                    cropOutlineContainer = createCropOutlineContainer(outlineType)
                )
            }

            OutlineType.ImageMask -> {
                CropFrame(
                    outlineType = outlineType,
                    editable = true,
                    cropOutlineContainer = createCropOutlineContainer(outlineType)
                )
            }
        }
    }

    private fun createCropOutlineContainer(
        outlineType: OutlineType
    ): CropOutlineContainer<out CropOutline> {
        return when (outlineType) {
            OutlineType.Rect -> {
                RectOutlineContainer(
                    outlines = listOf(RectCropShape(id = 0, title = "Rect"))
                )
            }

            OutlineType.RoundedRect -> {
                RoundedRectOutlineContainer(
                    outlines = listOf(RoundedCornerCropShape(id = 0, title = "Rounded"))
                )
            }

            OutlineType.CutCorner -> {
                CutCornerRectOutlineContainer(
                    outlines = listOf(CutCornerCropShape(id = 0, title = "CutCorner"))
                )
            }

            OutlineType.Oval -> {
                OvalOutlineContainer(
                    outlines = listOf(OvalCropShape(id = 0, title = "Oval"))
                )
            }

            OutlineType.Polygon -> {
                PolygonOutlineContainer(
                    outlines = listOf(PolygonCropShape(id = 0, title = "Polygon"))
                )
            }

            OutlineType.Custom -> {
                CustomOutlineContainer(
                    outlines = listOf(
                        CustomPathOutline(id = 0, title = "Custom", path = createDefaultPath())
                    )
                )
            }

            OutlineType.ImageMask -> {
                ImageMaskOutlineContainer(
                    outlines = listOf(
                        ImageMaskOutline(id = 0, title = "ImageMask", image = defaultImage)
                    )
                )
            }
        }
    }

    fun editCropFrame(cropFrame: CropFrame) {
        cropFrameMap[cropFrame.outlineType] = cropFrame
    }

    private fun createDefaultPath(): Path {
        return PathFavorite
    }
}


val PathFavorite
    get() = Path().apply {
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