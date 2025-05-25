package ru.hse.gymvision.ui.authorization

import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
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

@Composable
fun AuthorizationScreen(
    navigateToGymList: () -> Unit,
    navigateToRegistration: () -> Unit,
    viewModel: AuthorizationViewModel = koinViewModel()
) {
    val state = viewModel.state.collectAsState()
    val action = viewModel.action.collectAsState()

    Log.d("AuthorizationScreen", "State: $state")
    when (action.value) {
        is AuthorizationAction.NavigateToGymList -> {
            navigateToGymList()
            viewModel.obtainEvent(AuthorizationEvent.Clear)
        }

        is AuthorizationAction.NavigateToRegistration -> {
            navigateToRegistration()
            viewModel.obtainEvent(AuthorizationEvent.Clear)
        }

        null -> {}
    }

    when (state.value) {
        is AuthorizationState.Main -> {
            MainState(
                state.value as AuthorizationState.Main,
                onLoginClick = { login, password ->
                    viewModel.obtainEvent(AuthorizationEvent.LoginButtonClicked(login, password))
                },
                onRegistrationClick = {login, password ->
                    viewModel.obtainEvent(AuthorizationEvent.RegistrationButtonClicked(login, password))
                },
                onShowPasswordClick = {
                    viewModel.obtainEvent(AuthorizationEvent.ShowPasswordButtonClicked)
                }
            )
        }
        is AuthorizationState.Idle -> {
            IdleState()
            viewModel.obtainEvent(AuthorizationEvent.CheckPastLogin)
        }
        is AuthorizationState.Loading -> {
            LoadingState()
        }
    }
}

@Composable
fun MainState(
    state: AuthorizationState.Main,
    onLoginClick: (String, String) -> Unit,
    onRegistrationClick: (String, String) -> Unit,
    onShowPasswordClick: () -> Unit
) {
    val login: MutableState<String> = remember { mutableStateOf(state.login) }
    val password: MutableState<String> = remember { mutableStateOf(state.password) }

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
            MyTitle(stringResource(id = R.string.authorization_title))
            MyTextField(
                value = login.value,
                label = stringResource(id = R.string.login_label),
                isError = state.loginError != AuthorizationState.AuthorizationError.IDLE,
                errorText = if (state.loginError != AuthorizationState.AuthorizationError.NETWORK)
                    state.loginError.toText() else null
            ) {
                login.value = it
            }
            MyPasswordField(
                value = password.value,
                label = stringResource(id = R.string.password_label),
                isError = state.passwordError != AuthorizationState.AuthorizationError.IDLE,
                onValueChange = { password.value = it },
                onIconClick = {
                    onShowPasswordClick()
                },
                passwordVisibility = state.passwordVisibility,
                errorText = state.passwordError.toText()
            )
            Button(onClick = {
                onLoginClick(login.value, password.value)
            }) {
                Text(stringResource(id = R.string.login_button))
            }
            Row(
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = stringResource(R.string.no_account),
                    fontSize = 14.sp
                )
                TextButton(
                    onClick = {
                        onRegistrationClick(login.value, password.value)
                    },
                    modifier = Modifier.width(145.dp),
                    contentPadding = PaddingValues(0.dp)
                ) {
                    Text(
                        text = stringResource(R.string.register_button),
                        modifier = Modifier.padding(0.dp),
                        fontSize = 14.sp
                    )
                }
            }
        }
    }

    if (state.loading) {
        LoadingBlock()
    }
}

@Composable
private fun AuthorizationState.AuthorizationError.toText() = when (this) {
    AuthorizationState.AuthorizationError.EMPTY_LOGIN ->
        stringResource(id = R.string.empty_login_error)
    AuthorizationState.AuthorizationError.EMPTY_PASSWORD ->
        stringResource(id = R.string.empty_password_error)
    AuthorizationState.AuthorizationError.INVALID_CREDENTIALS ->
        stringResource(id = R.string.invalid_credentials_error)
    AuthorizationState.AuthorizationError.NETWORK ->
        stringResource(id = R.string.network_error_short)
    AuthorizationState.AuthorizationError.IDLE -> ""
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
fun AuthorizationScreenPreview() {
    AuthorizationScreen(
            {}, {}
    )
}