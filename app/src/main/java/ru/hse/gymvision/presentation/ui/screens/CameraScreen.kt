package ru.hse.gymvision.presentation.ui.screens

import android.util.Log
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.media3.exoplayer.ExoPlayer
import ru.hse.gymvision.domain.exampledata.videoUrlExample
import ru.hse.gymvision.presentation.ui.BottomNavScreen
import ru.hse.gymvision.presentation.ui.composables.MyBottomAppBar
import ru.hse.gymvision.presentation.ui.composables.MyTitle
import ru.hse.gymvision.presentation.ui.composables.myPlayerView

@Composable
fun CameraScreen() {
    val showControls = remember { mutableStateOf(false) }
    var player: ExoPlayer? = null
    val videoUrl = videoUrlExample

    Scaffold { paddingValues ->
        Box(modifier = Modifier.padding(paddingValues).padding(16.dp)) {
            MyTitle(text = "Камера")
            player = myPlayerView(
                videoUrl,
                true,
                showControls) { error ->
                Log.d("CameraScreen", "Error: $error")
            }
        }
    }
}