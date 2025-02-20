package ru.hse.gymvision.presentation.navigation

import ru.alexgladkov.odyssey.compose.extensions.screen
import ru.alexgladkov.odyssey.compose.navigation.RootComposeBuilder
import ru.hse.gymvision.presentation.ui.screens.AccountScreen
import ru.hse.gymvision.presentation.ui.screens.AuthorizationScreen
import ru.hse.gymvision.presentation.ui.screens.CameraScreen
import ru.hse.gymvision.presentation.ui.screens.GymListScreen
import ru.hse.gymvision.presentation.ui.screens.GymSchemeScreen
import ru.hse.gymvision.presentation.ui.screens.RegistrationScreen

fun RootComposeBuilder.navigationGraph() {
    screen(name = "authorization") {
        AuthorizationScreen()
    }
    screen(name = "registration") {
        RegistrationScreen()
    }
    screen(name = "gymList") {
        GymListScreen()
    }
    screen(name = "gymScheme") {
        GymSchemeScreen()
    }
    screen(name = "profile") {
        AccountScreen()
    }
    screen(name = "camera") {
        CameraScreen()
    }
}
