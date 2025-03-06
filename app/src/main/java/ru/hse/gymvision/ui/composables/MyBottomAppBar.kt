package ru.hse.gymvision.ui.composables

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.BottomAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import ru.alexgladkov.odyssey.compose.extensions.push
import ru.alexgladkov.odyssey.compose.local.LocalRootController
import ru.hse.gymvision.R
import ru.hse.gymvision.ui.BottomNavScreen

@Composable
fun MyBottomAppBar(curScreen: BottomNavScreen = BottomNavScreen.NONE) {
    val rootController = LocalRootController.current
    BottomAppBar(
        modifier = Modifier.height(100.dp)
    ) {
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier
                .fillMaxWidth()
                .height(40.dp)
                .padding(24.dp, 0.dp, 24.dp, 0.dp)
        ) {
            MyBottomNavButton(curScreen, BottomNavScreen.HOME, painterResource(id = R.drawable.ic_home), "Home") {
                if (curScreen != BottomNavScreen.HOME)
                    rootController.push("gymList")
            }
            MyBottomNavButton(curScreen, BottomNavScreen.GYM_SCHEME, painterResource(id = R.drawable.ic_fitness_center), "Gym scheme") {
                if (curScreen != BottomNavScreen.GYM_SCHEME)
                    rootController.push("gymScheme")
            }
            MyBottomNavButton(curScreen, BottomNavScreen.PROFILE, painterResource(id = R.drawable.ic_account_box),"Profile") {
                if (curScreen != BottomNavScreen.PROFILE)
                    rootController.push("profile")
            }

        }
    }
}