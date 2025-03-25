package ru.hse.gymvision.ui.composables

import android.net.Uri
import android.util.Log
import android.view.ViewGroup
import androidx.annotation.OptIn
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.media3.common.MediaItem
import androidx.media3.common.PlaybackException
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.PlayerView
import ru.hse.gymvision.R
import ru.hse.gymvision.domain.CameraMovement
import ru.hse.gymvision.domain.CameraRotation
import ru.hse.gymvision.domain.CameraZoom
import ru.hse.gymvision.domain.usecase.camera.MoveCameraUseCase
import ru.hse.gymvision.domain.usecase.camera.RotateCameraUseCase
import ru.hse.gymvision.domain.usecase.camera.ZoomCameraUseCase


@Composable
fun myPlayerView(
    videoUrl: String,
    playWhenReady: Boolean,
    showControls: MutableState<Boolean>,
    onError: (PlaybackException) -> Unit
): ExoPlayer {
    val player = rememberExoPlayerWithLifeCycle(
        videoUrl,
        playWhenReady,
        onError)
    val context = LocalContext.current
    val density = LocalDensity.current
    var videoWidth = remember { 0.dp }
    var videoHeight = remember { 0.dp }

    Box(modifier = Modifier
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
            modifier = Modifier.fillMaxSize()
                .onGloballyPositioned {
                    videoWidth = with(density) { it.size.width.toDp() }
                    videoHeight = with(density) { it.size.height.toDp() }
                }
            ,
            factory = {
                PlayerView(context).apply {
                    layoutParams = ViewGroup.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT
                    )
                    this.player = player
                    useController = false
                }
            }
        )

        if (showControls.value) {
            Box(modifier = Modifier.fillMaxSize().padding(16.dp), contentAlignment = Alignment.Center) {
                PauseButton(player)
            }
            ControlButton(iconId = R.drawable.ic_arrow_left,
                contentDescription = "Move camera to the left",
                alignment = Alignment.CenterStart) {
                MoveCameraUseCase().execute(CameraMovement.LEFT)
            }
            ControlButton(iconId = R.drawable.ic_arrow_right,
                contentDescription = "Move camera to the right",
                alignment = Alignment.CenterEnd) {
                MoveCameraUseCase().execute(CameraMovement.RIGHT)
            }
            ControlButton(iconId = R.drawable.ic_arrow_up,
                contentDescription = "Move camera up",
                alignment = Alignment.TopCenter) {
                MoveCameraUseCase().execute(CameraMovement.UP)
            }
            ControlButton(iconId = R.drawable.ic_arrow_down,
                contentDescription = "Move camera down",
                alignment = Alignment.BottomCenter) {
                MoveCameraUseCase().execute(CameraMovement.DOWN)
            }
            ControlButton(iconId = R.drawable.ic_zoom_out,
                contentDescription = "Zoom out",
                alignment = Alignment.TopStart) {
                ZoomCameraUseCase().execute(CameraZoom.OUT)
            }
            ControlButton(iconId = R.drawable.ic_zoom_in,
                contentDescription = "Zoom in",
                alignment = Alignment.TopEnd) {
                ZoomCameraUseCase().execute(CameraZoom.IN)
            }
            ControlButton(iconId = R.drawable.ic_rotate_left,
                contentDescription = "Rotate camera to the left",
                alignment = Alignment.BottomStart) {
                RotateCameraUseCase().execute(CameraRotation.LEFT)
            }
            ControlButton(iconId = R.drawable.ic_rotate_right,
                contentDescription = "Rotate camera to the right",
                alignment = Alignment.BottomEnd) {
                RotateCameraUseCase().execute(CameraRotation.RIGHT)
            }
        }


    }

    return player
}


@OptIn(UnstableApi::class)
@Composable
fun rememberExoPlayerWithLifeCycle(
    videoUrl: String,
    playWhenReady: Boolean,
    onError: (PlaybackException) -> Unit
): ExoPlayer {
    val context = LocalContext.current
    val exoPlayer = remember(videoUrl) {
        ExoPlayer.Builder(context)
            .build()
            .apply {
                val uri = Uri.parse(videoUrl)
                setMediaItem(MediaItem.fromUri(uri))
                prepare()
                addListener(object : Player.Listener {
                    override fun onPlayerError(error: PlaybackException) {
                        onError(error)
                    }
                })
                this.playWhenReady = playWhenReady
                Log.d("MyPlayerView", "ExoPlayer created")
            }
    }
    var appInBackground by remember {
        mutableStateOf(false)
    }
    val lifecycleOwner = androidx.lifecycle.compose.LocalLifecycleOwner.current
    DisposableEffect(lifecycleOwner, appInBackground) {
        val lifecycleObserver = getExoPlayerLifecycleObserver(exoPlayer, appInBackground) {
            appInBackground = it
        }
        lifecycleOwner.lifecycle.addObserver(lifecycleObserver)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(lifecycleObserver)
        }
    }
    return exoPlayer
}

fun getExoPlayerLifecycleObserver(
    exoPlayer: ExoPlayer,
    wasAppInBackground: Boolean,
    setWasAppInBackground: (Boolean) -> Unit
): LifecycleEventObserver =
    LifecycleEventObserver { _, event ->
        when (event) {
            Lifecycle.Event.ON_RESUME -> {
                if (wasAppInBackground)
                    exoPlayer.playWhenReady = true
                setWasAppInBackground(false)
            }

            Lifecycle.Event.ON_PAUSE -> {
                setWasAppInBackground(true)
            }

            Lifecycle.Event.ON_STOP -> {
                exoPlayer.playWhenReady = false
                setWasAppInBackground(true)
            }

            Lifecycle.Event.ON_DESTROY -> {
                exoPlayer.release()
            }

            else -> {}
        }
    }

