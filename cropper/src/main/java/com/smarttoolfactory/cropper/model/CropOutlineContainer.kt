package com.smarttoolfactory.cropper.model

import androidx.compose.runtime.Immutable

/**
 * Interface for containing multiple [CropOutline]s, currently selected item and index
 * for displaying on settings UI
 */
interface CropOutlineContainer<O : CropOutline> {
    var selectedIndex: Int
    val outlines: List<O>
    val selectedItem: O
        get() = outlines[selectedIndex]
}

/**
 * Container for [RectCropShape]
 */
@Immutable
class RectOutlineContainer(
    override var selectedIndex: Int = 0,
    override val outlines: List<RectCropShape>
) : CropOutlineContainer<RectCropShape>

/**
 * Container for [RoundedCornerCropShape]s
 */
@Immutable
class RoundedRectOutlineContainer(
    override var selectedIndex: Int = 0,
    override val outlines: List<RoundedCornerCropShape>
) : CropOutlineContainer<RoundedCornerCropShape>

/**
 * Container for [CutCornerCropShape]s
 */
@Immutable
class CutCornerRectOutlineContainer(
    override var selectedIndex: Int = 0,
    override val outlines: List<CutCornerCropShape>
) : CropOutlineContainer<CutCornerCropShape>

/**
 * Container for [OvalCropShape]s
 */
@Immutable
class OvalOutlineContainer(
    override var selectedIndex: Int = 0,
    override val outlines: List<OvalCropShape>
) : CropOutlineContainer<OvalCropShape>

/**
 * Container for [PolygonCropShape]s
 */
@Immutable
class PolygonOutlineContainer(
    override var selectedIndex: Int = 0,
    override val outlines: List<PolygonCropShape>
) : CropOutlineContainer<PolygonCropShape>

/**
 * Container for [CustomPathOutline]s
 */
@Immutable
class CustomOutlineContainer(
    override var selectedIndex: Int = 0,
    override val outlines: List<CustomPathOutline>
) : CropOutlineContainer<CustomPathOutline>

/**
 * Container for [ImageMaskOutline]s
 */
@Immutable
class ImageMaskOutlineContainer(
    override var selectedIndex: Int = 0,
    override val outlines: List<ImageMaskOutline>
) : CropOutlineContainer<ImageMaskOutline>
