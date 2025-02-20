package ru.hse.gymvision.domain.model

import android.graphics.Bitmap

data class GymSchemeModel(
    var image: Bitmap,
    val clickableTrainers: List<ClickableTrainer>,
    val clickableCameras: List<ClickableCamera>
    )
