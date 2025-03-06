package ru.hse.gymvision.ui.camera

import android.util.Log
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.media3.exoplayer.ExoPlayer
import androidx.navigation.NavHostController
import ru.hse.gymvision.domain.exampledata.videoUrlExample
import ru.hse.gymvision.ui.composables.MyTitle
import ru.hse.gymvision.ui.composables.myPlayerView

@Composable
fun CameraScreen(
    navigateToGymScheme: () -> Unit,
) {
    val showControls = remember { mutableStateOf(false) }
    var player: ExoPlayer? = null
    val videoUrl = videoUrlExample

    Box(
        modifier = Modifier
            .padding(top = 16.dp, bottom = 0.dp, start = 16.dp, end = 16.dp)
    ) {
        MyTitle(text = "Камера")
        player = myPlayerView(
            videoUrl,
            true,
            showControls
        ) { error ->
            Log.d("CameraScreen", "Error: $error")
        }
    }
}