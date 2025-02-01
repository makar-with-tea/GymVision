package ru.hse.gymvision.presentation.ui.screens
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import ru.hse.gymvision.presentation.navigation.LocalNavController
import ru.hse.gymvision.presentation.ui.composables.BottomBar
import ru.hse.gymvision.presentation.ui.composables.MyPasswordField
import ru.hse.gymvision.presentation.ui.composables.MyTextField
import ru.hse.gymvision.presentation.ui.composables.MyTitle

@Composable
fun AuthorizationScreen() {
    val navController = LocalNavController.current
    val login by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        bottomBar = {BottomBar()}
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
            }
            MyPasswordField(
                value = password,
                label = "Пароль",
                isError = false,
                onValueChange = { password = it },
                onIconClick = {  },
                passwordVisibility = false
            )
            Button(onClick = { }) {
                Text("Войти")
            }
        }
    }

}

@Preview(showBackground = true)
@Composable
fun AuthorizationScreenPreview() {
    AuthorizationScreen()
}