package ru.hse.gymvision.ui.camera

import android.content.res.Configuration
import android.util.Log
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import org.koin.androidx.compose.koinViewModel
import org.videolan.libvlc.MediaPlayer
import ru.hse.gymvision.R
import ru.hse.gymvision.domain.CameraMovement
import ru.hse.gymvision.domain.CameraRotation
import ru.hse.gymvision.domain.CameraZoom
import ru.hse.gymvision.ui.composables.LoadingBlock
import ru.hse.gymvision.ui.composables.mainPlayerView
import ru.hse.gymvision.ui.composables.secondaryPlayerView

@Composable
fun CameraScreen(
    gymId: Int = -1,
    newCameraId: Int? = null,
    navigateToGymScheme: () -> Unit,
    viewModel: CameraViewModel = koinViewModel()
) {
    val state = viewModel.state.collectAsState()
    val action = viewModel.action.collectAsState()
    val isPortrait = LocalConfiguration.current.orientation == Configuration.ORIENTATION_PORTRAIT

    when (action.value) {
        is CameraAction.NavigateToGymScheme -> {
            navigateToGymScheme()
            viewModel.obtainEvent(CameraEvent.Clear)
        }

        null -> {}
    }

    when (state.value) {
        CameraState.Idle -> {
            IdleState()
            viewModel.obtainEvent(
                CameraEvent.InitCameras(newCameraId, gymId)
            )
        }

        CameraState.Loading -> LoadingState()

        is CameraState.OneCamera -> {
            OneCameraState(
                state.value as CameraState.OneCamera,
                onRotateCamera = { rotation ->
                    viewModel.obtainEvent(CameraEvent.RotateCameraButtonClicked(rotation))
                },
                onMoveCamera = { movement ->
                    viewModel.obtainEvent(CameraEvent.MoveCameraButtonClicked(movement))
                },
                onZoomCamera = { zoom ->
                    viewModel.obtainEvent(CameraEvent.ZoomCameraButtonClicked(zoom))
                },
                onAddCamera = {
                    viewModel.obtainEvent(CameraEvent.AddCameraButtonClicked)
                },
                onPlay = {
                    viewModel.obtainEvent(CameraEvent.PlayCameraButtonClicked(1))
                },
                onChangeAIState = { isAiEnabled ->
                    viewModel.obtainEvent(CameraEvent.ChangeAiState(isAiEnabled))
                },)
        }

        is CameraState.TwoCameras -> {
            if (isPortrait) {
                TwoCamerasStatePortrait(
                    state.value as CameraState.TwoCameras,
                    onRotateCamera = { rotation ->
                        viewModel.obtainEvent(CameraEvent.RotateCameraButtonClicked(rotation))
                    },
                    onMoveCamera = { movement ->
                        viewModel.obtainEvent(CameraEvent.MoveCameraButtonClicked(movement))
                    },
                    onZoomCamera = { zoom ->
                        viewModel.obtainEvent(CameraEvent.ZoomCameraButtonClicked(zoom))
                    },
                    onAddCamera = {
                        viewModel.obtainEvent(CameraEvent.AddCameraButtonClicked)
                    },
                    onDeleteCamera2 = {
                        viewModel.obtainEvent(CameraEvent.DeleteCameraButtonClicked(2))
                    },
                    onPlay1 = {
                        viewModel.obtainEvent(CameraEvent.PlayCameraButtonClicked(1))
                    },
                    onPlay2 = {
                        viewModel.obtainEvent(CameraEvent.PlayCameraButtonClicked(2))
                    },
                    onMakeMainCamera2 = {
                        viewModel.obtainEvent(CameraEvent.MakeCameraMainButtonClicked(2))
                    },
                    onChangeAIState = { isAiEnabled ->
                        viewModel.obtainEvent(CameraEvent.ChangeAiState(isAiEnabled))
                    },
                )
            } else {
                TwoCamerasStateLandscape(
                    state.value as CameraState.TwoCameras,
                    onRotateCamera = { rotation ->
                        viewModel.obtainEvent(CameraEvent.RotateCameraButtonClicked(rotation))
                    },
                    onMoveCamera = { movement ->
                        viewModel.obtainEvent(CameraEvent.MoveCameraButtonClicked(movement))
                    },
                    onZoomCamera = { zoom ->
                        viewModel.obtainEvent(CameraEvent.ZoomCameraButtonClicked(zoom))
                    },
                    onAddCamera = {
                        viewModel.obtainEvent(CameraEvent.AddCameraButtonClicked)
                    },
                    onDeleteCamera2 = {
                        viewModel.obtainEvent(CameraEvent.DeleteCameraButtonClicked(2))
                    },
                    onPlay1 = {
                        viewModel.obtainEvent(CameraEvent.PlayCameraButtonClicked(1))
                    },
                    onPlay2 = {
                        viewModel.obtainEvent(CameraEvent.PlayCameraButtonClicked(2))
                    },
                    onMakeMainCamera2 = {
                        viewModel.obtainEvent(CameraEvent.MakeCameraMainButtonClicked(2))
                    },
                    onChangeAIState = { isAiEnabled ->
                        viewModel.obtainEvent(CameraEvent.ChangeAiState(isAiEnabled))
                    },
                )
            }
        }

        is CameraState.ThreeCameras -> {
            if (isPortrait) {
                ThreeCamerasStatePortrait(
                    state.value as CameraState.ThreeCameras,
                    onRotateCamera = { rotation ->
                        viewModel.obtainEvent(CameraEvent.RotateCameraButtonClicked(rotation))
                    },
                    onMoveCamera = { movement ->
                        viewModel.obtainEvent(CameraEvent.MoveCameraButtonClicked(movement))
                    },
                    onZoomCamera = { zoom ->
                        viewModel.obtainEvent(CameraEvent.ZoomCameraButtonClicked(zoom))
                    },
                    onDeleteCamera2 = {
                        viewModel.obtainEvent(CameraEvent.DeleteCameraButtonClicked(2))
                    },
                    onDeleteCamera3 = {
                        viewModel.obtainEvent(CameraEvent.DeleteCameraButtonClicked(3))
                    },
                    onPlay1 = {
                        viewModel.obtainEvent(CameraEvent.PlayCameraButtonClicked(1))
                    },
                    onPlay2 = {
                        viewModel.obtainEvent(CameraEvent.PlayCameraButtonClicked(2))
                    },
                    onPlay3 = {
                        viewModel.obtainEvent(CameraEvent.PlayCameraButtonClicked(3))
                    },
                    onMakeMainCamera2 = {
                        viewModel.obtainEvent(CameraEvent.MakeCameraMainButtonClicked(2))
                    },
                    onMakeMainCamera3 = {
                        viewModel.obtainEvent(CameraEvent.MakeCameraMainButtonClicked(3))
                    },
                    onChangeAIState = { isAiEnabled ->
                        viewModel.obtainEvent(CameraEvent.ChangeAiState(isAiEnabled))
                    },
                )
            } else {
                ThreeCamerasStateLandscape(
                    state.value as CameraState.ThreeCameras,
                    onRotateCamera = { rotation ->
                        viewModel.obtainEvent(CameraEvent.RotateCameraButtonClicked(rotation))
                    },
                    onMoveCamera = { movement ->
                        viewModel.obtainEvent(CameraEvent.MoveCameraButtonClicked(movement))
                    },
                    onZoomCamera = { zoom ->
                        viewModel.obtainEvent(CameraEvent.ZoomCameraButtonClicked(zoom))
                    },
                    onDeleteCamera2 = {
                        viewModel.obtainEvent(CameraEvent.DeleteCameraButtonClicked(2))
                    },
                    onDeleteCamera3 = {
                        viewModel.obtainEvent(CameraEvent.DeleteCameraButtonClicked(3))
                    },
                    onPlay1 = {
                        viewModel.obtainEvent(CameraEvent.PlayCameraButtonClicked(1))
                    },
                    onPlay2 = {
                        viewModel.obtainEvent(CameraEvent.PlayCameraButtonClicked(2))
                    },
                    onPlay3 = {
                        viewModel.obtainEvent(CameraEvent.PlayCameraButtonClicked(3))
                    },
                    onMakeMainCamera2 = {
                        viewModel.obtainEvent(CameraEvent.MakeCameraMainButtonClicked(2))
                    },
                    onMakeMainCamera3 = {
                        viewModel.obtainEvent(CameraEvent.MakeCameraMainButtonClicked(3))
                    },
                    onChangeAIState = { isAiEnabled ->
                        viewModel.obtainEvent(CameraEvent.ChangeAiState(isAiEnabled))
                    }
                )
            }
        }
    }
}

