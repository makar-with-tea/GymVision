package ru.hse.gymvision.domain.model

data class GymInfoModel(
    val id: Int = 0,
    val name: String = "",
    val address: String = "",
    val image: ByteArray? = null,
)
