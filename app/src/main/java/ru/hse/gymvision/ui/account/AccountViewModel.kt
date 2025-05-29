package ru.hse.gymvision.ui.account

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import ru.hse.gymvision.domain.usecase.user.ChangePasswordUseCase
import ru.hse.gymvision.domain.usecase.user.CheckPasswordUseCase
import ru.hse.gymvision.domain.usecase.user.DeleteUserUseCase
import ru.hse.gymvision.domain.usecase.user.GetUserInfoUseCase
import ru.hse.gymvision.domain.usecase.user.LogoutUseCase
import ru.hse.gymvision.domain.usecase.user.UpdateUserUseCase

class AccountViewModel(
    private val getUserInfoUseCase: GetUserInfoUseCase,
    private val changePasswordUseCase: ChangePasswordUseCase,
    private val updateUserUseCase: UpdateUserUseCase,
    private val deleteUserUseCase: DeleteUserUseCase,
    private val logoutUseCase: LogoutUseCase,
    private val checkPasswordUseCase: CheckPasswordUseCase,
    private val dispatcherIO: CoroutineDispatcher = Dispatchers.IO,
    private val dispatcherMain: CoroutineDispatcher = Dispatchers.Main,
): ViewModel() {
    private val _state: MutableStateFlow<AccountState> = MutableStateFlow(AccountState.Idle)
    val state : MutableStateFlow<AccountState>
        get() = _state
    private val _action = MutableStateFlow<AccountAction?>(null)
    val action : MutableStateFlow<AccountAction?>
        get() = _action

    fun obtainEvent(event: AccountEvent) {
        when (event) {
            is AccountEvent.GetUserInfo -> {
                getUserInfo()
            }
            is AccountEvent.SaveNameButtonClicked -> {
                saveName(
                    event.name,
                    event.surname
                )
            }

            is AccountEvent.SavePasswordButtonClicked -> {
                savePassword(
                    event.newPassword, event.oldPassword, event.newPasswordRepeat
                )
            }

            is AccountEvent.SaveEmailButtonClicked -> {
                saveEmail(
                    event.email
                )
            }

            is AccountEvent.ShowOldPasswordButtonClicked -> {
                showOldPassword()
            }

            is AccountEvent.ShowNewPasswordButtonClicked -> {
                showNewPassword()
            }

            is AccountEvent.ShowNewPasswordRepeatButtonClicked -> {
                showNewPasswordRepeat()
            }

            is AccountEvent.EditNameButtonClicked -> {
                editName()
            }

            is AccountEvent.EditEmailButtonClicked -> {
                editEmail()
            }

            is AccountEvent.EditPasswordButtonClicked -> {
                editPassword()
            }

            is AccountEvent.DeleteAccountButtonClicked -> {
                deleteAccount(event.login)
            }

            is AccountEvent.LogoutButtonClicked -> {
                logout()
            }

            is AccountEvent.Clear -> {
                clear()
            }

            is AccountEvent.ReturnToMain -> {
                returnToMain()
            }
        }
    }

    private fun getUserInfo() {
        _state.value = AccountState.Loading
        viewModelScope.launch(dispatcherIO) {
            try {
                val userInfo = getUserInfoUseCase.execute() ?: run {
                    withContext(dispatcherMain) {
                        _state.value = AccountState.Error(
                            error = AccountState.AccountError.ACCOUNT_NOT_FOUND
                        )
                    }
                    return@launch
                }
                withContext(dispatcherMain) {
                    _state.value = AccountState.Main(
                        name = userInfo.name,
                        surname = userInfo.surname,
                        email = userInfo.email,
                        login = userInfo.login
                    )
                }
            } catch (e: Exception) {
                withContext(dispatcherMain) {
                    _state.value = AccountState.Error(
                        error = AccountState.AccountError.NETWORK_FATAL
                    )
                }
            }
        }
    }

    private fun saveName(name: String, surname: String) {
        if (_state.value !is AccountState.EditName) return
        _state.value = (_state.value as AccountState.EditName).copy(
            isLoading = false,
            nameError = AccountState.AccountError.IDLE,
            surnameError = AccountState.AccountError.IDLE
        )

        var isError = false
        if (name.length < 2 || name.length > 20 || !name.all { it.isLetter() }) {
            _state.value = (_state.value as AccountState.EditName).copy(
                nameError = if (!name.all { it.isLetter() }) {
                    AccountState.AccountError.NAME_CONTENT
                } else {
                    AccountState.AccountError.NAME_LENGTH
                }
            )
            isError = true
        }
        if (surname.length < 2 || surname.length > 20 || !surname.all { it.isLetter() }) {
            _state.value = (_state.value as AccountState.EditName).copy(
                surnameError = if (!surname.all { it.isLetter() }) {
                    AccountState.AccountError.SURNAME_CONTENT
                } else {
                    AccountState.AccountError.SURNAME_LENGTH
                }
            )
            isError = true
        }
        if (isError) {
            _state.value = (_state.value as AccountState.EditName).copy(
                isLoading = false
            )
            return
        }

        _state.value = (_state.value as AccountState.EditName).copy(isLoading = true)
        viewModelScope.launch(dispatcherIO) {
            try {
                updateUserUseCase.execute(
                    name = name,
                    surname = surname,
                    login = (_state.value as AccountState.EditName).login,
                )
                _state.value = AccountState.Main(
                    name = name,
                    surname = surname,
                    email = (_state.value as AccountState.EditName).email,
                    login = (_state.value as AccountState.EditName).login
                )
            } catch (e: Exception) {
                withContext(dispatcherMain) {
                    _state.value = AccountState.EditName(
                        name = name,
                        surname = surname,
                        email = (_state.value as AccountState.EditName).email,
                        login = (_state.value as AccountState.EditName).login,
                        surnameError = AccountState.AccountError.NETWORK,
                        nameError = AccountState.AccountError.NETWORK,
                        isLoading = false
                    )
                }
            }
        }
    }

    private fun savePassword(newPassword: String, oldPassword: String, newPasswordRepeat: String) {
        if (_state.value !is AccountState.ChangePassword) return
        _state.value = (_state.value as AccountState.ChangePassword).copy(
            oldPasswordError = AccountState.AccountError.IDLE,
            newPasswordError = AccountState.AccountError.IDLE,
            newPasswordRepeatError = AccountState.AccountError.IDLE
        )

        if (newPassword.length < 8 || newPassword.length > 20) {
            _state.value = (_state.value as AccountState.ChangePassword).copy(
                newPasswordError = AccountState.AccountError.PASSWORD_LENGTH
            )
            return
        }

        if (newPassword.all { !it.isDigit() } || newPassword.all { !it.isLetter() }) {
            _state.value = (_state.value as AccountState.ChangePassword).copy(
                newPasswordError = AccountState.AccountError.PASSWORD_CONTENT
            )
            return
        }

        if (newPassword != newPasswordRepeat) {
            _state.value = (_state.value as AccountState.ChangePassword).copy(
                newPasswordRepeatError = AccountState.AccountError.PASSWORD_MISMATCH
            )
            return
        }

        _state.value = (_state.value as AccountState.ChangePassword).copy(
            isLoading = true
        )
        viewModelScope.launch(dispatcherIO) {
            try {
                val isOldPasswordCorrect = checkPasswordUseCase.execute(
                    (_state.value as AccountState.ChangePassword).login,
                    oldPassword
                )
                if (!isOldPasswordCorrect) {
                    _state.value = (_state.value as AccountState.ChangePassword).copy(
                        oldPasswordError = AccountState.AccountError.PASSWORD_INCORRECT,
                        isLoading = false
                    )
                    return@launch
                }
                changePasswordUseCase.execute(
                    (_state.value as AccountState.ChangePassword).login,
                    newPassword
                )
                withContext(dispatcherMain) {
                    _state.value = AccountState.Main(
                        name = (_state.value as AccountState.ChangePassword).name,
                        surname = (_state.value as AccountState.ChangePassword).surname,
                        email = (_state.value as AccountState.ChangePassword).email,
                        login = (_state.value as AccountState.ChangePassword).login
                    )
                }
            } catch (e: Exception) {
                withContext(dispatcherMain) {
                    _state.value = (_state.value as AccountState.ChangePassword).copy(
                        isLoading = false,
                        oldPasswordError = AccountState.AccountError.NETWORK,
                        newPasswordError = AccountState.AccountError.NETWORK,
                        newPasswordRepeatError = AccountState.AccountError.NETWORK
                    )
                }
            }
        }
    }

    private fun saveEmail(email: String) {
        if (_state.value !is AccountState.ChangeEmail) return
        _state.value = (_state.value as AccountState.ChangeEmail).copy(
            emailError = AccountState.AccountError.IDLE,
            isLoading = false
        )

        val emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$".toRegex()
        if (!emailRegex.matches(email)) {
            _state.value = (_state.value as AccountState.ChangeEmail).copy(
                emailError = AccountState.AccountError.EMAIL_CONTENT
            )
            return
        }

        _state.value = (_state.value as AccountState.ChangeEmail).copy(isLoading = true)
        viewModelScope.launch(dispatcherIO) {
            try {
                updateUserUseCase.execute(
                    name = (_state.value as AccountState.ChangeEmail).name,
                    surname = (_state.value as AccountState.ChangeEmail).surname,
                    email = email,
                    login = (_state.value as AccountState.ChangeEmail).login,
                )
                withContext(dispatcherMain) {
                    _state.value = AccountState.Main(
                        name = (_state.value as AccountState.ChangeEmail).name,
                        surname = (_state.value as AccountState.ChangeEmail).surname,
                        email = email,
                        login = (_state.value as AccountState.ChangeEmail).login
                    )
                }
            } catch (e: Exception) {
                withContext(dispatcherMain) {
                    _state.value = (_state.value as AccountState.ChangeEmail).copy(
                        emailError = AccountState.AccountError.NETWORK,
                        isLoading = false
                    )
                }
            }
        }
    }

    private fun showOldPassword() {
        if (_state.value is AccountState.ChangePassword) {
            _state.value = (_state.value as AccountState.ChangePassword).copy(
                oldPasswordVisibility =
                    !(_state.value as AccountState.ChangePassword).oldPasswordVisibility)
        }
    }

    private fun showNewPassword() {
        if (_state.value is AccountState.ChangePassword) {
            _state.value = (_state.value as AccountState.ChangePassword).copy(
                newPasswordVisibility =
                    !(_state.value as AccountState.ChangePassword).newPasswordVisibility)
        }
    }

    private fun showNewPasswordRepeat() {
        if (_state.value is AccountState.ChangePassword) {
            _state.value = (_state.value as AccountState.ChangePassword).copy(
                newPasswordRepeatVisibility =
                    !(_state.value as AccountState.ChangePassword).newPasswordRepeatVisibility)
        }
    }

    private fun editName() {
        if (_state.value !is AccountState.Main) {
            return
        }
        _state.value = AccountState.EditName(
            name = (_state.value as AccountState.Main).name,
            surname = (_state.value as AccountState.Main).surname,
            email = (_state.value as AccountState.Main).email,
            login = (_state.value as AccountState.Main).login,
        )
    }

    private fun editPassword() {
        if (_state.value !is AccountState.Main) return
        _state.value = AccountState.ChangePassword(
            name = (_state.value as AccountState.Main).name,
            surname = (_state.value as AccountState.Main).surname,
            email = (_state.value as AccountState.Main).email,
            login = (_state.value as AccountState.Main).login,
        )
    }

    private fun editEmail() {
        if (_state.value !is AccountState.Main) return
        _state.value = AccountState.ChangeEmail(
            name = (_state.value as AccountState.Main).name,
            surname = (_state.value as AccountState.Main).surname,
            email = (_state.value as AccountState.Main).email,
            login = (_state.value as AccountState.Main).login,
        )
    }

    private fun deleteAccount(login: String) {
        if (_state.value !is AccountState.Main) return
        _state.value = (_state.value as AccountState.Main).copy(
            isLoading = true
        )
        viewModelScope.launch(dispatcherIO) {
            try {
                deleteUserUseCase.execute(login)
                withContext(dispatcherMain) {
                    _action.value = AccountAction.NavigateToAuthorization
                }
            } catch (e: Exception) {
                withContext(dispatcherMain) {
                    _state.value = AccountState.DeletionError(
                        login = (state.value as AccountState.Main).login
                    )
                }
            }
        }
    }

    private fun returnToMain() {
        _state.value = when (
            val currentState = _state.value
        ) {
            is AccountState.EditName -> AccountState.Main(
                name = currentState.name,
                surname = currentState.surname,
                email = currentState.email,
                login = currentState.login
            )
            is AccountState.ChangeEmail -> AccountState.Main(
                name = currentState.name,
                surname = currentState.surname,
                email = currentState.email,
                login = currentState.login
            )
            is AccountState.ChangePassword -> AccountState.Main(
                name = currentState.name,
                surname = currentState.surname,
                email = currentState.email,
                login = currentState.login
            )
            else -> AccountState.Idle
        }
    }

    private fun logout() {
        _state.value = AccountState.Loading
        viewModelScope.launch(dispatcherIO) {
            try {
                logoutUseCase.execute()
            } finally {
                withContext(dispatcherMain) {
                    _action.value = AccountAction.NavigateToAuthorization
                }
            }
        }
    }

    private fun clear() {
        _state.value = AccountState.Idle
        _action.value = null
    }
}