@Composable
fun OneCameraState(
    state: CameraState.OneCamera,
    onRotateCamera: (CameraRotation) -> Unit,
    onMoveCamera: (CameraMovement) -> Unit,
    onZoomCamera: (CameraZoom) -> Unit,
    onAddCamera: () -> Unit,
    onPlay: () -> Unit,
    onChangeAIState: (Boolean) -> Unit,
) {
    val showControls = remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .padding(top = 16.dp, bottom = 0.dp, start = 16.dp, end = 16.dp)
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            val player = mainPlayerView(
                state.camera1Link,
                state.isPlaying1,
                state.isAiEnabled,
                showControls,
                onError = { error ->
                    Log.d("CameraScreen", "Error: $error")
                },
                onRotateCamera = onRotateCamera,
                onMoveCamera = onMoveCamera,
                onZoomCamera = onZoomCamera,
                onPlay = onPlay,
                onChangeAi = onChangeAIState
            )
        }
        FloatingActionButton(
            onClick = {
                onAddCamera()
            },
            modifier = Modifier
                .padding(16.dp)
                .align(Alignment.BottomEnd)
        ) {
            Icon(
                imageVector = ImageVector.vectorResource(id = R.drawable.ic_add_camera),
                contentDescription = stringResource(R.string.add_camera_button_description),
                tint = MaterialTheme.colorScheme.onPrimaryContainer
            )
        }
    }
}

