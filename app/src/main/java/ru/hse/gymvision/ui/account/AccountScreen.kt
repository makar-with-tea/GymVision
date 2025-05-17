package ru.hse.gymvision.ui.account

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import org.koin.androidx.compose.koinViewModel
import ru.hse.gymvision.R
import ru.hse.gymvision.ui.composables.LoadingBlock
import ru.hse.gymvision.ui.composables.MyAlertDialog
import ru.hse.gymvision.ui.composables.MyPasswordField
import ru.hse.gymvision.ui.composables.MyTitle

@Composable
fun AccountScreen(
    navigateToAuthorization: () -> Unit,
    viewModel: AccountViewModel = koinViewModel()
) {
    val state = viewModel.state.collectAsState()
    val action = viewModel.action.collectAsState()
    // todo: тут половина недоделана((

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
                onDeleteAccount = { viewModel.obtainEvent(AccountEvent.DeleteAccountButtonClicked(
                    (state.value as AccountState.Main).login
                )) },
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
                onSavePassword = { newPassword, oldPassword, realPassword ->
                    viewModel.obtainEvent(AccountEvent.SavePasswordButtonClicked(newPassword,
                        oldPassword, realPassword
                        ))
                },
                onShowOldPassword = { viewModel.obtainEvent(AccountEvent.ShowOldPasswordButtonClicked) },
                onShowNewPassword = { viewModel.obtainEvent(AccountEvent.ShowNewPasswordButtonClicked) }
            )
        }

        is AccountState.Idle -> {
            IdleState()
            viewModel.obtainEvent(AccountEvent.GetUserInfo)
        }

        is AccountState.Error -> {
            ErrorState(
                errorText = (state.value as AccountState.Error).message,
                onDismiss = {
                    viewModel.obtainEvent(AccountEvent.GetUserInfo)
                },
                onConfirm = {
                    viewModel.obtainEvent(AccountEvent.GetUserInfo)
                }
            )
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
        MyTitle(
            text = stringResource(
                R.string.my_profile_title
            )
        )
        Text("${stringResource(id = R.string.name)}: ${state.name}")
        Text("${stringResource(id = R.string.surname)}: ${state.surname}")
        Text("${stringResource(id = R.string.login)}: ${state.login}")
        Button(onClick = { onEditName() }) {
            Text(stringResource(id = R.string.edit))
        }
        Button(onClick = { onChangePassword() }) {
            Text(stringResource(id = R.string.change_password))
        }
        Button(onClick = { onLogout() }) {
            Text(stringResource(id = R.string.logout))
        }
        Button(onClick = { showDeleteDialog.value = true }) {
            Text(stringResource(id = R.string.delete_account))
        }
    }

    if (showDeleteDialog.value) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog.value = false },
            title = { Text(stringResource(id = R.string.delete_account_title)) },
            text = { Text(stringResource(id = R.string.delete_account_confirmation)) },
            confirmButton = {
                Button(onClick = { onDeleteAccount() }) {
                    Text(stringResource(id = R.string.yes))
                }
            },
            dismissButton = {
                Button(onClick = { showDeleteDialog.value = false }) {
                    Text(stringResource(id = R.string.no))
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
        MyTitle(text = stringResource(id = R.string.edit_name))
        TextField(value = name.value, onValueChange = { name.value = it }, label = { Text(stringResource(id = R.string.name)) })
        TextField(value = surname.value, onValueChange = { surname.value = it }, label = { Text(stringResource(id = R.string.surname)) })
        Button(onClick = { onSaveName(name.value, surname.value) }) {
            Text(stringResource(id = R.string.save))
        }
    }
}

@Composable
fun ChangePasswordState(
    state: AccountState.ChangePassword,
    onSavePassword: (String, String, String) -> Unit,
    onShowOldPassword: () -> Unit,
    onShowNewPassword: () -> Unit
) {
    val oldPassword: MutableState<String> = remember { mutableStateOf(state.oldPassword) }
    val newPassword: MutableState<String> = remember { mutableStateOf(state.newPassword) }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top,
        modifier = Modifier
            .fillMaxSize()
            .padding(top = 16.dp, bottom = 0.dp, start = 16.dp, end = 16.dp)
    ) {
        MyTitle(text = stringResource(id = R.string.change_password_title))


        MyPasswordField(
            value = "",
            label = stringResource(id = R.string.old_password),
            isError = false,
            onValueChange = { oldPassword.value = it },
            onIconClick = { onShowOldPassword() },
            passwordVisibility = state.oldPasswordVisibility
        )
        MyPasswordField(
            value = "",
            label = stringResource(id = R.string.new_password),
            isError = false,
            onValueChange = { newPassword.value = it },
            onIconClick = { onShowNewPassword() },
            passwordVisibility = state.newPasswordRepeatVisibility
        )
        Button(onClick = {
            onSavePassword(
                newPassword.value,
                oldPassword.value, state.password
            )
        }) {
            Text(stringResource(id = R.string.save))
        }
    }
    if (state.isLoading) {
        LoadingBlock()
    }
}

@Composable
fun ErrorState(
    errorText: String?,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.5f))
            .clickable { },
        contentAlignment = Alignment.Center
    ) {
        MyAlertDialog(
            title = stringResource(id = R.string.loading_error),
            text = errorText ?: stringResource(id = R.string.unknown_error),
            onConfirm = { onConfirm() },
            onDismissRequest = { onDismiss() },
            confirmButtonText = stringResource(id = R.string.reload_button_text),
        )
    }
}
