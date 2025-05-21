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
import java.net.URLEncoder
import java.nio.charset.StandardCharsets

sealed class Route(val route: String) {
    data object Authorization: Route("authorization")

    data object Registration: Route("registration")

    data object GymList: Route("gym_list")

    data object GymScheme: Route("gym_scheme/{gym_id}") {
        fun createRoute(gymId: Int): String {
            return "gym_scheme/$gymId"
        }
    }

    data object Account: Route("account")

    data object Camera: Route("camera/{gym_id}/{new_camera_id}") {
        fun createRoute(gymId: Int, newCameraId: Int): String {
            return "camera/$gymId/$newCameraId"
        }
    }
}

@Composable
fun MainView() {
    Log.d("MainView", "SetUpNavHost")
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    val navigateToGymList = {
        navController.navigate(Route.GymList.route) {
            launchSingleTop = true
            restoreState = true
        }
    }
    val navigateToGymScheme = {
        navController.navigate(Route.GymScheme.route) {
            launchSingleTop = true
            restoreState = true
        }
    }
    val navigateToAccount = {
        navController.navigate(Route.Account.route) {
            launchSingleTop = true
            restoreState = true
        }
    }

    Scaffold(
        bottomBar = {
            Log.d("SetUpNavHost", "Current route: $currentRoute")
            if (currentRoute != Route.Authorization.route && currentRoute != Route.Registration.route) {
                BottomNavigationBar(
                    navigateToGymList = navigateToGymList,
                    navigateToGymScheme = navigateToGymScheme,
                    navigateToAccount = navigateToAccount,
                    currentRoute = currentRoute
                )
            }
        }
    ) { paddingValues ->
        Box(modifier = Modifier.padding(paddingValues)) {
            NavHost(navController = navController, startDestination = Route.Authorization.route) {
                composable(Route.Authorization.route) {
                    AuthorizationScreen(
                        navigateToRegistration = {
                            navController.navigate(Route.Registration.route) {
                                popUpTo(Route.Authorization.route) {
                                    inclusive = true
                                    saveState = false
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                            Log.d("Navigation", "Navigate to Registration from Authorization")
                        },
                        navigateToGymList = {
                            navController.navigate(Route.GymList.route) {
                                launchSingleTop = true
                                restoreState = true
                            }
                            Log.d("Navigation", "Navigate to GymList from Authorization")
                        },
                    )
                }
                composable(Route.Registration.route) {
                    RegistrationScreen(
                        navigateToGymList = {
                            navController.navigate(Route.GymList.route) {
                                popUpTo(Route.Registration.route) {
                                    inclusive = true
                                    saveState = false
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                            Log.d("Navigation", "Navigate to GymList from Registration")
                        },
                        navigateToAuthorization = {
                            navController.navigate(Route.Authorization.route) {
                                launchSingleTop = true
                                restoreState = true
                            }
                            Log.d("Navigation", "Navigate to Authorization from Registration")
                        }
                    )
                }
                composable(Route.GymList.route) {
                    GymListScreen(
                        navigateToGymScheme = { gymId: Int ->
                            navController.navigate(Route.GymScheme.createRoute(gymId)) {
                                launchSingleTop = true
                                restoreState = true
                            }
                            Log.d("Navigation", "Navigate to GymScheme from GymList")
                        },
                    )
                }

                composable(Route.GymScheme.route) { backStackEntry ->
                    val gymId = backStackEntry.arguments?.getString("gym_id")?.toIntOrNull()
                    GymSchemeScreen(
                        id = gymId,
                        navigateToCamera = { id: Int, newCameraId: Int ->
                            navController.navigate(Route.Camera.createRoute(id, newCameraId)) {
                                launchSingleTop = true
                                restoreState = true
                            }
                            Log.d("Navigation", "Navigate to Camera from GymScheme")
                        },
                        navigateToGymList = {
                            navController.navigate(Route.GymList.route) {
                                launchSingleTop = true
                                restoreState = true
                            }
                            Log.d("Navigation", "Navigate to GymList from GymScheme")
                        },
                    )
                }

                composable(Route.Account.route) {
                    AccountScreen(
                        navigateToAuthorization = {
                            navController.navigate(Route.Authorization.route) {
                                popUpTo(Route.Authorization.route) {
                                    inclusive = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                            Log.d("Navigation", "Navigate to Authorization from Account")
                        },
                    )
                }
                composable(Route.Camera.route) { backStackEntry ->
                    val gymId = backStackEntry.arguments?.getString("gym_id")?.toIntOrNull()
                    val newCameraId = backStackEntry.arguments?.getString("new_camera_id")?.toIntOrNull()
                    if (gymId == null) {
                        Log.e("CameraScreen", "Gym ID is null")
                        return@composable
                    }
                    CameraScreen(
                        gymId = gymId,
                        newCameraId = newCameraId,
                        navigateToGymScheme = {
                            navController.navigate(Route.GymScheme.route) {
                                launchSingleTop = true
                                restoreState = true
                            }
                            Log.d("Navigation", "Navigate to GymScheme from Camera")
                        },
                    )
                }
            }
        }
    }
}