@Composable
fun TwoCamerasStatePortrait(
    state: CameraState.TwoCameras,
    onRotateCamera: (CameraRotation) -> Unit,
    onMoveCamera: (CameraMovement) -> Unit,
    onZoomCamera: (CameraZoom) -> Unit,
    onAddCamera: () -> Unit,
    onDeleteCamera2: () -> Unit,
    onMakeMainCamera2: () -> Unit,
    onPlay1: () -> Unit,
    onPlay2: () -> Unit,
    onChangeAIState: (Boolean) -> Unit,
) {
    val showControls1 = remember { mutableStateOf(false) }
    val showControls2 = remember { mutableStateOf(false) }
    lateinit var player1: MediaPlayer
    lateinit var player2: MediaPlayer

    Box(
        modifier = Modifier
            .padding(top = 16.dp, bottom = 0.dp, start = 16.dp, end = 16.dp)
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier.weight(1f)
            ) {
                player1 = mainPlayerView(
                    state.camera1Link,
                    state.isPlaying1,
                    state.isAiEnabled,
                    showControls1,
                    onError = { error ->
                        Log.d("CameraScreen", "Error: $error")
                    },
                    onRotateCamera = onRotateCamera,
                    onMoveCamera = onMoveCamera,
                    onZoomCamera = onZoomCamera,
                    onPlay = onPlay1,
                    onChangeAi = onChangeAIState
                )
            }

            Box(
                modifier = Modifier.weight(1f)
            ) {
                player2 = secondaryPlayerView(
                    state.camera2Link,
                    state.isPlaying2,
                    showControls2,
                    onError = { error ->
                        Log.d("CameraScreen", "Error: $error")
                    },
                    onPlay = onPlay2,
                    onMakeMainCamera = onMakeMainCamera2,
                    onDeleteCamera = onDeleteCamera2
                )
            }
        }
        FloatingActionButton(
            onClick = {
                onAddCamera()
            },
            modifier = Modifier
                .padding(16.dp)
                .align(Alignment.BottomEnd)
        ) {
            Icon(
                imageVector = ImageVector.vectorResource(id = R.drawable.ic_add_camera),
                contentDescription = stringResource(R.string.add_camera_button_description),
                tint = MaterialTheme.colorScheme.onPrimaryContainer
            )
        }

    }
}

