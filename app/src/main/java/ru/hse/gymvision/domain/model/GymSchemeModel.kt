package ru.hse.gymvision.domain.model

import android.graphics.Bitmap

data class ClickableCoord(
    val xPercent: Float = 0f,
    val yPercent: Float = 0f,
    val widthPercent: Float = 0f,
    val heightPercent: Float = 0f,
    val id: Int = 0,
    val description: String = ""
)

data class GymSchemeModel(
    var image: Bitmap,
    val clickableCoords: List<ClickableCoord>
    )
