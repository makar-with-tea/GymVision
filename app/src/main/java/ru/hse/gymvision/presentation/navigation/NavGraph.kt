package ru.hse.gymvision.presentation.navigation

import ru.alexgladkov.odyssey.compose.extensions.screen
import ru.alexgladkov.odyssey.compose.navigation.RootComposeBuilder
import ru.hse.gymvision.presentation.ui.account.AccountScreen
import ru.hse.gymvision.presentation.ui.authorization.AuthorizationScreen
import ru.hse.gymvision.presentation.ui.camera.CameraScreen
import ru.hse.gymvision.presentation.ui.gymlist.GymListScreen
import ru.hse.gymvision.presentation.ui.gymscheme.GymSchemeScreen
import ru.hse.gymvision.presentation.ui.registration.RegistrationScreen

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
