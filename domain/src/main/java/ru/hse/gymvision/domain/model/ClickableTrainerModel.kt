package ru.hse.gymvision.domain.model

data class ClickableTrainerModel(
    val id: Int = 0,
    val name: String = "",
    val description: String = "",
    val xPercent: Float = 0f,
    val yPercent: Float = 0f,
    val widthPercent: Float = 0f,
    val heightPercent: Float = 0f
)