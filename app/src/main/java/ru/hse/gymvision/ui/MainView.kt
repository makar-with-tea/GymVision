package ru.hse.gymvision.ui

import androidx.activity.ComponentActivity
import androidx.compose.runtime.Composable
import ru.alexgladkov.odyssey.compose.setup.OdysseyConfiguration
import ru.alexgladkov.odyssey.compose.setup.StartScreen
import ru.alexgladkov.odyssey.compose.setup.setNavigationContent
import ru.hse.gymvision.navigation.navigationGraph

@Composable
fun MainView(activity: ComponentActivity) {
    val odysseyConfiguration = OdysseyConfiguration(
        canvas = activity,
        startScreen = StartScreen.Custom("login")
    )

    setNavigationContent(odysseyConfiguration, onApplicationFinish = {
        activity.finishAffinity()
    }) {
        navigationGraph()
    }
}