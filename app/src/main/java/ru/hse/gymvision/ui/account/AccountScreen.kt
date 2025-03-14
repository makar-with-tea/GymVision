package ru.hse.gymvision.ui.account

import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.koin.androidx.compose.koinViewModel
import ru.hse.gymvision.ui.composables.LoadingBlock
import ru.hse.gymvision.ui.composables.MyPasswordField
import ru.hse.gymvision.ui.composables.MyTitle

@Composable
fun AccountScreen(
    navigateToAuthorization: () -> Unit,
    viewModel: AccountViewModel = koinViewModel()
) {
    val state = viewModel.state.collectAsState()
    val action = viewModel.action.collectAsState()
    // todo: тут половина недоделана(( как минимум обновление имени

    when (action.value) {
        is AccountAction.NavigateToAuthorization -> {
            navigateToAuthorization()
            viewModel.obtainEvent(AccountEvent.Clear)
        }

        null -> {}
    }

    when (state.value) {
        is AccountState.Main -> {
            MainState(state.value as AccountState.Main,
                onEditName = { viewModel.obtainEvent(AccountEvent.EditNameButtonClicked) },
                onChangePassword = { viewModel.obtainEvent(AccountEvent.EditPasswordButtonClicked) },
                onDeleteAccount = { viewModel.obtainEvent(AccountEvent.DeleteAccountButtonClicked) },
                onLogout = { viewModel.obtainEvent(AccountEvent.LogoutButtonClicked) }
                )
        }

        is AccountState.Loading -> {
            LoadingState()
        }

        is AccountState.EditName -> {
            EditNameState(
                state.value as AccountState.EditName,
                onSaveName = { name, surname ->
                    viewModel.obtainEvent(AccountEvent.SaveNameButtonClicked(name, surname))
                }
            )
        }

        is AccountState.ChangePassword -> {
            ChangePasswordState(
                state.value as AccountState.ChangePassword,
                onSavePassword = { newPassword ->
                    viewModel.obtainEvent(AccountEvent.SavePasswordButtonClicked(newPassword))
                }
            )
        }

        is AccountState.Idle -> {
            IdleState()
            viewModel.obtainEvent(AccountEvent.GetUserInfo(0)) // TODO: get user id
        }
    }
}

@Composable
fun IdleState() {
    LoadingBlock()
}

@Composable
fun LoadingState() {
    LoadingBlock()
}

@Composable
fun MainState(
    state: AccountState.Main,
    onChangePassword: () -> Unit,
    onEditName: () -> Unit,
    onDeleteAccount: () -> Unit,
    onLogout: () -> Unit
) {
    val showDeleteDialog = remember { mutableStateOf(false) }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top,
        modifier = Modifier
            .fillMaxSize()
            .padding(top = 16.dp, bottom = 0.dp, start = 16.dp, end = 16.dp)
    ) {
        MyTitle(text = "Мой профиль")
        Text("Имя: ${state.name}")
        Text("Фамилия: ${state.surname}")
        Text("Логин: ${state.login}")
        Button(onClick = {
            onEditName()
        }) {
            Text("Редактировать")
        }
        Button(onClick = {
            onChangePassword()
        }) {
            Text("Сменить пароль")
        }
        Button(onClick = {
            onLogout()
        }) {
            Text("Выйти")
        }
        Button(onClick = { showDeleteDialog.value = true }) {
            Text("Удалить аккаунт")
        }

    }
    if (showDeleteDialog.value) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog.value = false },
            title = { Text("Удаление аккаунта") },
            text = { Text("Вы уверены, что хотите удалить аккаунт?") },
            confirmButton = {
                Button(onClick = { onDeleteAccount() }) {
                    Text("Да")
                }
            },
            dismissButton = {
                Button(onClick = { showDeleteDialog.value = false }) {
                    Text("Нет")
                }
            }
        )
    }
}

@Composable
fun EditNameState(state: AccountState.EditName,
                  onSaveName: (String, String) -> Unit) {
    Log.d("EditNameState", "EditNameState")
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top,
        modifier = Modifier
            .fillMaxSize()
            .padding(top = 16.dp, bottom = 0.dp, start = 16.dp, end = 16.dp)
    ) {
        val name = remember { mutableStateOf(state.name) }
        val surname = remember { mutableStateOf(state.surname) }
        MyTitle(text = "Редактирование имени")
        TextField(value = name.value, onValueChange = { name.value = it }, label = { Text("Имя") })
        TextField(value = surname.value, onValueChange = { surname.value = it }, label = { Text("Фамилия") })
        Button(onClick = { onSaveName(name.value, surname.value)}) {
            Text("Сохранить")
        }
    }
}

@Composable
fun ChangePasswordState(
    state: AccountState.ChangePassword,
    onSavePassword: (String) -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top,
        modifier = Modifier
            .fillMaxSize()
            .padding(top = 16.dp, bottom = 0.dp, start = 16.dp, end = 16.dp)
    ) {
        MyTitle(text = "Смена пароля")
        val oldPassword = remember { mutableStateOf("") }
        val newPassword = remember { mutableStateOf("") }

        MyPasswordField(
            value = "",
            label = "Старый пароль",
            isError = false,
            onValueChange = { oldPassword.value = it },
            onIconClick = { },
            passwordVisibility = false // todo: viewmodel
        )
        MyPasswordField(
            value = "",
            label = "Новый пароль",
            isError = false,
            onValueChange = { newPassword.value = it },
            onIconClick = { },
            passwordVisibility = false // todo: viewmodel
        )
        Button(onClick = { onSavePassword(newPassword.value) }) {
            Text("Сохранить")
        }
    }
    if (state.isLoading) {
        LoadingBlock()
    }
}
