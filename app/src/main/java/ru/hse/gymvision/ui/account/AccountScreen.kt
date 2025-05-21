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
import ru.hse.gymvision.ui.composables.MyTextField
import ru.hse.gymvision.ui.composables.MyTitle

@Composable
fun AccountScreen(
    navigateToAuthorization: () -> Unit,
    viewModel: AccountViewModel = koinViewModel()
) {
    val state = viewModel.state.collectAsState()
    val action = viewModel.action.collectAsState()

    when (action.value) {
        is AccountAction.NavigateToAuthorization -> {
            navigateToAuthorization()
            viewModel.obtainEvent(AccountEvent.Clear)
        }

        null -> {}
    }

    when (state.value) {
        is AccountState.Main -> {
            MainState(
                state.value as AccountState.Main,
                onEditName = { viewModel.obtainEvent(AccountEvent.EditNameButtonClicked) },
                onChangePassword = {
                    viewModel.obtainEvent(AccountEvent.EditPasswordButtonClicked)
                },
                onDeleteAccount = {
                    viewModel.obtainEvent(
                        AccountEvent.DeleteAccountButtonClicked(
                            (state.value as AccountState.Main).login
                        )
                    )
                },
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
                    viewModel.obtainEvent(
                        AccountEvent.SavePasswordButtonClicked(
                            newPassword,
                            oldPassword, realPassword
                        )
                    )
                },
                onShowOldPassword = {
                    viewModel.obtainEvent(AccountEvent.ShowOldPasswordButtonClicked)
                },
                onShowNewPassword = {
                    viewModel.obtainEvent(AccountEvent.ShowNewPasswordButtonClicked)
                },
                onShowNewPasswordRepeat = {
                    viewModel.obtainEvent(AccountEvent.ShowNewPasswordRepeatButtonClicked)
                }
            )
        }

        is AccountState.Idle -> {
            IdleState()
            viewModel.obtainEvent(AccountEvent.GetUserInfo)
        }

        is AccountState.Error -> {
            ErrorState(
                errorText = (state.value as AccountState.Error).error.toText(),
                onDismiss = {
                    viewModel.obtainEvent(AccountEvent.LogoutButtonClicked)
                },
                onConfirm = {
                    viewModel.obtainEvent(AccountEvent.GetUserInfo)
                }
            )
        }

        is AccountState.DeletionError -> {
            ErrorState(
                errorText = stringResource(R.string.account_delete_error),
                onDismiss = {
                    viewModel.obtainEvent(AccountEvent.GetUserInfo)
                },
                onConfirm = {
                    viewModel.obtainEvent(
                        AccountEvent.DeleteAccountButtonClicked(
                            (state.value as AccountState.DeletionError).login
                        )
                    )
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
        MyTextField(
            value = name.value,
            onValueChange = { name.value = it },
            label = stringResource(id = R.string.name),
            isError = state.nameError != AccountState.AccountError.IDLE,
            errorText = if (state.nameError != AccountState.AccountError.NETWORK)
                state.nameError.toText() else null
        )
        MyTextField(
            value = surname.value,
            onValueChange = { surname.value = it },
            label = stringResource(id = R.string.surname),
            isError = state.surnameError != AccountState.AccountError.IDLE,
            errorText = state.surnameError.toText()
        )
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
    onShowNewPassword: () -> Unit,
    onShowNewPasswordRepeat: () -> Unit
) {
    val oldPassword: MutableState<String> = remember { mutableStateOf(state.oldPassword) }
    val newPassword: MutableState<String> = remember { mutableStateOf(state.newPassword) }
    val newPasswordRepeat: MutableState<String> = remember { mutableStateOf(state.newPassword) }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top,
        modifier = Modifier
            .fillMaxSize()
            .padding(top = 16.dp, bottom = 0.dp, start = 16.dp, end = 16.dp)
    ) {
        MyTitle(text = stringResource(id = R.string.change_password_title))

        MyPasswordField(
            value = oldPassword.value,
            label = stringResource(id = R.string.old_password),
            isError = state.oldPasswordError != AccountState.AccountError.IDLE,
            errorText = if (state.oldPasswordError != AccountState.AccountError.NETWORK)
                state.oldPasswordError.toText() else null,
            onValueChange = { oldPassword.value = it },
            onIconClick = { onShowOldPassword() },
            passwordVisibility = state.oldPasswordVisibility
        )
        MyPasswordField(
            value = newPassword.value,
            label = stringResource(id = R.string.new_password),
            isError = state.newPasswordError != AccountState.AccountError.IDLE,
            errorText = if (state.newPasswordError != AccountState.AccountError.NETWORK)
                state.newPasswordError.toText() else null,
            onValueChange = { newPassword.value = it },
            onIconClick = { onShowNewPassword() },
            passwordVisibility = state.newPasswordVisibility
        )
        MyPasswordField(
            value = newPasswordRepeat.value,
            label = stringResource(id = R.string.repeat_new_password_label),
            isError = state.newPasswordRepeatError != AccountState.AccountError.IDLE,
            errorText = state.newPasswordRepeatError.toText(),
            onValueChange = { newPasswordRepeat.value = it },
            onIconClick = { onShowNewPasswordRepeat() },
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
            onConfirm = onConfirm,
            onDismissRequest = onDismiss,
            confirmButtonText = stringResource(id = R.string.retry_button_text)
        )
    }
}

@Composable
fun AccountState.AccountError.toText() = when (this) {
    AccountState.AccountError.IDLE -> null
    AccountState.AccountError.NAME_LENGTH -> stringResource(id = R.string.name_length_error)
    AccountState.AccountError.SURNAME_LENGTH -> stringResource(id = R.string.surname_length_error)
    AccountState.AccountError.PASSWORD_LENGTH -> stringResource(id = R.string.password_length_error)
    AccountState.AccountError.PASSWORD_CONTENT ->
        stringResource(id = R.string.password_content_error)

    AccountState.AccountError.PASSWORD_MISMATCH ->
        stringResource(id = R.string.password_mismatch_error)

    AccountState.AccountError.PASSWORD_INCORRECT ->
        stringResource(id = R.string.password_incorrect_error)

    AccountState.AccountError.CHANGE_FAILED ->
        stringResource(id = R.string.change_failed_error)

    AccountState.AccountError.NETWORK ->
        stringResource(id = R.string.network_error_short)

    AccountState.AccountError.ACCOUNT_NOT_FOUND ->
        stringResource(id = R.string.account_not_found_error)
}