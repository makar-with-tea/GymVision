package ru.hse.gymvision.ui.gymscheme

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import org.koin.androidx.compose.koinViewModel
import ru.hse.gymvision.R
import ru.hse.gymvision.domain.model.GymSchemeModel
import ru.hse.gymvision.ui.BitmapHelper
import ru.hse.gymvision.ui.composables.LoadingBlock
import ru.hse.gymvision.ui.composables.MyAlertDialog
import ru.hse.gymvision.ui.composables.MyPopup
import ru.hse.gymvision.ui.composables.MyTitle

val CAMERA_SIZE = 24.dp

@Composable
fun GymSchemeScreen(
    id: Int? = null,
    navigateToCamera: (Int, Int) -> Unit,
    viewModel: GymSchemeViewModel = koinViewModel(),
    navigateToGymList: () -> Unit
) {
    val state = viewModel.state.collectAsState()
    val action = viewModel.action.collectAsState()

    when (action.value) {
        is GymSchemeAction.NavigateToCamera -> {
            navigateToCamera(
                (action.value as GymSchemeAction.NavigateToCamera).gymId,
                (action.value as GymSchemeAction.NavigateToCamera).cameraId
            )
            viewModel.obtainEvent(GymSchemeEvent.Clear)
        }
        null -> {}
    }

    when (state.value) {
        is GymSchemeState.Main -> {
            MainState(
                state.value as GymSchemeState.Main,
                onCameraClicked = { cameraId ->
                    viewModel.obtainEvent(GymSchemeEvent.CameraClicked(id, cameraId))
                },
                onTrainerClicked = { name, description, trainerId ->
                    viewModel.obtainEvent(GymSchemeEvent.TrainerClicked(name, description, trainerId))
                },
                onHidePopup = {
                    viewModel.obtainEvent(GymSchemeEvent.HidePopup)
                },
                onHideDialog = {
                    viewModel.obtainEvent(GymSchemeEvent.HideDialog)
                }
            )
        }
        is GymSchemeState.Loading -> {
            LoadingState()
        }
        is GymSchemeState.Idle -> {
            IdleState()
            viewModel.obtainEvent(GymSchemeEvent.LoadGymScheme(id))
        }
        is GymSchemeState.Error -> {
            ErrorState(
                errorText = (state.value as GymSchemeState.Error).error.toText(),
                onConfirm = {
                    viewModel.obtainEvent(GymSchemeEvent.LoadGymScheme(id))
                },
                onDismiss = {
                    viewModel.obtainEvent(GymSchemeEvent.Clear)
                    navigateToGymList()
                }
            )
        }
    }
}

@Composable
fun MainState(
    state: GymSchemeState.Main,
    onCameraClicked: (Int) -> Unit,
    onTrainerClicked: (String, String, Int) -> Unit,
    onHidePopup: () -> Unit,
    onHideDialog: () -> Unit
) {
    Log.d("GymSchemeScreenMainState", "MainState: $state")
    val gymScheme: MutableState<GymSchemeModel?> = remember { mutableStateOf(state.gymScheme) }

    var imWidth by remember { mutableStateOf(0.dp) }
    var imHeight by remember { mutableStateOf(0.dp) }
    val density = LocalDensity.current
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween,
        modifier = Modifier
            .fillMaxSize()
            .padding(top = 16.dp, bottom = 0.dp, start = 16.dp, end = 16.dp)
    ) {
        MyTitle(text = stringResource(R.string.gym_scheme_title))
        val placeholderPainter = painterResource(id = R.drawable.im_placeholder)
        val imageBitmap = BitmapHelper.byteArrayToBitmap(gymScheme.value?.image)
        val painter: Painter = remember(imageBitmap) {
            imageBitmap?.let {
                BitmapPainter(imageBitmap.asImageBitmap())
            } ?: placeholderPainter
        }

        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .fillMaxSize()
        ) {
            Image(
                painter = painter,
                contentDescription = stringResource(R.string.gym_scheme_image_description),
                modifier = Modifier
                    .wrapContentSize(Alignment.Center)
                    .onGloballyPositioned {
                        val imageSize = it.size
                        imWidth = with(density) { imageSize.width.toDp() }
                        imHeight = with(density) { imageSize.height.toDp() }
                    }
            )

            val clickableTrainers = gymScheme.value?.clickableTrainerModels ?: emptyList()
            val clickableCameras = gymScheme.value?.clickableCameraModels ?: emptyList()

            Box(
                modifier = Modifier.size(imWidth, imHeight)
            ) {
                for (clickableTrainer in clickableTrainers) {
                    val x = (clickableTrainer.xPercent * imWidth.value).dp
                    val y = (clickableTrainer.yPercent * imHeight.value).dp
                    val width = (clickableTrainer.widthPercent * imWidth.value).dp
                    val height = (clickableTrainer.heightPercent * imHeight.value).dp
                    val isSelected = state.selectedTrainerId == clickableTrainer.id

                    Box(
                        modifier = Modifier
                            .offset(x, y)
                            .size(width, height)
                            .background(
                                if (isSelected) MaterialTheme.colorScheme.secondary.copy(alpha = 0.5f)
                                else MaterialTheme.colorScheme.primary.copy(alpha = 0.5f)
                            )
                            .clickable {
                                Log.d("GymSchemeScreen", "trainer clicked: x = $x, y = $y")
                                onTrainerClicked(
                                    clickableTrainer.name,
                                    clickableTrainer.description,
                                    clickableTrainer.id
                                )
                            }
                    )
                }

                for (clickableCamera in clickableCameras) {
                    val x = (clickableCamera.xPercent * imWidth.value).dp
                    val y = (clickableCamera.yPercent * imHeight.value).dp
                    Log.d("GymSchemeScreen", "cam: x = $x, y = $y")
                    Box(
                        modifier = Modifier
                            .offset(x, y)
                            .size(CAMERA_SIZE)
                            .background(Color.Transparent)
                            .clickable {
                                onCameraClicked(clickableCamera.id)
                            }
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_camera),
                            contentDescription = stringResource(R.string.camera_icon_description),
                            tint = MaterialTheme.colorScheme.onPrimary
                        )
                    }
                }
            }

            if (state.showPopup) {
                MyPopup(
                    state.trainerName,
                    state.trainerDescription
                ) {
                    onHidePopup()
                }
            }

            if (state.showDialog) {
                MyAlertDialog(
                    title = stringResource(R.string.camera_unavailable_title),
                    text = stringResource(R.string.camera_unavailable_message),
                    onConfirm = { onHideDialog() }
                )
            }
        }
        if (state.isLoading) {
            LoadingBlock()
        }
    }
}

@Composable
private fun GymSchemeState.GymSchemeError.toText() = when (this) {
    GymSchemeState.GymSchemeError.GYM_NOT_FOUND -> stringResource(R.string.gym_not_found_error)
    GymSchemeState.GymSchemeError.NETWORK_ERROR -> stringResource(R.string.network_error_short)
    GymSchemeState.GymSchemeError.IDLE -> stringResource(R.string.unknown_error)
}

@Composable
fun LoadingState() {
    LoadingBlock()
}

@Composable
fun IdleState() {
    LoadingBlock()
}

@Composable
fun ErrorState(
    errorText: String?,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.5f))
            .clickable { },
        contentAlignment = Alignment.Center
    ) {
        MyAlertDialog(
            title = stringResource(R.string.loading_error),
            text = errorText ?: stringResource(R.string.unknown_error),
            onConfirm = { onConfirm() },
            onDismissRequest = { onDismiss() },
            confirmButtonText = stringResource(R.string.reload_button_text),
        )
    }
}
