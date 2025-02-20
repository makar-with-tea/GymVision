package ru.hse.gymvision.presentation.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import ru.hse.gymvision.presentation.ui.BottomNavScreen
import ru.hse.gymvision.presentation.ui.composables.MyBottomAppBar
import ru.hse.gymvision.presentation.ui.composables.MyTitle

@Composable
fun AccountScreen() {
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        bottomBar = { MyBottomAppBar(BottomNavScreen.PROFILE) }
    ) { paddingValues ->
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top,
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
                .padding(16.dp)
        ) {
            MyTitle(text = "Мой профиль")
        }
    }
}