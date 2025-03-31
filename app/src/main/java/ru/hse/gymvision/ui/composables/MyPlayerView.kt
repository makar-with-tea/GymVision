package ru.hse.gymvision.ui.composables

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import org.videolan.libvlc.LibVLC
import org.videolan.libvlc.Media
import org.videolan.libvlc.MediaPlayer
import org.videolan.libvlc.util.VLCVideoLayout
import ru.hse.gymvision.R
import ru.hse.gymvision.domain.CameraMovement
import ru.hse.gymvision.domain.CameraRotation
import ru.hse.gymvision.domain.CameraZoom
import androidx.core.net.toUri

@Composable
fun mainPlayerView(
    videoUrl: String,
    playWhenReady: Boolean,
    showControls: MutableState<Boolean>,
    onError: (Exception) -> Unit,
    onRotateCamera: (CameraRotation) -> Unit,
    onMoveCamera: (CameraMovement) -> Unit,
    onZoomCamera: (CameraZoom) -> Unit,
    onPlay: () -> Unit
): MediaPlayer {
    val context = LocalContext.current
    val libVLC = remember { LibVLC(context) }
    val mediaPlayer = remember { MediaPlayer(libVLC) }
    val videoUri = videoUrl.toUri()

    DisposableEffect(videoUrl) {
        val media = Media(libVLC, videoUri)
        mediaPlayer.media = media
        mediaPlayer.setEventListener { event ->
            if (event.type == MediaPlayer.Event.EndReached) {
                // Handle completion
            } else if (event.type == MediaPlayer.Event.EncounteredError) {
                onError(Exception("VLC error"))
            }
        }
        if (playWhenReady) {
            mediaPlayer.play()
        }
        onDispose {
            mediaPlayer.release()
            media.release()
        }
    }

    Box(
        modifier = Modifier
            .padding(0.dp)
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .pointerInput(Unit) {
                detectTapGestures(
                    onTap = {
                        showControls.value = !showControls.value
                    }
                )
            },
        contentAlignment = Alignment.Center
    ) {
        AndroidView(
            modifier = Modifier.fillMaxSize(),
            factory = {
                VLCVideoLayout(context).apply {
                    mediaPlayer.attachViews(this, null, false, false)
                }
            }
        )

        if (showControls.value) {
            Box(
                modifier = Modifier.fillMaxSize().padding(16.dp),
                contentAlignment = Alignment.Center
            ) {
                 PauseButton(mediaPlayer, onPlay)
            }
            ControlButton(
                iconId = R.drawable.ic_arrow_left,
                contentDescription = "Move camera left",
                alignment = Alignment.CenterStart
            ) {
                onMoveCamera(CameraMovement.LEFT)
            }
            ControlButton(
                iconId = R.drawable.ic_arrow_right,
                contentDescription = "Move camera right",
                alignment = Alignment.CenterEnd
            ) {
                onMoveCamera(CameraMovement.RIGHT)
            }
            ControlButton(
                iconId = R.drawable.ic_arrow_up,
                contentDescription = "Move camera up",
                alignment = Alignment.TopCenter
            ) {
                onMoveCamera(CameraMovement.UP)
            }
            ControlButton(
                iconId = R.drawable.ic_arrow_down,
                contentDescription = "Move camera down",
                alignment = Alignment.BottomCenter
            ) {
                onMoveCamera(CameraMovement.DOWN)
            }
            ControlButton(
                iconId = R.drawable.ic_zoom_out,
                contentDescription = "Zoom out",
                alignment = Alignment.TopStart
            ) {
                onZoomCamera(CameraZoom.OUT)
            }
            ControlButton(
                iconId = R.drawable.ic_zoom_in,
                contentDescription = "Zoom in",
                alignment = Alignment.TopEnd
            ) {
                onZoomCamera(CameraZoom.IN)
            }
            ControlButton(
                iconId = R.drawable.ic_rotate_left,
                contentDescription = "Rotate camera left",
                alignment = Alignment.BottomStart
            ) {
                onRotateCamera(CameraRotation.LEFT)
            }
            ControlButton(
                iconId = R.drawable.ic_rotate_right,
                contentDescription = "Rotate camera right",
                alignment = Alignment.BottomEnd
            ) {
                onRotateCamera(CameraRotation.RIGHT)
            }
        }
    }
    return mediaPlayer
}

@Composable
fun secondaryPlayerView(
    videoUrl: String,
    playWhenReady: Boolean,
    showControls: MutableState<Boolean>,
    onError: (Exception) -> Unit,
    onMakeMainCamera: () -> Unit,
    onDeleteCamera: () -> Unit,
    onPlay: () -> Unit
): MediaPlayer {
    val context = LocalContext.current
    val libVLC = remember { LibVLC(context) }
    val mediaPlayer = remember { MediaPlayer(libVLC) }
    val videoUri = videoUrl.toUri()

    DisposableEffect(videoUrl) {
        val media = Media(libVLC, videoUri)
        mediaPlayer.media = media
        mediaPlayer.setEventListener { event ->
            if (event.type == MediaPlayer.Event.EndReached) {
                // Handle completion
            } else if (event.type == MediaPlayer.Event.EncounteredError) {
                onError(Exception("VLC error"))
            }
        }
        if (playWhenReady) {
            mediaPlayer.play()
        }
        onDispose {
            mediaPlayer.release()
            media.release()
        }
    }

    Box(
        modifier = Modifier
            .padding(
                start = 0.dp,
                end = 0.dp,
                bottom = 50.dp,
                top = 0.dp
            )
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .pointerInput(Unit) {
                detectTapGestures(
                    onTap = {
                        showControls.value = !showControls.value
                    }
                )
            },
        contentAlignment = Alignment.Center
    ) {
        AndroidView(
            modifier = Modifier.fillMaxSize(),
            factory = {
                VLCVideoLayout(context).apply {
                    mediaPlayer.attachViews(this, null, false, false)
                }
            }
        )

        if (showControls.value) {
            Box(
                modifier = Modifier.fillMaxSize().padding(16.dp),
                contentAlignment = Alignment.Center
            ) {
                PauseButton(mediaPlayer, onPlay)
            }
            ControlButton(
                iconId = R.drawable.ic_crop_free,
                contentDescription = "Сделать главной камерой",
                alignment = Alignment.CenterStart
            ) {
                onMakeMainCamera()
            }
            ControlButton(
                iconId = R.drawable.ic_delete,
                contentDescription = "Удалить камеру",
                alignment = Alignment.CenterEnd
            ) {
                onDeleteCamera()
            }
        }
    }
    return mediaPlayer
}