package ru.hse.gymvision.ui.camera

import android.util.Log
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import org.koin.androidx.compose.koinViewModel
import ru.hse.gymvision.R
import ru.hse.gymvision.domain.CameraMovement
import ru.hse.gymvision.domain.CameraRotation
import ru.hse.gymvision.domain.CameraZoom
import ru.hse.gymvision.ui.composables.LoadingBlock
import ru.hse.gymvision.ui.composables.MyTitle
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

        is CameraState.OneCamera -> OneCameraState(
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
                viewModel.obtainEvent(CameraEvent.PlayFirstCameraButtonClicked)
            })

        is CameraState.TwoCameras -> TwoCamerasState(
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
                viewModel.obtainEvent(CameraEvent.DeleteSecondCameraButtonClicked)
            },
            onPlay1 = {
                viewModel.obtainEvent(CameraEvent.PlayFirstCameraButtonClicked)
            },
            onPlay2 = {
                viewModel.obtainEvent(CameraEvent.PlaySecondCameraButtonClicked)
            },
            onMakeMainCamera2 = {
                viewModel.obtainEvent(CameraEvent.MakeSecondCameraMainButtonClicked)
            }
        )

        is CameraState.ThreeCameras -> ThreeCamerasState(
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
            onAddCamera = {
                viewModel.obtainEvent(CameraEvent.AddCameraButtonClicked)
            },
            onDeleteCamera2 = {
                viewModel.obtainEvent(CameraEvent.DeleteSecondCameraButtonClicked)
            },
            onDeleteCamera3 = {
                viewModel.obtainEvent(CameraEvent.DeleteThirdCameraButtonClicked)
            },
            onPlay1 = {
                viewModel.obtainEvent(CameraEvent.PlayFirstCameraButtonClicked)
            },
            onPlay2 = {
                viewModel.obtainEvent(CameraEvent.PlaySecondCameraButtonClicked)
            },
            onPlay3 = {
                viewModel.obtainEvent(CameraEvent.PlayThirdCameraButtonClicked)
            },
            onMakeMainCamera2 = {
                viewModel.obtainEvent(CameraEvent.MakeSecondCameraMainButtonClicked)
            },
            onMakeMainCamera3 = {
                viewModel.obtainEvent(CameraEvent.MakeThirdCameraMainButtonClicked)
            }
        )
    }
}

@Composable
fun OneCameraState(
    state: CameraState.OneCamera,
    onRotateCamera: (CameraRotation) -> Unit,
    onMoveCamera: (CameraMovement) -> Unit,
    onZoomCamera: (CameraZoom) -> Unit,
    onAddCamera: () -> Unit,
    onPlay: () -> Unit) {
    val showControls = remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .padding(top = 16.dp, bottom = 0.dp, start = 16.dp, end = 16.dp)
    ) {
        MyTitle(text = "Камера")
        mainPlayerView(
            state.camera1Link,
            state.isPlaying1,
            showControls,
            onError = { error ->
            Log.d("CameraScreen", "Error: $error") },
            onRotateCamera = onRotateCamera,
            onMoveCamera = onMoveCamera,
            onZoomCamera = onZoomCamera,
            onPlay = onPlay
            )
        FloatingActionButton(
            onClick = {
                onAddCamera()
            },
            modifier = Modifier.padding(16.dp).align(Alignment.BottomEnd)
        ) {
            Icon(
                imageVector = ImageVector.vectorResource(id = R.drawable.ic_add_camera),
                contentDescription = "Добавить камеру",
                tint = androidx.compose.ui.graphics.Color.White
            )
        }

    }
}

@Composable
fun TwoCamerasState(
    state: CameraState.TwoCameras,
    onRotateCamera: (CameraRotation) -> Unit,
    onMoveCamera: (CameraMovement) -> Unit,
    onZoomCamera: (CameraZoom) -> Unit,
    onAddCamera: () -> Unit,
    onDeleteCamera2: () -> Unit,
    onMakeMainCamera2: () -> Unit,
    onPlay1: () -> Unit,
    onPlay2: () -> Unit
    ) {
    val showControls1 = remember { mutableStateOf(false) }
    val showControls2 = remember { mutableStateOf(false) }
    Box(
        modifier = Modifier
            .padding(top = 16.dp, bottom = 0.dp, start = 16.dp, end = 16.dp)
    ) {
        MyTitle(text = "Камеры")
        Column {
            Box(
                modifier = Modifier.weight(1f)
            ) {
                mainPlayerView(
                    state.camera1Link,
                    state.isPlaying1,
                    showControls1,
                    onError = { error ->
                        Log.d("CameraScreen", "Error: $error")
                    },
                    onRotateCamera = onRotateCamera,
                    onMoveCamera = onMoveCamera,
                    onZoomCamera = onZoomCamera,
                    onPlay = onPlay1
                )
            }

            Box(
                modifier = Modifier.weight(1f)
            ) {
                secondaryPlayerView(
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
            modifier = Modifier.padding(16.dp).align(Alignment.BottomEnd)
        ) {
            Icon(
                imageVector = ImageVector.vectorResource(id = R.drawable.ic_add_camera),
                contentDescription = "Добавить камеру",
                tint = androidx.compose.ui.graphics.Color.White
            )
        }

    }
}

@Composable
fun ThreeCamerasState(
    state: CameraState.ThreeCameras,
    onRotateCamera: (CameraRotation) -> Unit,
    onMoveCamera: (CameraMovement) -> Unit,
    onZoomCamera: (CameraZoom) -> Unit,
    onAddCamera: () -> Unit,
    onDeleteCamera2: () -> Unit,
    onDeleteCamera3: () -> Unit,
    onMakeMainCamera2: () -> Unit,
    onMakeMainCamera3: () -> Unit,
    onPlay1: () -> Unit,
    onPlay2: () -> Unit,
    onPlay3: () -> Unit
) {
    val showControls1 = remember { mutableStateOf(false) }
    val showControls2 = remember { mutableStateOf(false) }
    val showControls3 = remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .padding(top = 16.dp, bottom = 0.dp, start = 16.dp, end = 16.dp)
    ) {
        MyTitle(text = "Камеры")
        Column {
            Box(
                modifier = Modifier.weight(1f)
            ) {
                mainPlayerView(
                    state.camera1Link,
                    state.isPlaying1,
                    showControls1,
                    onError = { error ->
                        Log.d("CameraScreen", "Error: $error")
                    },
                    onRotateCamera = onRotateCamera,
                    onMoveCamera = onMoveCamera,
                    onZoomCamera = onZoomCamera,
                    onPlay = onPlay1
                )
            }

            Box(
                modifier = Modifier.weight(1f)
            ) {
                Row {
                    Box(
                        modifier = Modifier.weight(1f)
                    ) {
                        secondaryPlayerView(
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
                        secondaryPlayerView(
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
        FloatingActionButton(
            onClick = {
                onAddCamera()
            },
            modifier = Modifier.padding(16.dp).align(Alignment.BottomEnd)
        ) {
            Icon(
                imageVector = ImageVector.vectorResource(id = R.drawable.ic_add_camera),
                contentDescription = "Добавить камеру",
                tint = androidx.compose.ui.graphics.Color.White
            )
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