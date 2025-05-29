package ru.hse.gymvision.ui.gymlist

import android.util.Log
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.koin.androidx.compose.koinViewModel
import ru.hse.gymvision.R
import ru.hse.gymvision.ui.BitmapHelper
import ru.hse.gymvision.ui.composables.LoadingBlock
import ru.hse.gymvision.ui.composables.MyAlertDialog
import ru.hse.gymvision.ui.composables.MyTitle
import ru.hse.gymvision.ui.composables.SkeletonView

@Composable
fun GymListScreen(
    navigateToGymScheme: (Int) -> Unit,
    viewModel: GymListViewModel = koinViewModel()
) {
    val state = viewModel.state.collectAsState()
    val action = viewModel.action.collectAsState()

    val isActive = remember { mutableStateOf(true) }

    when (action.value) {
        is GymListAction.NavigateToGym -> {
            isActive.value = false
            navigateToGymScheme((action.value as GymListAction.NavigateToGym).gymId)
            viewModel.obtainEvent(GymListEvent.Clear)
        }

        null -> {}
    }

    when (state.value) {
        is GymListState.Main -> {
            MainState(
                state = state.value as GymListState.Main,
                onGymClicked = { gymId ->
                    Log.d("GymListScreen", "Gym clicked: $gymId")
                    viewModel.obtainEvent(GymListEvent.SelectGym(gymId))
                }
            )
        }

        is GymListState.Loading -> {
            LoadingState()
        }

        is GymListState.Idle -> {
            IdleState()
            if (isActive.value) {
                viewModel.obtainEvent(GymListEvent.GetGymList)
            }
        }

        is GymListState.Error -> {
            ErrorState(
                errorText = stringResource(R.string.network_error_long),
                onDismiss = {
                    viewModel.obtainEvent(GymListEvent.GetGymList)
                }
            )
        }
    }
}

@Composable
fun IdleState() {
}

@Composable
fun LoadingState() {
    val animatable = remember { Animatable(0f) }

    LaunchedEffect(Unit) {
        while (true) {
            animatable.animateTo(
                targetValue = 1f,
                animationSpec = tween(1200, 0, LinearEasing)
            )
            animatable.snapTo(0f)
        }
    }

    val tan = remember { 0.26795f } // tan(15 degrees)
    val screenHeight = with (LocalDensity.current) {
        LocalConfiguration.current.screenHeightDp.dp.toPx()
    }
    val globalY = screenHeight * animatable.value
    val globalX = tan * globalY

    val endY = screenHeight + globalY
    val endX = tan * endY

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top,
        modifier = Modifier
            .fillMaxSize()
            .padding(top = 16.dp, bottom = 0.dp, start = 16.dp, end = 16.dp)
    ) {
        repeat(5) {
            SkeletonView(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(80.dp)
                    .padding(vertical = 8.dp)
                    .clip(shape = RoundedCornerShape(14.dp)),
                globalX = globalX,
                globalY = globalY,
                endX = endX,
                endY = endY,
            )
        }
    }
}

@Composable
fun ErrorState(errorText: String?, onDismiss: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.5f))
            .clickable { },
        contentAlignment = Alignment.Center
    ) {
        MyAlertDialog(
            stringResource(R.string.loading_error),
            errorText ?: stringResource(R.string.unknown_error),
            onDismiss,
            stringResource(R.string.reload_button_text),
        )
    }
}

@Composable
fun MainState(
    state: GymListState.Main,
    onGymClicked: (Int) -> Unit
) {
    LazyColumn(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top,
        modifier = Modifier
            .fillMaxSize()
            .padding(top = 16.dp, bottom = 0.dp, start = 16.dp, end = 16.dp)
    ) {
        itemsIndexed(state.gyms) { _, gym ->

            OutlinedCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(80.dp)
                    .padding(vertical = 8.dp)
                    .clickable(onClick = {
                        onGymClicked(gym.id)
                    })
            ) {
                Row {
                    val placeholderPainter = painterResource(id = R.drawable.im_placeholder)
                    val imageBitmap = BitmapHelper.byteArrayToBitmap(gym.image)
                    val painter: Painter = remember(imageBitmap) {
                        imageBitmap?.let {
                            BitmapPainter(it.asImageBitmap())
                        } ?: placeholderPainter
                    }
                    Image(
                        painter = painter,
                        contentDescription = stringResource(R.string.gym_avatar_description),
                        modifier = Modifier
                            .size(64.dp)
                            .padding(8.dp)
                    )
                    Column(
                        modifier = Modifier
                            .padding(8.dp),
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = gym.name,
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                        )

                        Text(
                            text = gym.address,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                        )
                    }
                }
            }
        }
    }
}
