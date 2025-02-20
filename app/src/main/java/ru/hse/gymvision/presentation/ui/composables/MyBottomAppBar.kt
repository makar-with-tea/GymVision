package ru.hse.gymvision.presentation.ui.composables

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import ru.alexgladkov.odyssey.compose.extensions.push
import ru.alexgladkov.odyssey.compose.local.LocalRootController
import ru.hse.gymvision.R

@Composable
fun MyBottomAppBar() {
    val rootController = LocalRootController.current
    BottomAppBar {
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxWidth()
                .padding(24.dp, 4.dp, 24.dp, 4.dp)
        ) {
            IconButton(onClick = { rootController.push("gymList") },
                modifier = Modifier.size(48.dp)) {
                Icon(
                    imageVector = Icons.Filled.Home,
                    contentDescription = "Home",
                    modifier = Modifier.size(64.dp)
                )
            }
            IconButton(onClick = { rootController.push("gymScheme") },
                modifier = Modifier.size(48.dp)) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_fitness_center),
                    contentDescription = "Fitness center",
                    modifier = Modifier.size(64.dp)
                )
            }
            IconButton(onClick = { rootController.push("profile") },
                modifier = Modifier.size(48.dp)) {
                Icon(
                    imageVector = Icons.Filled.AccountCircle,
                    contentDescription = "Account",
                    modifier = Modifier.size(64.dp)
                )
            }
        }
    }
}