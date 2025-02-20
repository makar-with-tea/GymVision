package ru.hse.gymvision.presentation.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import ru.alexgladkov.odyssey.compose.extensions.push
import ru.alexgladkov.odyssey.compose.local.LocalRootController
import ru.hse.gymvision.R
import ru.hse.gymvision.domain.model.GymModel
import ru.hse.gymvision.presentation.ui.composables.MyBottomAppBar
import ru.hse.gymvision.presentation.ui.composables.MyTitle

@Composable
fun GymListScreen() {
    val viewModel = GymListViewModel()
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        bottomBar = { MyBottomAppBar() }
    ) { paddingValues ->
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top,
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
                .padding(16.dp)
        ) {
            MyTitle(text = "Доступные залы")
            LazyColumn {
                itemsIndexed(viewModel.getGymList()) { index, gym ->
                    GymCard(gym)
                }
            }
        }
    }
}

@Composable
fun GymCard(gym: GymModel) {
    val rootController = LocalRootController.current
    OutlinedCard(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .clickable(onClick = {
                rootController.push("gymScheme")
            })
    ) {
        Row {
            val placeholderPainter = painterResource(id = R.drawable.im_placeholder)
            val painter: Painter = remember(gym.image) {
                gym.image?.let {
                    BitmapPainter(it.asImageBitmap())
                } ?: placeholderPainter
            }
            Image(
                painter = painter,
                contentDescription = "Cover",
                modifier = Modifier
                    .size(64.dp)
                    .padding(8.dp)
            )
            Column(
                modifier = Modifier
                    .padding(8.dp),
                verticalArrangement = Arrangement.Center
            ) {
                Text(text = gym.name)
                Text(text = gym.address)
            }
        }
    }
}
