package ru.hse.gymvision.ui.registration

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import ru.hse.gymvision.domain.usecase.user.CheckLoginAvailableUseCase
import ru.hse.gymvision.domain.usecase.user.RegisterUseCase

const val LATIN = "abcdefghijklmnopqrstuvwxyz"

class RegistrationViewModel(
    private val registerUseCase: RegisterUseCase,
    private val checkLoginAvailableUseCase: CheckLoginAvailableUseCase
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
            nameError = RegistrationState.RegistrationError.IDLE,
            surnameError = RegistrationState.RegistrationError.IDLE,
            loginError = RegistrationState.RegistrationError.IDLE,
            passwordError = RegistrationState.RegistrationError.IDLE,
            passwordRepeatError = RegistrationState.RegistrationError.IDLE,
            loading = true
        )

        var isError = false
        if (name.length < 2 || name.length > 20) {
            _state.value = (_state.value as RegistrationState.Main).copy(
                nameError = RegistrationState.RegistrationError.NAME_LENGTH,
            )
            isError = true
        }
        if (surname.length < 2 || surname.length > 20) {
            _state.value = (_state.value as RegistrationState.Main).copy(
                surnameError = RegistrationState.RegistrationError.SURNAME_LENGTH,
            )
            isError = true
        }
        if (login.length < 5 || login.length > 15) {
            _state.value = (_state.value as RegistrationState.Main).copy(
                loginError = RegistrationState.RegistrationError.LOGIN_LENGTH,
            )
            isError = true
        }
        if (login.any { !it.isDigit() && !LATIN.contains(it.lowercase()) }) {
            _state.value = (_state.value as RegistrationState.Main).copy(
                loginError = RegistrationState.RegistrationError.LOGIN_CONTENT,
            )
            isError = true
        }
        if (password.length < 8) {
            _state.value = (_state.value as RegistrationState.Main).copy(
                passwordError = RegistrationState.RegistrationError.PASSWORD_LENGTH,
            )
            isError = true
        }
        if (password.all { !it.isDigit() } || password.all { !it.isLetter() }) {
            _state.value = (_state.value as RegistrationState.Main).copy(
                passwordError = RegistrationState.RegistrationError.PASSWORD_CONTENT,
            )
            isError = true
        }
        if (password != passwordRepeat) {
            _state.value = (_state.value as RegistrationState.Main).copy(
                passwordRepeatError = RegistrationState.RegistrationError.PASSWORD_MISMATCH,
            )
            isError = true
        }
        if (isError) {
            return
        }

        viewModelScope.launch(Dispatchers.IO) {
            try {
                val isAvailable = checkLoginAvailableUseCase.execute(login)
                if (!isAvailable) {
                    withContext(Dispatchers.Main) {
                        _state.value = (_state.value as RegistrationState.Main).copy(
                            loginError = RegistrationState.RegistrationError.LOGIN_TAKEN,
                        )
                    }
                    return@launch
                }
                val success = registerUseCase.execute(name, surname, login, password)
                withContext(Dispatchers.Main) {
                    if (!success) {
                        _state.value = RegistrationState.Main(
                            name = name,
                            surname = surname,
                            login = login,
                            password = password,
                            loginError = RegistrationState.RegistrationError.REGISTRATION_FAILED,
                        )
                    } else _action.value = RegistrationAction.NavigateToGymList
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    _state.value = RegistrationState.Main(
                        name = name,
                        surname = surname,
                        login = login,
                        password = password,
                        loginError = RegistrationState.RegistrationError.NETWORK,
                    )
                }
            } finally {
                withContext(Dispatchers.Main) {
                    _state.value = (_state.value as RegistrationState.Main).copy(
                        loading = false
                    )
                }
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