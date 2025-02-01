package ru.hse.gymvision.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import ru.hse.gymvision.presentation.ui.screens.AuthorizationScreen
import ru.hse.gymvision.presentation.ui.screens.CameraScreen
import ru.hse.gymvision.presentation.ui.screens.GymChoiceScreen
import ru.hse.gymvision.presentation.ui.screens.GymSchemeScreen
import ru.hse.gymvision.presentation.ui.screens.ProfileScreen
import ru.hse.gymvision.presentation.ui.screens.RegistrationScreen

val LocalNavController = compositionLocalOf<NavHostController> { error("No NavController found!") }

@Composable
fun NavGraph(
    navController: NavHostController,
    modifier: Modifier = Modifier
) {
    CompositionLocalProvider(LocalNavController provides navController) {
        NavHost(
            navController = navController,
            startDestination = "authorization",
            modifier = modifier
        ) {
            composable("authorization") {
                AuthorizationScreen()
            }
            composable("registration") {
                RegistrationScreen()
            }
            composable("gymList") {
                GymChoiceScreen()
            }
            composable("gymScheme") {
                GymSchemeScreen()
            }
            composable("profile") {
                ProfileScreen()
            }
            composable("camera") {
                CameraScreen()
            }
        }
    }
}