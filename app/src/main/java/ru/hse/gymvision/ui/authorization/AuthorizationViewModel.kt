package ru.hse.gymvision.ui.authorization

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import ru.hse.gymvision.domain.usecase.user.GetPastLoginUseCase
import ru.hse.gymvision.domain.usecase.user.LoginUseCase

class AuthorizationViewModel(
    private val getPastLoginUseCase: GetPastLoginUseCase,
    private val loginUseCase: LoginUseCase
    ): ViewModel() {
    private val _state: MutableStateFlow<AuthorizationState> =
        MutableStateFlow(AuthorizationState.Idle)
    val state: StateFlow<AuthorizationState>
        get() = _state
    private val _action = MutableStateFlow<AuthorizationAction?>(null)
    val action: StateFlow<AuthorizationAction?>
        get() = _action

    fun obtainEvent(event: AuthorizationEvent) {
        when (event) {
            is AuthorizationEvent.LoginButtonClicked -> {
                login(event.login, event.password)
            }

            is AuthorizationEvent.RegistrationButtonClicked -> {
                register(event.login, event.password)
            }

            is AuthorizationEvent.ShowPasswordButtonClicked -> {
                Log.d("AuthorizationViewModel", "ShowPasswordButtonClicked")
                changeVisibilityState()
            }

            is AuthorizationEvent.Clear -> clear()
            is AuthorizationEvent.CheckPastLogin -> checkPastLogin()
        }
    }

    private fun login(login: String, password: String) {
        _state.value = AuthorizationState.Main(
            login = login,
            password = password,
            loading = true
        )
        var isError = false

        if (login.isEmpty()) {
            _state.value = (_state.value as AuthorizationState.Main).copy(
                loginError = true,
                loginErrorText = "Поле не может быть пустым",
                loading = false
            )
            isError = true
        }

        if (password.isEmpty()) {
            _state.value = (_state.value as AuthorizationState.Main).copy(
                passwordError = true,
                passwordErrorText = "Поле не может быть пустым",
                loading = false
            )
            isError = true
        }

        if (isError) {
            return
        }

        viewModelScope.launch(Dispatchers.IO) {
            val res = loginUseCase.execute(login, password)
            if (!res) {
                _state.value = (_state.value as AuthorizationState.Main).copy(
                    loginError = true,
                    passwordError = true,
                    loginErrorText = "Неверный логин или пароль",
                )
                withContext(Dispatchers.Main) {
                    _state.value = (_state.value as AuthorizationState.Main).copy(
                        loading = false
                    )
                }
                return@launch
            }
            withContext(Dispatchers.Main) {
                _action.value = AuthorizationAction.NavigateToGymList
            }
        }
    }

    private fun clear() {
        _state.value = AuthorizationState.Idle
        _action.value = null
    }

    private fun register(login: String, password: String) {
        _state.value = AuthorizationState.Main(
            login = login,
            password = password
        )
        _action.value = AuthorizationAction.NavigateToRegistration
    }

    private fun changeVisibilityState() {
        if (_state.value is AuthorizationState.Main) {
            _state.value = (_state.value as AuthorizationState.Main).copy(
                passwordVisibility = !(_state.value as AuthorizationState.Main).passwordVisibility
            )
        }
    }

    private fun checkPastLogin() {
        _state.value = AuthorizationState.Loading
        viewModelScope.launch(Dispatchers.IO) {
            val username = getPastLoginUseCase.execute()
            withContext(Dispatchers.Main) {
                if (username != null) {
                    _action.value = AuthorizationAction.NavigateToGymList
                    Log.d("AuthorizationViewModel", "past login = $username")
                } else {
                    _state.value = AuthorizationState.Main()
                    Log.d("AuthorizationViewModel", "no past login")
                }
            }
        }
    }
}