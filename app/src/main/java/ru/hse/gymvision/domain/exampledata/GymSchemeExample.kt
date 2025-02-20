package ru.hse.gymvision.domain.exampledata

import android.graphics.Bitmap
import ru.hse.gymvision.domain.model.ClickableCoord
import ru.hse.gymvision.domain.model.GymSchemeModel

val gymSchemeExample = GymSchemeModel(
    image = Bitmap.createBitmap(1000, 1000, Bitmap.Config.ARGB_8888),
    clickableCoords = listOf(
        ClickableCoord(0.135f, 0.16f, 0.2f, 0.35f, 0, "Беговая дорожка"),
        ClickableCoord(0.53f, 0.16f, 0.3f, 0.2f, 1, "Эллиптический тренажер"),
        ClickableCoord(0.48f, 0.5f, 0.35f, 0.35f, 2, "Силовой комплекс"),
    )
)