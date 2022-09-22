package com.smarttoolfactory.cropper.model

import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.CutCornerShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.graphics.RectangleShape
import com.smarttoolfactory.cropper.util.createPolygonShape

val shapes = listOf(
    CropShape(
        title = "Rect",
        shape = RectangleShape,
        shapeType = ShapeType.Rect
    ),
    CropShape(
        title = "Rounded",
        shape = RoundedCornerShape(10),
        editable = true,
        shapeType = ShapeType.RoundedRect
    ),
    CropShape(
        title = "CutCorner",
        shape = CutCornerShape(10),
        editable = true,
        shapeType = ShapeType.CutCorner),
    CropShape(
        title = "Oval",
        shape = CircleShape,
        editable = true,
        shapeType = ShapeType.Oval
    ),
    CropShape(
        title = "Polygon",
        shape = createPolygonShape(6),
        editable = true,
        shapeType = ShapeType.Polygon
    )
)