@Composable
fun ThreeCamerasStatePortrait(
    state: CameraState.ThreeCameras,
    onRotateCamera: (CameraRotation) -> Unit,
    onMoveCamera: (CameraMovement) -> Unit,
    onZoomCamera: (CameraZoom) -> Unit,
    onDeleteCamera2: () -> Unit,
    onDeleteCamera3: () -> Unit,
    onMakeMainCamera2: () -> Unit,
    onMakeMainCamera3: () -> Unit,
    onPlay1: () -> Unit,
    onPlay2: () -> Unit,
    onPlay3: () -> Unit,
    onChangeAIState: (Boolean) -> Unit,
) {
    val showControls1 = remember { mutableStateOf(false) }
    val showControls2 = remember { mutableStateOf(false) }
    val showControls3 = remember { mutableStateOf(false) }

    lateinit var player1: MediaPlayer
    lateinit var player2: MediaPlayer
    lateinit var player3: MediaPlayer

    Box(
        modifier = Modifier
            .padding(top = 16.dp, bottom = 0.dp, start = 16.dp, end = 16.dp)
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier.weight(1f)
            ) {
                player1 = mainPlayerView(
                    state.camera1Link,
                    state.isPlaying1,
                    state.isAiEnabled,
                    showControls1,
                    onError = { error ->
                        Log.d("CameraScreen", "Error: $error")
                    },
                    onRotateCamera = onRotateCamera,
                    onMoveCamera = onMoveCamera,
                    onZoomCamera = onZoomCamera,
                    onPlay = onPlay1,
                    onChangeAi = onChangeAIState
                )
            }

            Box(
                modifier = Modifier.weight(1f)
            ) {
                Row {
                    Box(
                        modifier = Modifier.weight(1f)
                    ) {
                        player2 = secondaryPlayerView(
                            state.camera2Link,
                            state.isPlaying2,
                            showControls2,
                            onError = { error ->
                                Log.d("CameraScreen", "Error: $error")
                            },
                            onPlay = onPlay2,
                            onMakeMainCamera = onMakeMainCamera2,
                            onDeleteCamera = onDeleteCamera2
                        )
                    }
                    Box(
                        modifier = Modifier.weight(1f)
                    ) {
                        player3 = secondaryPlayerView(
                            state.camera3Link,
                            state.isPlaying3,
                            showControls3,
                            onError = { error ->
                                Log.d("CameraScreen", "Error: $error")
                            },
                            onPlay = onPlay3,
                            onMakeMainCamera = onMakeMainCamera3,
                            onDeleteCamera = onDeleteCamera3
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun TwoCamerasStateLandscape(
    state: CameraState.TwoCameras,
    onRotateCamera: (CameraRotation) -> Unit,
    onMoveCamera: (CameraMovement) -> Unit,
    onZoomCamera: (CameraZoom) -> Unit,
    onAddCamera: () -> Unit,
    onDeleteCamera2: () -> Unit,
    onMakeMainCamera2: () -> Unit,
    onPlay1: () -> Unit,
    onPlay2: () -> Unit,
    onChangeAIState: (Boolean) -> Unit,
) {
    val showControls1 = remember { mutableStateOf(false) }
    val showControls2 = remember { mutableStateOf(false) }

    lateinit var player1: MediaPlayer
    lateinit var player2: MediaPlayer

    Box(
        modifier = Modifier
            .padding(top = 16.dp, bottom = 0.dp, start = 16.dp, end = 16.dp)
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row {
                Box(
                    modifier = Modifier.weight(1f)
                ) {
                    player1 = mainPlayerView(
                        state.camera1Link,
                        state.isPlaying1,
                        state.isAiEnabled,
                        showControls1,
                        onError = { error ->
                            Log.d("CameraScreen", "Error: $error")
                        },
                        onRotateCamera = onRotateCamera,
                        onMoveCamera = onMoveCamera,
                        onZoomCamera = onZoomCamera,
                        onPlay = onPlay1,
                        onChangeAi = onChangeAIState
                    )
                }

                Box(
                    modifier = Modifier.weight(1f)
                ) {
                    player2 = secondaryPlayerView(
                        state.camera2Link,
                        state.isPlaying2,
                        showControls2,
                        onError = { error ->
                            Log.d("CameraScreen", "Error: $error")
                        },
                        onPlay = onPlay2,
                        onMakeMainCamera = onMakeMainCamera2,
                        onDeleteCamera = onDeleteCamera2
                    )
                }
            }
        }
        FloatingActionButton(
            onClick = {
                onAddCamera()
            },
            modifier = Modifier
                .padding(16.dp)
                .align(Alignment.BottomEnd)
        ) {
            Icon(
                imageVector = ImageVector.vectorResource(id = R.drawable.ic_add_camera),
                contentDescription = stringResource(R.string.add_camera_button_description),
                tint = MaterialTheme.colorScheme.onPrimaryContainer
            )
        }

    }
}

@Composable
fun ThreeCamerasStateLandscape(
    state: CameraState.ThreeCameras,
    onRotateCamera: (CameraRotation) -> Unit,
    onMoveCamera: (CameraMovement) -> Unit,
    onZoomCamera: (CameraZoom) -> Unit,
    onDeleteCamera2: () -> Unit,
    onDeleteCamera3: () -> Unit,
    onMakeMainCamera2: () -> Unit,
    onMakeMainCamera3: () -> Unit,
    onPlay1: () -> Unit,
    onPlay2: () -> Unit,
    onPlay3: () -> Unit,
    onChangeAIState: (Boolean) -> Unit,
) {
    val showControls1 = remember { mutableStateOf(false) }
    val showControls2 = remember { mutableStateOf(false) }
    val showControls3 = remember { mutableStateOf(false) }

    lateinit var player1: MediaPlayer
    lateinit var player2: MediaPlayer
    lateinit var player3: MediaPlayer

    Box(
        modifier = Modifier
            .padding(top = 16.dp, bottom = 0.dp, start = 16.dp, end = 16.dp)
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row {
                Box(
                    modifier = Modifier.weight(1f)
                ) {
                    player1 = mainPlayerView(
                        state.camera1Link,
                        state.isPlaying1,
                        state.isAiEnabled,
                        showControls1,
                        onError = { error ->
                            Log.d("CameraScreen", "Error: $error")
                        },
                        onRotateCamera = onRotateCamera,
                        onMoveCamera = onMoveCamera,
                        onZoomCamera = onZoomCamera,
                        onPlay = onPlay1,
                        onChangeAi = onChangeAIState
                    )
                }

                Box(
                    modifier = Modifier.weight(1f)
                ) {
                    Row {
                        Column(
                            modifier = Modifier.weight(1f)
                        ) {
                            player2 = secondaryPlayerView(
                                state.camera2Link,
                                state.isPlaying2,
                                showControls2,
                                onError = { error ->
                                    Log.d("CameraScreen", "Error: $error")
                                },
                                onPlay = onPlay2,
                                onMakeMainCamera = onMakeMainCamera2,
                                onDeleteCamera = onDeleteCamera2
                            )
                        }
                        Box(
                            modifier = Modifier.weight(1f)
                        ) {
                            player3 = secondaryPlayerView(
                                state.camera3Link,
                                state.isPlaying3,
                                showControls3,
                                onError = { error ->
                                    Log.d("CameraScreen", "Error: $error")
                                },
                                onPlay = onPlay3,
                                onMakeMainCamera = onMakeMainCamera3,
                                onDeleteCamera = onDeleteCamera3
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun LoadingState() {
    LoadingBlock()
}

@Composable
fun IdleState() {
    LoadingBlock()
}