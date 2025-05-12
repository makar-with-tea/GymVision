package ru.hse.gymvision.domain.exampledata

import ru.hse.gymvision.domain.model.ClickableCamera
import ru.hse.gymvision.domain.model.ClickableTrainer
import ru.hse.gymvision.domain.model.GymSchemeModel

val gymSchemeExample = GymSchemeModel(
    image = ByteArray(0),
    clickableTrainers = listOf(
        ClickableTrainer(0, "Беговая дорожка", "Тут можно бегать", 0.135f, 0.16f, 0.2f, 0.35f),
        ClickableTrainer(1, "Эллиптический тренажер", "Тут можно делать... Что-то...", 0.53f, 0.16f, 0.3f, 0.2f),
        ClickableTrainer(2, "Силовой комплекс","Тут можно становиться сильным :>", 0.48f, 0.5f, 0.35f, 0.35f),
    ),
    clickableCameras = listOf(
        ClickableCamera(0, 0.4f, 0.25f),
        ClickableCamera(1, 0.63f, 0.44f),
        ClickableCamera(2, 0.3f, 0.8f)
    ),
    id = 0,
    name = "Имя зала"
)
