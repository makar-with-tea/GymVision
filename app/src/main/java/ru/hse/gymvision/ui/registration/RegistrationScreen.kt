package ru.hse.gymvision.ui.registration

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import org.koin.androidx.compose.koinViewModel
import ru.hse.gymvision.R
import ru.hse.gymvision.ui.composables.LoadingBlock
import ru.hse.gymvision.ui.composables.MyPasswordField
import ru.hse.gymvision.ui.composables.MyTextField
import ru.hse.gymvision.ui.composables.MyTitle
import ru.hse.gymvision.ui.theme.GymVisionTheme

@Composable
fun RegistrationScreen(
    navigateToAuthorization: () -> Unit,
    navigateToGymList: () -> Unit,
    viewModel: RegistrationViewModel = koinViewModel()
) {
    val state = viewModel.state.collectAsState()
    val action = viewModel.action.collectAsState()

    when (action.value) {
        is RegistrationAction.NavigateToAuthorization -> {
            navigateToAuthorization()
            viewModel.obtainEvent(RegistrationEvent.Clear)
        }

        is RegistrationAction.NavigateToGymList -> {
            navigateToGymList()
            viewModel.obtainEvent(RegistrationEvent.Clear)
        }

        null -> {}
    }

    when (state.value) {
        RegistrationState.Idle -> IdleState()
        RegistrationState.Loading -> LoadingState()
        is RegistrationState.Main -> MainState(
            state.value as RegistrationState.Main,
            onRegistrationClick = { name, surname, login, password, passwordRepeat ->
                viewModel.obtainEvent(
                    RegistrationEvent.RegistrationButtonClicked(
                        name,
                        surname,
                        login,
                        password,
                        passwordRepeat
                    )
                )
            },
            onShowPasswordClick = {
                viewModel.obtainEvent(RegistrationEvent.ShowPasswordButtonClicked)
            },
            onShowPasswordRepeatClick = {
                viewModel.obtainEvent(RegistrationEvent.ShowPasswordRepeatButtonClicked)
            },
            onLoginClick = { login, password ->
                viewModel.obtainEvent(RegistrationEvent.Clear)
                viewModel.obtainEvent(
                    RegistrationEvent.LoginButtonClicked(
                        login,
                        password
                    )
                )
            }
        )
    }

}

@Composable
fun MainState(
    state: RegistrationState.Main,
    onRegistrationClick: (String, String, String, String, String) -> Unit,
    onLoginClick: (String, String) -> Unit,
    onShowPasswordClick: () -> Unit,
    onShowPasswordRepeatClick: () -> Unit
) {
    val name: MutableState<String> = remember { mutableStateOf(state.name) }
    val surname: MutableState<String> = remember { mutableStateOf(state.surname) }
    val login: MutableState<String> = remember { mutableStateOf(state.login) }
    val password: MutableState<String> = remember { mutableStateOf(state.password) }
    val passwordRepeat: MutableState<String> = remember { mutableStateOf(state.passwordRepeat) }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        MyTitle(stringResource(R.string.registration_title))
        MyTextField(value = name.value, label = stringResource(R.string.name_label), isError = state.nameIsError, errorText = state.nameErrorText) {
            name.value = it
        }
        MyTextField(value = surname.value, label = stringResource(R.string.surname_label), isError = state.surnameIsError, errorText = state.surnameErrorText) {
            surname.value = it
        }
        MyTextField(value = login.value, label = stringResource(R.string.login_label), isError = state.loginIsError, errorText = state.loginErrorText) {
            login.value = it
        }
        MyPasswordField(
            value = password.value,
            label = stringResource(R.string.password_label),
            isError = state.passwordIsError,
            errorText = state.passwordErrorText,
            onValueChange = { password.value = it },
            onIconClick = { onShowPasswordClick() },
            passwordVisibility = state.passwordVisibility
        )
        MyPasswordField(
            value = passwordRepeat.value,
            label = stringResource(R.string.repeat_password_label),
            isError = state.passwordRepeatIsError,
            onValueChange = { passwordRepeat.value = it },
            onIconClick = { onShowPasswordRepeatClick() },
            passwordVisibility = state.passwordRepeatVisibility,
            errorText = state.passwordRepeatErrorText
        )
        Button(onClick = {
            onRegistrationClick(
                name.value,
                surname.value,
                login.value,
                password.value,
                passwordRepeat.value
            )
        }) {
            Text(stringResource(R.string.register_button))
        }
        Row(
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(stringResource(R.string.already_have_account))
            TextButton(
                onClick = { onLoginClick(login.value, password.value) },
                modifier = Modifier.wrapContentWidth(),
                contentPadding = PaddingValues(0.dp)
            ) {
                Text(stringResource(R.string.login_button), modifier = Modifier.padding(0.dp))
            }
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

@Preview(showBackground = true)
@Composable
fun RegistrationScreenPreview() {
    GymVisionTheme {
        RegistrationScreen(
            {}, {}
        )
    }
}