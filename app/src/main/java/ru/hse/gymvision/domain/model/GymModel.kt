package ru.hse.gymvision.domain.model

import android.graphics.Bitmap

data class GymModel(
    val id: Int = 0,
    val name: String = "",
    val address: String = "",
    val image: Bitmap? = null,
)
