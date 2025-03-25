package ru.hse.gymvision.ui.navigation

import android.util.Log
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import ru.hse.gymvision.R


@Composable
fun BottomNavigationBar(
    navigateToGymList: () -> Unit,
    navigateToGymScheme: () -> Unit,
    navigateToAccount: () -> Unit,
    currentRoute: String?
) {


    NavigationBar {
        NavigationBarItem(
            selected = currentRoute == Route.GymList.route,
            icon = {
                Icon(
                    modifier = Modifier.size(32.dp),
                    imageVector = ImageVector.vectorResource(R.drawable.ic_home),
                    contentDescription = "Home"
                )
            },
            onClick = {
                navigateToGymList()
                Log.d("Navigation", "BottomNav to GymList from $currentRoute")
            }
        )

        NavigationBarItem(
            selected = currentRoute == Route.GymScheme.route,
            icon = {
                Icon(
                    modifier = Modifier.size(32.dp),
                    imageVector = ImageVector.vectorResource(R.drawable.ic_fitness_center),
                    contentDescription = "Gym"
                )
            },
            onClick = {
                navigateToGymScheme()
                Log.d("Navigation", "BottomNav to GymScheme from $currentRoute")
            }
        )

        NavigationBarItem(
            selected = currentRoute == Route.Account.route,
            icon = {
                Icon(
                    modifier = Modifier.size(32.dp),
                    imageVector = ImageVector.vectorResource(R.drawable.ic_account_box),
                    contentDescription = "Account"
                )
            },
            onClick = {
                navigateToAccount()
                Log.d("Navigation", "BottomNav to Account from $currentRoute")
            }
        )
    }
}