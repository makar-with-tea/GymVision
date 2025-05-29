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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.videolan.libvlc.MediaPlayer
import ru.hse.gymvision.R

@Composable
fun PauseButton(
    modifier: Modifier = Modifier,
    player: MediaPlayer,
    onPlay: () -> Unit,
    size: Dp = 26.dp,
    iconSize: Dp = 20.dp,
) {
    var isPlaying by remember { mutableStateOf(player.isPlaying) }

    LaunchedEffect(player.isPlaying) {
        withContext(Dispatchers.Main) {
            isPlaying = player.isPlaying
        }
    }

    IconButton(
        modifier = modifier.size(size),
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
            modifier = Modifier.size(iconSize),
            imageVector = if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
            contentDescription = stringResource(R.string.pause_description),
            tint = MaterialTheme.colorScheme.primary
        )
    }
}
