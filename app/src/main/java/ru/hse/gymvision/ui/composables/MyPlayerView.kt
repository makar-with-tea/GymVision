package ru.hse.gymvision.ui.composables

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
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
    isAiEnabled: Boolean,
    showControls: MutableState<Boolean>,
    onError: (Exception) -> Unit,
    onRotateCamera: (CameraRotation) -> Unit,
    onMoveCamera: (CameraMovement) -> Unit,
    onZoomCamera: (CameraZoom) -> Unit,
    onChangeAi: (Boolean) -> Unit,
    onPlay: () -> Unit,
): MediaPlayer {
    Log.d("camera", "Creating main player view for URL: $videoUrl")
    val context = LocalContext.current
    val libVLC = remember { LibVLC(context) }
    val mediaPlayer = remember { MediaPlayer(libVLC) }
    val videoUri = videoUrl.toUri()
    val vlcErrorText = stringResource(R.string.vlc_error)
    val isAIEnabled = remember { mutableStateOf(isAiEnabled) }

    DisposableEffect(videoUrl) {
        val media = Media(libVLC, videoUri)
        Log.d("camera", "Setting media for player: $videoUrl")
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
            Log.d("camera", "Releasing media player: $videoUrl")
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
            Column(
                modifier = Modifier
                    .width(172.dp)
                    .padding(16.dp)
                    .align(Alignment.BottomStart),
            ) {
                Box(
                    contentAlignment = Alignment.Center
                ) {
                    Image(
                        modifier = Modifier
                            .padding(horizontal = 16.dp)
                            .fillMaxWidth()
                            .height(40.dp),
                        painter = painterResource(R.drawable.ic_controller_horizontal),
                        contentDescription = null, // todo add content description
                        colorFilter = ColorFilter.tint(
                            MaterialTheme.colorScheme.primaryContainer
                        )
                    )

                    Row(
                        modifier = Modifier
                            .padding(horizontal = 16.dp)
                            .fillMaxWidth()
                            .height(40.dp),
                        horizontalArrangement = Arrangement.SpaceEvenly,
                    ) {
                        ControlButton(
                            iconId = R.drawable.ic_remove,
                            contentDescription = stringResource(R.string.zoom_out_description),
                            onPress = { onZoomCamera(CameraZoom.OUT) },
                            onRelease = { onZoomCamera(CameraZoom.STOP) },
                            size = 26.dp,
                            iconSize = 20.dp,
                            padding = 6.dp
                        )
                        ControlButton(
                            iconId = R.drawable.ic_add,
                            contentDescription = stringResource(R.string.zoom_in_description),
                            onPress = { onZoomCamera(CameraZoom.IN) },
                            onRelease = { onZoomCamera(CameraZoom.STOP) },
                            size = 26.dp,
                            iconSize = 20.dp,
                            padding = 6.dp
                        )
                    }
                }
                Box(
                    modifier = Modifier
                        .size(140.dp)
                        .padding(10.dp)
                ) {
                    Image(
                        modifier = Modifier.fillMaxSize(),
                        painter = painterResource(R.drawable.ic_controller),
                        contentDescription = null, // todo add content description
                        alignment = Alignment.Center,
                        colorFilter = ColorFilter.tint(
                            MaterialTheme.colorScheme.primaryContainer
                        )
                    )

                    PauseButton(
                        modifier = Modifier.align(Alignment.Center),
                        player = mediaPlayer,
                        onPlay = onPlay
                    )

                    ControlButton(
                        iconId = R.drawable.ic_arrow_left,
                        contentDescription = stringResource(R.string.rotate_camera_left_description),
                        modifier = Modifier.align(Alignment.CenterStart),
                        onPress = { onRotateCamera(CameraRotation.LEFT) },
                        onRelease = { onRotateCamera(CameraRotation.STOP) },
                        size = 20.dp,
                        iconSize = 20.dp,
                        padding = 6.dp
                    )
                    ControlButton(
                        iconId = R.drawable.ic_arrow_right,
                        contentDescription = stringResource(R.string.rotate_camera_right_description),
                        modifier = Modifier.align(Alignment.CenterEnd),
                        onPress = { onRotateCamera(CameraRotation.RIGHT) },
                        onRelease = { onRotateCamera(CameraRotation.STOP) },
                        size = 20.dp,
                        iconSize = 20.dp,
                        padding = 6.dp
                    )
                    ControlButton(
                        iconId = R.drawable.ic_arrow_up,
                        contentDescription = stringResource(R.string.move_camera_up_description),
                        modifier = Modifier.align(Alignment.TopCenter),
                        onPress = { onMoveCamera(CameraMovement.UP) },
                        onRelease = { onMoveCamera(CameraMovement.STOP) },
                        size = 20.dp,
                        iconSize = 20.dp,
                        padding = 6.dp
                    )
                    ControlButton(
                        iconId = R.drawable.ic_arrow_down,
                        contentDescription = stringResource(R.string.move_camera_down_description),
                        modifier = Modifier.align(Alignment.BottomCenter),
                        onPress = { onMoveCamera(CameraMovement.DOWN) },
                        onRelease = { onMoveCamera(CameraMovement.STOP) },
                        size = 20.dp,
                        iconSize = 20.dp,
                        padding = 6.dp
                    )
                }
            }
            ControlButtonSimple(
                modifier = Modifier
                    .padding(vertical = 86.dp, horizontal = 18.dp)
                    .size(50.dp)
                    .align(Alignment.BottomEnd)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primaryContainer.copy(0.5f))
                    .border(2.dp, MaterialTheme.colorScheme.primaryContainer.copy(0.7f), CircleShape)
                ,
                iconId = if (isAIEnabled.value)
                    R.drawable.ic_sparkles_crossed else R.drawable.ic_sparkles,
                contentDescription = stringResource(R.string.ai_analysis_description),
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
            Log.d("camera secondary", "Releasing media player and media")
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
            PauseButton(
                Modifier.align(Alignment.Center),
                mediaPlayer,
                onPlay,
                size = 40.dp,
                iconSize = 32.dp
            )
            ControlButtonSimple(
                modifier = Modifier.align(Alignment.CenterStart),
                iconId = R.drawable.ic_crop_free,
                contentDescription = stringResource(R.string.make_main_camera_description),
                onClick = onMakeMainCamera
            )
            ControlButtonSimple(
                modifier = Modifier.align(Alignment.CenterEnd),
                iconId = R.drawable.ic_delete,
                contentDescription = stringResource(R.string.delete_camera_description),
                onClick = onDeleteCamera
            )
        }
    }
    return mediaPlayer
}
