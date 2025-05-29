package ru.hse.gymvision.ui.registration

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import ru.hse.gymvision.domain.exception.LoginAlreadyInUseException
import ru.hse.gymvision.domain.usecase.user.RegisterUseCase

const val LATIN = "abcdefghijklmnopqrstuvwxyz"

class RegistrationViewModel(
    private val registerUseCase: RegisterUseCase,
    private val dispatcherIO: CoroutineDispatcher = Dispatchers.IO,
    private val dispatcherMain: CoroutineDispatcher = Dispatchers.Main,
): ViewModel() {
    private val _state: MutableStateFlow<RegistrationState> = MutableStateFlow(
        RegistrationState.Main()
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
                    event.email,
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
        email: String,
        login: String,
        password: String,
        passwordRepeat: String
    ) {
        if (_state.value !is RegistrationState.Main) {
            return
        }
        _state.value = RegistrationState.Main(
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
            isLoading = true
        )

        var isError = false

        if (name.length < 2 || name.length > 20 || !name.all { it.isLetter() }) {
            _state.value = (_state.value as RegistrationState.Main).copy(
                nameError = if (!name.all { it.isLetter() }) {
                    RegistrationState.RegistrationError.NAME_CONTENT
                } else {
                    RegistrationState.RegistrationError.NAME_LENGTH
                }
            )
            isError = true
        }
        if (surname.length < 2 || surname.length > 20 || !surname.all { it.isLetter() }) {
            _state.value = (_state.value as RegistrationState.Main).copy(
                surnameError = if (!surname.all { it.isLetter() }) {
                    RegistrationState.RegistrationError.SURNAME_CONTENT
                } else {
                    RegistrationState.RegistrationError.SURNAME_LENGTH
                }
            )
            isError = true
        }

        val emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$".toRegex()
        if (!emailRegex.matches(email)) {
            _state.value = (_state.value as RegistrationState.Main).copy(
                emailError = RegistrationState.RegistrationError.EMAIL_CONTENT,
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
            _state.value = (_state.value as RegistrationState.Main).copy(
                isLoading = false
            )
            return
        }

        viewModelScope.launch(dispatcherIO) {
            try {
                registerUseCase.execute(name, surname, email, login, password)
                withContext(dispatcherMain) {
                    _action.value = RegistrationAction.NavigateToGymList
                }
            } catch (e: Exception) {
                withContext(dispatcherMain) {
                    if (e is LoginAlreadyInUseException) {
                        _state.value = (_state.value as RegistrationState.Main).copy(
                            loginError = RegistrationState.RegistrationError.LOGIN_TAKEN,
                            isLoading = false
                        )
                        return@withContext
                    }
                    _state.value = RegistrationState.Main(
                        name = name,
                        surname = surname,
                        login = login,
                        password = password,
                        loginError = RegistrationState.RegistrationError.NETWORK,
                        surnameError = RegistrationState.RegistrationError.NETWORK,
                        emailError = RegistrationState.RegistrationError.NETWORK,
                        nameError = RegistrationState.RegistrationError.NETWORK,
                        passwordError = RegistrationState.RegistrationError.NETWORK,
                        passwordRepeatError = RegistrationState.RegistrationError.NETWORK,
                        isLoading = false
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