package ru.hse.gymvision.domain.exampledata

import ru.hse.gymvision.domain.model.ClickableCameraModel
import ru.hse.gymvision.domain.model.ClickableTrainerModel
import ru.hse.gymvision.domain.model.GymSchemeModel

val gymSchemeExample = GymSchemeModel(
    scheme = ByteArray(0),
    clickableTrainerModels = listOf(
        ClickableTrainerModel(0, "Беговая дорожка", "Тут можно бегать", 0.135f, 0.16f, 0.2f, 0.35f),
        ClickableTrainerModel(1, "Эллиптический тренажер", "Тут можно делать... Что-то...", 0.53f, 0.16f, 0.3f, 0.2f),
        ClickableTrainerModel(2, "Силовой комплекс","Тут можно становиться сильным :>", 0.48f, 0.5f, 0.35f, 0.35f),
    ),
    clickableCameraModels = listOf(
        ClickableCameraModel(0, 0.4f, 0.25f),
        ClickableCameraModel(1, 0.63f, 0.44f),
        ClickableCameraModel(2, 0.3f, 0.8f)
    ),
    id = 0,
    name = "Имя зала"
)
