package ru.hse.gymvision.presentation.ui.screens

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Popup
import androidx.compose.ui.window.PopupProperties
import androidx.lifecycle.viewmodel.compose.viewModel
import ru.alexgladkov.odyssey.compose.extensions.push
import ru.alexgladkov.odyssey.compose.local.LocalRootController
import ru.hse.gymvision.R
import ru.hse.gymvision.domain.model.ClickableCamera
import ru.hse.gymvision.domain.model.ClickableTrainer
import ru.hse.gymvision.presentation.ui.BottomNavScreen
import ru.hse.gymvision.presentation.ui.PreferencesHelper
import ru.hse.gymvision.presentation.ui.composables.LoadingScreen
import ru.hse.gymvision.presentation.ui.composables.MyAlertDialog
import ru.hse.gymvision.presentation.ui.composables.MyBottomAppBar
import ru.hse.gymvision.presentation.ui.composables.MyPopup
import ru.hse.gymvision.presentation.ui.composables.MyTitle

val CAMERA_SIZE = 24.dp

@Composable
fun GymSchemeScreen() {
    val viewModel: GymSchemeViewModel = viewModel()
    val isLoading by viewModel.isLoading.collectAsState()
    val gymScheme by viewModel.gymScheme.collectAsState()
    var imageWidth by remember { mutableStateOf(0.dp) }
    var imageHeight by remember { mutableStateOf(0.dp) }
    val context = LocalContext.current
    val gymId = PreferencesHelper(context).getCurGymId()
    val density = LocalDensity.current

    viewModel.loadGymScheme(gymId)

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        bottomBar = { MyBottomAppBar(BottomNavScreen.GYM_SCHEME) }
    ) { paddingValues ->
        if (isLoading) {
            LoadingScreen()
        } else {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier
                    .padding(paddingValues)
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
                MyTitle(text = "Схема зала")
                val placeholderPainter = painterResource(id = R.drawable.im_placeholder)
                val painter: Painter = remember(gymScheme?.image) {
                    gymScheme?.let {
                        BitmapPainter(it.image.asImageBitmap())
                    } ?: placeholderPainter
                }

                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .fillMaxSize()
                ) {
                    Image(
                        painter = painter,
                        contentDescription = "Gym scheme",
                        modifier = Modifier
                            .wrapContentSize(Alignment.Center)
                            .onGloballyPositioned {
                                val imageSize = it.size
                                imageWidth = with(density) { imageSize.width.toDp() }
                                imageHeight = with(density) { imageSize.height.toDp() }
                            }
                    )
                    Clickables(gymScheme?.clickableTrainers ?: emptyList(),
                        gymScheme?.clickableCameras ?: emptyList(),
                        imageWidth, imageHeight)
                }
            }
        }
    }
}

@Composable
fun Clickables(clickableTrainers: List<ClickableTrainer>, clickableCameras: List<ClickableCamera>, imWidth: Dp, imHeight: Dp) {
    val rootController = LocalRootController.current
    var showDialog by remember { mutableStateOf(false) }
    var showPopup by remember { mutableStateOf(false) }
    var trainerName by remember { mutableStateOf("") }
    var trainerDescription by remember { mutableStateOf("") }
    var selectedTrainerId by remember { mutableIntStateOf(-1) }

    Box(
        modifier = Modifier.size(imWidth, imHeight)
    ) {
        for (clickableTrainer in clickableTrainers) {
            val x = (clickableTrainer.xPercent * imWidth.value).dp
            val y = (clickableTrainer.yPercent * imHeight.value).dp
            val width = (clickableTrainer.widthPercent * imWidth.value).dp
            val height = (clickableTrainer.heightPercent * imHeight.value).dp
            val isSelected = selectedTrainerId == clickableTrainer.id

            Box(
                modifier = Modifier
                    .offset(x, y)
                    .size(width, height)
                    .background(
                        if (isSelected) MaterialTheme.colorScheme.secondary.copy(alpha = 0.5f)
                        else MaterialTheme.colorScheme.primary.copy(alpha = 0.5f)
                    )
                    .clickable {
                        trainerName = clickableTrainer.name
                        trainerDescription = clickableTrainer.description
                        selectedTrainerId = clickableTrainer.id
                        showPopup = true
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
                        // проверить можно ли подключиться к камере
                        // вью модель все дела
                        val isAccessible = false
                        if (isAccessible) {
                            rootController.push("camera") // что-то с айди придумать
                        } else {
                            showDialog = true
                        }
                    }
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_camera),
                    contentDescription = "Camera",
                    tint = MaterialTheme.colorScheme.onPrimary
                )
            }
        }
    }

    if (showPopup) {
        MyPopup(
            trainerName,
            trainerDescription
        ) {
            showPopup = false
            selectedTrainerId = -1
        }
    }

    if (showDialog) {
        MyAlertDialog(
            "Камера недоступна",
            "Камера временно недоступна. Попробуйте позже.",
        ) {
            showDialog = false
        }
    }
}
