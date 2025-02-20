package ru.hse.gymvision.presentation.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import ru.alexgladkov.odyssey.compose.extensions.push
import ru.alexgladkov.odyssey.compose.local.LocalRootController
import ru.hse.gymvision.presentation.ui.composables.MyPasswordField
import ru.hse.gymvision.presentation.ui.composables.MyTextField
import ru.hse.gymvision.presentation.ui.composables.MyTitle
import ru.hse.gymvision.presentation.ui.theme.GymVisionTheme

@Composable
fun RegistrationScreen() {
    var name by remember { mutableStateOf("") }
    var surname by remember { mutableStateOf("") }
    var login by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordRepeat by remember { mutableStateOf("") }
    val rootController = LocalRootController.current

    Scaffold(
        modifier = Modifier.fillMaxSize(),
    ) { paddingValues ->
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
                .padding(16.dp)
        ) {
            MyTitle("Регистрация")
            MyTextField(value = name, label = "Имя", isError = false) {
                name = it
            }
            MyTextField(value = surname, label = "Фамилия", isError = false) {
                surname = it
            }
            MyTextField(value = login, label = "Логин", isError = false) {
                login = it
            }
            MyPasswordField(
                value = password,
                label = "Пароль",
                isError = false,
                onValueChange = { password = it },
                onIconClick = {  },
                passwordVisibility = false
            )
            MyPasswordField(
                value = passwordRepeat,
                label = "Повторите пароль",
                isError = false,
                onValueChange = { passwordRepeat = it },
                onIconClick = { /*TODO*/ },
                passwordVisibility = false
            )
            Button(onClick = {
                rootController.push("gymList") // todo: viewModel.register
            }) {
                Text("Зарегистрироваться")
            }
            Row(
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Уже есть аккаунт?")
                TextButton(
                    onClick = {
                        rootController.push("authorization")
                    },
                    modifier = Modifier.wrapContentWidth(),
                    contentPadding = PaddingValues(0.dp)
                ) {
                    Text("Войти", modifier = Modifier.padding(0.dp))
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun RegistrationScreenPreview() {
    GymVisionTheme {
        RegistrationScreen()
    }
}