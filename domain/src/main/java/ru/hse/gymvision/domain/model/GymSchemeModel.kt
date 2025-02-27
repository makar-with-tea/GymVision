package ru.hse.gymvision.domain.model

data class GymSchemeModel(
    var image: ByteArray,
    val clickableTrainers: List<ClickableTrainer>,
    val clickableCameras: List<ClickableCamera>
    )
