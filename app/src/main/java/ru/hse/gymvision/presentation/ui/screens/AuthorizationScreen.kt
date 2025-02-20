package ru.hse.gymvision.presentation.ui.screens
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
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

@Composable
fun AuthorizationScreen() {
    var login by rememberSaveable { mutableStateOf("") }
    var password by rememberSaveable { mutableStateOf("") }
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
            MyTitle("Авторизация")
            MyTextField(value = login, label = "Логин", isError = false) {
                login = it
            }
            MyPasswordField(
                value = password,
                label = "Пароль",
                isError = false,
                onValueChange = { password = it },
                onIconClick = {
                              // todo: viewModel.passwordVisibility = !viewModel.passwordVisibility
                },
                passwordVisibility = false
            )
            Button(onClick = {
                rootController.push("gymList")
            }) { // todo: viewModel.login(login, password)
                Text("Войти")
            }
            Row(
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Нет аккаунта?")
                TextButton(
                    onClick = {
                        rootController.push("registration")
                    },
                    modifier = Modifier.padding(horizontal = 4.dp, vertical = 0.dp)
                        .wrapContentSize(),
                    contentPadding = PaddingValues(0.dp)
                ) {
                    Text("Зарегистрироваться", modifier = Modifier.padding(0.dp))
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun AuthorizationScreenPreview() {
    AuthorizationScreen()
}