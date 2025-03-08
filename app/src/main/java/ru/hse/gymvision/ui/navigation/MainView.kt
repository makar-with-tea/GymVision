package ru.hse.gymvision.ui.navigation

import android.util.Log
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import ru.hse.gymvision.ui.account.AccountScreen
import ru.hse.gymvision.ui.authorization.AuthorizationScreen
import ru.hse.gymvision.ui.camera.CameraScreen
import ru.hse.gymvision.ui.gymlist.GymListScreen
import ru.hse.gymvision.ui.gymscheme.GymSchemeScreen
import ru.hse.gymvision.ui.registration.RegistrationScreen

sealed class Route(val route: String) {
    data object Authorization: Route("authorization")

    data object Registration: Route("registration")

    data object GymList: Route("gym_list")

    data object GymScheme: Route("gym_scheme")

    data object Account: Route("account")

    data object Camera: Route("camera")
}

@Composable
fun MainView() {
    Log.d("MainView", "SetUpNavHost")
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route
    Scaffold(
        bottomBar = {
            Log.d("SetUpNavHost", "Current route: $currentRoute")
            if (currentRoute != Route.Authorization.route && currentRoute != Route.Registration.route) {
                BottomNavigationBar(navController = navController)
            }
        }
    ) { paddingValues ->
        Box(modifier = Modifier.padding(paddingValues)) {
            NavHost(navController = navController, startDestination = Route.Authorization.route) {
                composable(Route.Authorization.route) {
                    AuthorizationScreen(
                        navigateToRegistration = {
                            navController.navigate(Route.Registration.route) {
                                launchSingleTop = true
                                restoreState = true
                            }
                        },
                        navigateToGymList = {
                            navController.navigate(Route.GymList.route) {
                                launchSingleTop = true
                                restoreState = true
                            }
                        },
                    )
                }
                composable(Route.Registration.route) {
                    RegistrationScreen(
                        navigateToGymList = {
                            navController.navigate(Route.GymList.route) {
                                launchSingleTop = true
                                restoreState = true
                            }
                        },
                        navigateToAuthorization = {
                            navController.navigate(Route.Authorization.route) {
                                launchSingleTop = true
                                restoreState = true
                            }
                        }
                    )
                }
                composable(Route.GymList.route) {
                    GymListScreen(
                        navigateToGymScheme = {
                            navController.navigate(Route.GymScheme.route) {
                                launchSingleTop = true
                                restoreState = true
                            }
                        },
                    )
                }
                composable(Route.GymScheme.route) {
                    GymSchemeScreen(
                        navigateToCamera = {
                            navController.navigate(Route.Camera.route) {
                                launchSingleTop = true
                                restoreState = true
                            }
                        },
                    )
                }

                composable(Route.Account.route) {
                    AccountScreen(
                        navigateToAuthorization = {
                            navController.navigate(Route.Authorization.route) {
                                launchSingleTop = true
                                restoreState = true
                            }
                        },
                    )
                }
                composable(Route.Camera.route) { backStackEntry ->
//                    val camera: Route.Camera = backStackEntry.toRoute()
                    // todo: разобраться, что делать с несколькими камерами
                    CameraScreen(
                        navigateToGymScheme = {
                            navController.navigate(Route.GymScheme.route) {
                                launchSingleTop = true
                                restoreState = true
                            }
                        },
                    )
                }
            }
        }
    }
}