package ru.hse.gymvision.presentation.ui.composables

import android.net.Uri
import android.view.ViewGroup
import androidx.annotation.OptIn
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.media3.common.MediaItem
import androidx.media3.common.PlaybackException
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.datasource.DefaultHttpDataSource
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.source.DefaultMediaSourceFactory
import androidx.media3.exoplayer.source.MediaSource
import androidx.media3.ui.PlayerView
import kotlinx.coroutines.delay

const val HIDE_CONTROLS_DELAY = 4000L

@Composable
fun myPlayerView(
    videoUri: Uri,
    playWhenReady: Boolean,
    showControls: MutableState<Boolean>,
    onError: (PlaybackException) -> Unit
): ExoPlayer {
    val player = rememberExoPlayerWithLifeCycle(
        videoUri.toString(),
        playWhenReady,
        onError)
    val context = LocalContext.current
    var lastClickTime by remember { mutableLongStateOf(System.currentTimeMillis()) }

    LaunchedEffect(lastClickTime) {
        delay(HIDE_CONTROLS_DELAY)
        showControls.value = false
    }

    Box(modifier = Modifier
        .fillMaxSize()
        .background(MaterialTheme.colorScheme.background)
        .pointerInput(Unit) {
            detectTapGestures(
                onTap = {
                    if (showControls.value) {
                        showControls.value = false
                    } else {
                        showControls.value = true
                        lastClickTime = System.currentTimeMillis()
                    }
                }
            )
        }
    ) {
        AndroidView(
            modifier = Modifier.fillMaxSize(),
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
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {

                PauseButton(player, onTouch = {
                    lastClickTime = System.currentTimeMillis()
                })
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
                val dataSourceFactory = DefaultHttpDataSource.Factory()
                val mediaSourceFactory = DefaultMediaSourceFactory(dataSourceFactory)
                val mediaSource: MediaSource = mediaSourceFactory.createMediaSource(MediaItem.fromUri(Uri.parse(videoUrl)))
                setMediaSource(mediaSource)
                prepare()
                addListener(object : Player.Listener {
                    override fun onPlayerError(error: PlaybackException) {
                        onError(error)
                    }
                })
                this.playWhenReady = playWhenReady
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

