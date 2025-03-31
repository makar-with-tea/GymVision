package ru.hse.gymvision.ui.composables

import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.videolan.libvlc.MediaPlayer

@Composable
fun PauseButton(player: MediaPlayer, onPlay: () -> Unit) {
    var isPlaying by remember { mutableStateOf(player.isPlaying) }

    LaunchedEffect(player.isPlaying) {
        withContext(Dispatchers.Main) {
            isPlaying = player.isPlaying
        }
    }

    IconButton(
        modifier = Modifier.size(64.dp),
        onClick = {
            if (isPlaying) {
                player.pause()
            } else {
                player.play()
            }
            isPlaying = !isPlaying
            onPlay()
        }
    ) {
        Icon(
            modifier = Modifier.size(50.dp),
            imageVector = if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
            contentDescription = "пауза/воспроизведение",
            tint = MaterialTheme.colorScheme.primary
        )
    }
}