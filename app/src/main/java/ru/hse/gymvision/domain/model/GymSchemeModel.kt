package ru.hse.gymvision.domain.model

import android.graphics.Bitmap

data class ClickableCoord(
    val x: Int,
    val y: Int,
    val width: Int,
    val height: Int,
    val id: Int
)

data class GymSchemeModel(
    val image: Bitmap,
    val clickableCoords: List<ClickableCoord>
    )
