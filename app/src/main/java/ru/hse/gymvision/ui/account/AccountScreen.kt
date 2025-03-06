package ru.hse.gymvision.ui.account

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import ru.alexgladkov.odyssey.compose.local.LocalRootController
import ru.hse.gymvision.ui.BottomNavScreen
import ru.hse.gymvision.ui.composables.MyBottomAppBar
import ru.hse.gymvision.ui.composables.MyTitle

@Composable
fun AccountScreen() {
    val rootController = LocalRootController.current
    val showDeleteDialog = remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf("") }
    var successMessage by remember { mutableStateOf("") }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        bottomBar = { MyBottomAppBar(BottomNavScreen.PROFILE) },
    ) { paddingValues ->
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top,
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
                .padding(16.dp)
        ) {
            MyTitle(text = "Мой профиль")
            Text("Имя: Иван")
            Text("Фамилия: Иванов")
            Text("Логин: iiivanov@hse.ru")
            Button(onClick = {
//                rootController.push("editAccount")
            }) {
                Text("Редактировать")
            }
            Button(onClick = {
//                rootController.push("changePassword")
            }) {
                Text("Сменить пароль")
            }
            Button(onClick = { showDeleteDialog.value = true }) {
                Text("Удалить аккаунт")
            }
            if (errorMessage.isNotEmpty()) {
                Text(text = errorMessage, color = Color.Red)
            }
            if (successMessage.isNotEmpty()) {
                Text(text = successMessage, color = Color.Green)
            }
        }
    }

//    if (showDeleteDialog.value) {
//        DeleteAccountDialog(
//            onConfirm = {
//                // Handle account deletion
//                rootController.push("authorization")
//            },
//            onDismiss = { showDeleteDialog.value = false }
//        )
//    }
}