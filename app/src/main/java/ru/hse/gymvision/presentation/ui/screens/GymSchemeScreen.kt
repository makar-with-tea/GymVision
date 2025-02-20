package ru.hse.gymvision.presentation.ui.screens

import android.widget.Toast
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
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import ru.hse.gymvision.R
import ru.hse.gymvision.domain.model.ClickableCoord
import ru.hse.gymvision.presentation.ui.BottomNavScreen
import ru.hse.gymvision.presentation.ui.PreferencesHelper
import ru.hse.gymvision.presentation.ui.composables.LoadingScreen
import ru.hse.gymvision.presentation.ui.composables.MyBottomAppBar
import ru.hse.gymvision.presentation.ui.composables.MyTitle


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
                    Clickables(gymScheme?.clickableCoords ?: emptyList(), imageWidth, imageHeight)
                }
            }
        }
    }
}

@Composable
fun Clickables(clickableCoords: List<ClickableCoord>, imWidth: Dp, imHeight: Dp) {
    val context = LocalContext.current
    Box(
        modifier = Modifier.size(imWidth, imHeight)
    ) {
        for (clickableCoord in clickableCoords) {
            val x = (clickableCoord.xPercent * imWidth.value).dp
            val y = (clickableCoord.yPercent * imHeight.value).dp
            val width = (clickableCoord.widthPercent * imWidth.value).dp
            val height = (clickableCoord.heightPercent * imHeight.value).dp

            Box(
                modifier = Modifier
                    .offset(x, y)
                    .size(width, height)
                    .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.5f))
                    .clickable {
                        Toast
                            .makeText(
                                context,
                                clickableCoord.description,
                                Toast.LENGTH_SHORT
                            )
                            .show()
                    }
            )
        }
    }
}