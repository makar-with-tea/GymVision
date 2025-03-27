package ru.hse.gymvision.ui.registration

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import ru.hse.gymvision.domain.usecase.user.RegisterUseCase

const val LATIN = "abcdefghijklmnopqrstuvwxyz"

class RegistrationViewModel(
    private val registerUseCase: RegisterUseCase
): ViewModel() {
    private val _state: MutableStateFlow<RegistrationState> = MutableStateFlow(
        RegistrationState.Main(
            login = "",
            password = "",
            passwordRepeat = "",
            passwordVisibility = false,
            passwordRepeatVisibility = false,
            loading = false
        )
    )
    val state: StateFlow<RegistrationState>
        get() = _state
    private val _action = MutableStateFlow<RegistrationAction?>(null)
    val action: StateFlow<RegistrationAction?>
        get() = _action

    fun obtainEvent(event: RegistrationEvent) {
        when (event) {
            is RegistrationEvent.RegistrationButtonClicked -> {
                register(
                    event.name,
                    event.surname,
                    event.login,
                    event.password,
                    event.passwordRepeat
                )
            }

            is RegistrationEvent.LoginButtonClicked -> {
                _state.value = RegistrationState.Main(
                    login = event.login,
                    password = event.password
                )
                _action.value = RegistrationAction.NavigateToAuthorization
            }

            is RegistrationEvent.ShowPasswordButtonClicked -> {
                changeVisibilityState()
            }

            is RegistrationEvent.ShowPasswordRepeatButtonClicked -> {
                changeVisibilityRepeatState()
            }

            is RegistrationEvent.Clear -> clear()
        }
    }

    private fun register(
        name: String,
        surname: String,
        login: String,
        password: String,
        passwordRepeat: String
    ) {
        if (_state.value !is RegistrationState.Main) {
            return
        }
        _state.value = (_state.value as RegistrationState.Main).copy(
            name = name,
            surname = surname,
            login = login,
            password = password,
            passwordRepeat = passwordRepeat,
            nameIsError = false,
            surnameIsError = false,
            loginIsError = false,
            passwordIsError = false,
            passwordRepeatIsError = false,
            nameErrorText = null,
            surnameErrorText = null,
            loginErrorText = null,
            passwordErrorText = null,
            passwordRepeatErrorText = null
        )

        var isError = false
        if (name.length < 2 || name.length > 20) {
            _state.value = (_state.value as RegistrationState.Main).copy(
                nameErrorText = "Имя должно быть длиной от 2 до 20 символов",
                nameIsError = true
            )
            isError = true
        }
        if (surname.length < 2 || surname.length > 20) {
            _state.value = (_state.value as RegistrationState.Main).copy(
                surnameErrorText = "Фамилия должна быть длиной от 2 до 20 символов",
                surnameIsError = true
            )
            isError = true
        }
        if (login.length < 5 || login.length > 15) {
            _state.value = (_state.value as RegistrationState.Main).copy(
                loginErrorText = "Логин должен быть длиной от 5 до 15 символов",
                loginIsError = true
            )
            isError = true
        }
        if (login.any { !it.isDigit() && !LATIN.contains(it.lowercase()) }) {
            _state.value = (_state.value as RegistrationState.Main).copy(
                loginErrorText = "Логин должен содержать только латинские буквы и цифры",
                loginIsError = true
            )
            isError = true
        }
        if (password.length < 8) {
            _state.value = (_state.value as RegistrationState.Main).copy(
                passwordErrorText = "Пароль должен быть длиной не менее 8 символов",
                passwordIsError = true
            )
            isError = true
        }
        if (password.all { !it.isDigit() } || password.all { !it.isLetter() }) {
            _state.value = (_state.value as RegistrationState.Main).copy(
                passwordErrorText = "Пароль должен содержать латинские буквы и цифры",
                passwordIsError = true
            )
            isError = true
        }
        if (password != passwordRepeat) {
            _state.value = (_state.value as RegistrationState.Main).copy(
                passwordRepeatErrorText = "Пароли не совпадают",
                passwordRepeatIsError = true
            )
            isError = true
        }
        if (isError) {
            return
        }

        viewModelScope.launch(Dispatchers.IO) {
            val res = registerUseCase.execute(name, surname, login, password)
            withContext(Dispatchers.Main) {
                if (!res) {
                    _state.value = RegistrationState.Main(
                        name = name,
                        surname = surname,
                        login = login,
                        password = password,
                        loginErrorText = "Не удалось зарегистрироваться",
                        loginIsError = true
                    )
                } else _action.value = RegistrationAction.NavigateToGymList
            }
        }
    }

    private fun clear() {
        _state.value = RegistrationState.Main()
        _action.value = null
    }

    private fun changeVisibilityState() {
        if (_state.value is RegistrationState.Main) {
            _state.value = (_state.value as RegistrationState.Main).copy(
                passwordVisibility = !(_state.value as RegistrationState.Main).passwordVisibility)
        }
    }

    private fun changeVisibilityRepeatState() {
        if (_state.value is RegistrationState.Main) {
            _state.value = (_state.value as RegistrationState.Main).copy(
                passwordRepeatVisibility = !(_state.value as RegistrationState.Main).passwordRepeatVisibility)
        }
    }
}