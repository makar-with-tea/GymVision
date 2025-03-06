package ru.hse.gymvision.navigation

import ru.alexgladkov.odyssey.compose.extensions.screen
import ru.alexgladkov.odyssey.compose.navigation.RootComposeBuilder
import ru.hse.gymvision.ui.account.AccountScreen
import ru.hse.gymvision.ui.authorization.AuthorizationScreen
import ru.hse.gymvision.ui.camera.CameraScreen
import ru.hse.gymvision.ui.gymlist.GymListScreen
import ru.hse.gymvision.ui.gymscheme.GymSchemeScreen
import ru.hse.gymvision.ui.registration.RegistrationScreen

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
