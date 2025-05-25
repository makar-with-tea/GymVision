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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.net.toUri
import org.videolan.libvlc.LibVLC
import org.videolan.libvlc.Media
import org.videolan.libvlc.MediaPlayer
import org.videolan.libvlc.util.VLCVideoLayout
import ru.hse.gymvision.R
import ru.hse.gymvision.domain.CameraMovement
import ru.hse.gymvision.domain.CameraRotation
import ru.hse.gymvision.domain.CameraZoom

@Composable
fun mainPlayerView(
    videoUrl: String,
    playWhenReady: Boolean,
    showControls: MutableState<Boolean>,
    onError: (Exception) -> Unit,
    onRotateCamera: (CameraRotation) -> Unit,
    onMoveCamera: (CameraMovement) -> Unit,
    onZoomCamera: (CameraZoom) -> Unit,
    onChangeAi: (Boolean) -> Unit,
    onPlay: () -> Unit
): MediaPlayer {
    val context = LocalContext.current
    val libVLC = remember { LibVLC(context) }
    val mediaPlayer = remember { MediaPlayer(libVLC) }
    val videoUri = videoUrl.toUri()
    val vlcErrorText = stringResource(R.string.vlc_error)
    val isAIEnabled = remember { mutableStateOf(false) }

    DisposableEffect(videoUrl) {
        val media = Media(libVLC, videoUri)
        mediaPlayer.media = media
        mediaPlayer.setEventListener { event ->
            if (event.type == MediaPlayer.Event.EncounteredError) {
                onError(Exception(vlcErrorText))
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
                    mediaPlayer.attachViews(
                        this, null, false, false
                    )
                }
            }
        )

        if (showControls.value) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                contentAlignment = Alignment.Center
            ) {
                PauseButton(mediaPlayer, onPlay)
            }
            ControlButton(
                iconId = R.drawable.ic_arrow_left,
                contentDescription = stringResource(R.string.rotate_camera_left_description),
                alignment = Alignment.CenterStart,
                onPress = { onRotateCamera(CameraRotation.LEFT) },
                onRelease = { onRotateCamera(CameraRotation.STOP) }
            )
            ControlButton(
                iconId = R.drawable.ic_arrow_right,
                contentDescription = stringResource(R.string.rotate_camera_right_description),
                alignment = Alignment.CenterEnd,
                onPress = { onRotateCamera(CameraRotation.RIGHT) },
                onRelease = { onRotateCamera(CameraRotation.STOP) }
            )
            ControlButton(
                iconId = R.drawable.ic_arrow_up,
                contentDescription = stringResource(R.string.move_camera_up_description),
                alignment = Alignment.TopCenter,
                onPress = { onMoveCamera(CameraMovement.UP) },
                onRelease = { onMoveCamera(CameraMovement.STOP) }
            )
            ControlButton(
                iconId = R.drawable.ic_arrow_down,
                contentDescription = stringResource(R.string.move_camera_down_description),
                alignment = Alignment.BottomCenter,
                onPress = { onMoveCamera(CameraMovement.DOWN) },
                onRelease = { onMoveCamera(CameraMovement.STOP) }
            )
            ControlButton(
                iconId = R.drawable.ic_zoom_out,
                contentDescription = stringResource(R.string.zoom_out_description),
                alignment = Alignment.TopStart,
                onPress = { onZoomCamera(CameraZoom.OUT) },
                onRelease = { onZoomCamera(CameraZoom.STOP) }
            )
            ControlButton(
                iconId = R.drawable.ic_zoom_in,
                contentDescription = stringResource(R.string.zoom_in_description),
                alignment = Alignment.TopEnd,
                onPress = { onZoomCamera(CameraZoom.IN) },
                onRelease = { onZoomCamera(CameraZoom.STOP) }
            )
            ControlButtonSimple(
                iconId = if (isAIEnabled.value)
                    R.drawable.ic_sparkles_crossed else R.drawable.ic_sparkles,
                contentDescription = stringResource(R.string.ai_analysis_description),
                alignment = Alignment.BottomStart,
            ) {
                isAIEnabled.value = !isAIEnabled.value
                onChangeAi(isAIEnabled.value)
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
    val vlcErrorText = stringResource(R.string.vlc_error)

    DisposableEffect(videoUrl) {
        val media = Media(libVLC, videoUri)
        mediaPlayer.media = media
        mediaPlayer.setEventListener { event ->
            if (event.type == MediaPlayer.Event.EncounteredError) {
                onError(Exception(vlcErrorText))
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
                    mediaPlayer.attachViews(
                        this, null, false, false
                    )
                }
            }
        )

        if (showControls.value) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                contentAlignment = Alignment.Center
            ) {
                PauseButton(mediaPlayer, onPlay)
            }
            ControlButtonSimple(
                iconId = R.drawable.ic_crop_free,
                contentDescription = stringResource(R.string.make_main_camera_description),
                alignment = Alignment.CenterStart
            ) {
                onMakeMainCamera()
            }
            ControlButtonSimple(
                iconId = R.drawable.ic_delete,
                contentDescription = stringResource(R.string.delete_camera_description),
                alignment = Alignment.CenterEnd
            ) {
                onDeleteCamera()
            }
        }
    }
    return mediaPlayer
}
