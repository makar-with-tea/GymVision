package ru.hse.gymvision.presentation.ui.composables

import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavController
import ru.hse.gymvision.presentation.navigation.LocalNavController

@Composable
fun BottomBar() {
    val navController = LocalNavController.current
    BottomAppBar {
        Button(onClick = { navController.navigate("gymList") }) {
        }
        Button(onClick = { navController.navigate("gymScheme") }) {
        }
        Button(onClick = { navController.navigate("profile") }) {
        }
    }
}