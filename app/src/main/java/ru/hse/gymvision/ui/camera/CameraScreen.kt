package ru.hse.gymvision.ui.camera

import android.util.Log
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import org.koin.androidx.compose.koinViewModel
import ru.hse.gymvision.R
import ru.hse.gymvision.ui.composables.LoadingBlock
import ru.hse.gymvision.ui.composables.MyTitle
import ru.hse.gymvision.ui.composables.myPlayerView
import java.net.URLDecoder
import java.nio.charset.StandardCharsets

@Composable
fun CameraScreen(
    serverUrl: String,
    newCameraId: Int? = null,
    navigateToGymScheme: () -> Unit,
    viewModel: CameraViewModel = koinViewModel()
) {
    val state = viewModel.state.collectAsState()
    val action = viewModel.action.collectAsState()

    val decodedUrl = URLDecoder.decode(serverUrl, StandardCharsets.UTF_8.toString())

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
                CameraEvent.LoadCameraIds(newCameraId)
            )
        }
        CameraState.Loading -> LoadingState()
        is CameraState.OneCamera -> OneCameraState(state.value as CameraState.OneCamera, decodedUrl, navigateToGymScheme)
        is CameraState.TwoCameras -> TwoCamerasState()
        is CameraState.ThreeCameras -> ThreeCamerasState()
    }
}


@Composable
fun OneCameraState(
    state: CameraState.OneCamera,
    serverUrl: String,
//    onRotateCamera: (Int) -> Unit = {},
//    onMoveCamera: (Int) -> Unit = {},
//    onZoomCamera: (Int) -> Unit = {},
    navigateToGymScheme: () -> Unit) {
    val showControls = remember { mutableStateOf(false) }

    val videoUrl1 = serverUrl //todo: "${state.serverUrl}/${state.camera1Id}"

    Column(
        modifier = Modifier
            .padding(top = 16.dp, bottom = 0.dp, start = 16.dp, end = 16.dp)
    ) {
        MyTitle(text = "Камера")
        myPlayerView(
            videoUrl1,
            state.isPlaying1,
            showControls,
            onError = { error ->
            Log.d("CameraScreen", "Error: $error") },
            onRotateCamera = { rotation ->
//                todo: onRotateCamera(rotation)
                Log.d("CameraScreen", "Rotation: $rotation")
            },
            onMoveCamera = { movement ->
//                todo: onMoveCamera(movement)
                Log.d("CameraScreen", "Movement: $movement")
            },
            onZoomCamera = { zoom ->
//               todo: onZoomCamera(zoom)
                Log.d("CameraScreen", "Zoom: $zoom")
            })
        // todo: почему кнопку не видно?
        FloatingActionButton(
            onClick = {
                navigateToGymScheme()
            },
            modifier = Modifier.padding(16.dp)
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
fun TwoCamerasState() {
    Text(
        text = "Two cameras!!",
        modifier = Modifier.padding(16.dp)
    )
}

@Composable
fun ThreeCamerasState() {
    Text(
        text = "Three cameras!!",
        modifier = Modifier.padding(16.dp)
    )
}

@Composable
fun LoadingState() {
    LoadingBlock()
}

@Composable
fun IdleState() {
    LoadingBlock()
}