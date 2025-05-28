package ru.hse.gymvision.ui.registration

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
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
import androidx.compose.ui.unit.sp
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
            onRegistrationClick = { name, surname, email, login, password, passwordRepeat ->
                viewModel.obtainEvent(
                    RegistrationEvent.RegistrationButtonClicked(
                        name,
                        surname,
                        email,
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
    onRegistrationClick: (String, String, String, String, String, String) -> Unit,
    onLoginClick: (String, String) -> Unit,
    onShowPasswordClick: () -> Unit,
    onShowPasswordRepeatClick: () -> Unit
) {
    val name: MutableState<String> = remember { mutableStateOf(state.name) }
    val surname: MutableState<String> = remember { mutableStateOf(state.surname) }
    val email: MutableState<String> = remember { mutableStateOf(state.email) }
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
        MyTextField(
            value = name.value,
            label = stringResource(R.string.name_label),
            isError = state.nameError != RegistrationState.RegistrationError.IDLE,
            errorText = if (state.nameError != RegistrationState.RegistrationError.NETWORK)
                state.nameError.getText() else null
        ) {
            name.value = it
        }
        MyTextField(
            value = surname.value,
            label = stringResource(R.string.surname_label),
            isError = state.surnameError != RegistrationState.RegistrationError.IDLE,
            errorText = if (state.surnameError != RegistrationState.RegistrationError.NETWORK)
                state.surnameError.getText() else null
        ) {
            surname.value = it
        }
        MyTextField(
            value = email.value,
            label = stringResource(R.string.email_label),
            isError = state.emailError != RegistrationState.RegistrationError.IDLE,
            errorText = if (state.emailError != RegistrationState.RegistrationError.NETWORK)
                state.emailError.getText() else null
        ) {
            email.value = it
        }
        MyTextField(
            value = login.value,
            label = stringResource(R.string.login_label),
            isError = state.loginError != RegistrationState.RegistrationError.IDLE,
            errorText = if (state.loginError != RegistrationState.RegistrationError.NETWORK)
                state.loginError.getText() else null
        ) {
            login.value = it
        }
        MyPasswordField(
            value = password.value,
            label = stringResource(R.string.password_label),
            isError = state.passwordError != RegistrationState.RegistrationError.IDLE,
            errorText = if (state.passwordError != RegistrationState.RegistrationError.NETWORK)
                state.passwordError.getText() else null,
            onValueChange = { password.value = it },
            onIconClick = { onShowPasswordClick() },
            passwordVisibility = state.passwordVisibility
        )
        MyPasswordField(
            value = passwordRepeat.value,
            label = stringResource(R.string.repeat_password_label),
            isError = state.passwordRepeatError != RegistrationState.RegistrationError.IDLE,
            onValueChange = { passwordRepeat.value = it },
            onIconClick = { onShowPasswordRepeatClick() },
            passwordVisibility = state.passwordRepeatVisibility,
            errorText = state.passwordRepeatError.getText()
        )
        Button(onClick = {
            onRegistrationClick(
                name.value,
                surname.value,
                email.value,
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
            Text(
                text = stringResource(R.string.already_have_account),
                fontSize = 14.sp
            )
            TextButton(
                onClick = { onLoginClick(login.value, password.value) },
                modifier = Modifier.width(50.dp),
                contentPadding = PaddingValues(0.dp)
            ) {
                Text(
                    text = stringResource(R.string.login_button),
                    modifier = Modifier.padding(0.dp),
                    fontSize = 14.sp
                )
            }
        }
    }
}

@Composable
private fun RegistrationState.RegistrationError.getText(): String {
    return when (this) {
        RegistrationState.RegistrationError.NAME_LENGTH ->
            stringResource(R.string.name_length_error)
        RegistrationState.RegistrationError.SURNAME_LENGTH ->
            stringResource(R.string.surname_length_error)
        RegistrationState.RegistrationError.LOGIN_LENGTH ->
            stringResource(R.string.login_length_error)
        RegistrationState.RegistrationError.LOGIN_CONTENT ->
            stringResource(R.string.login_content_error)
        RegistrationState.RegistrationError.PASSWORD_LENGTH ->
            stringResource(R.string.password_length_error)
        RegistrationState.RegistrationError.PASSWORD_CONTENT ->
            stringResource(R.string.password_content_error)
        RegistrationState.RegistrationError.PASSWORD_MISMATCH ->
            stringResource(R.string.password_mismatch_error)
        RegistrationState.RegistrationError.LOGIN_TAKEN ->
            stringResource(R.string.login_taken_error)
        RegistrationState.RegistrationError.REGISTRATION_FAILED ->
            stringResource(R.string.registration_failed_error)
        RegistrationState.RegistrationError.NETWORK ->
            stringResource(R.string.network_error_short)
        RegistrationState.RegistrationError.EMAIL_CONTENT ->
            stringResource(R.string.email_content_error)
        RegistrationState.RegistrationError.IDLE -> ""
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