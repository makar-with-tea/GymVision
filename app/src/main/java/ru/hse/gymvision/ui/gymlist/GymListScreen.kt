package ru.hse.gymvision.ui.gymlist

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ru.hse.gymvision.R
import ru.hse.gymvision.domain.model.GymInfoModel
import ru.hse.gymvision.ui.BitmapHelper
import ru.hse.gymvision.ui.PreferencesHelper
import ru.hse.gymvision.ui.composables.MyTitle

@Composable
fun GymListScreen(
    navigateToGymScheme: () -> Unit,
) {
    val viewModel = GymListViewModel()
    val context = LocalContext.current
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top,
        modifier = Modifier
            .fillMaxSize()
            .padding(top = 16.dp, bottom = 0.dp, start = 16.dp, end = 16.dp)
    ) {
        MyTitle(text = "Доступные залы")
        LazyColumn {
            itemsIndexed(viewModel.getGymList()) { _, gym ->
                GymCard(gym) {
                    PreferencesHelper(context).saveCurGymId(gym.id)
                    navigateToGymScheme()
                }
            }
        }
    }
}

@Composable
fun GymCard(gym: GymInfoModel, navigateToGymScheme: () -> Unit) {
    val context = LocalContext.current
    OutlinedCard(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .clickable(onClick = {
                navigateToGymScheme()
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
                Text(text = gym.name,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp
                )

                Text(text = gym.address)
            }
        }
    }
}
