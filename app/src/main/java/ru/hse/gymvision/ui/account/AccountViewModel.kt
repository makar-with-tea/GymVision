package ru.hse.gymvision.ui.account

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
import ru.hse.gymvision.domain.usecase.user.LoginUseCase
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
            isLoading = true
        )
        var isError = false
        if (name.length < 2 || name.length > 20) {
            _state.value = (_state.value as AccountState.EditName).copy(
                nameError = AccountState.AccountError.NAME_LENGTH
            )
            isError = true
        }
        if (surname.length < 2 || surname.length > 20) {
            _state.value = (_state.value as AccountState.EditName).copy(
                surnameError = AccountState.AccountError.SURNAME_LENGTH
            )
            isError = true
        }
        if (isError) {
            _state.value = (_state.value as AccountState.EditName).copy(
                isLoading = false
            )
            return
        }
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
                    login = (_state.value as AccountState.EditName).login
                )
            } catch (e: Exception) {
                withContext(dispatcherMain) {
                    _state.value = AccountState.EditName(
                        name = name,
                        surname = surname,
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
            login = (_state.value as AccountState.Main).login,
        )
    }

    private fun editPassword() {
        if (_state.value !is AccountState.Main) return
        _state.value = AccountState.ChangePassword(
            name = (_state.value as AccountState.Main).name,
            surname = (_state.value as AccountState.Main).surname,
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
