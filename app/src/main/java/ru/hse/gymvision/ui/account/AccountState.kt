package ru.hse.gymvision.ui.account

import ru.hse.gymvision.ui.registration.RegistrationState.RegistrationError

sealed class AccountState {
    enum class AccountError {
        NAME_LENGTH,
        SURNAME_LENGTH,
        PASSWORD_LENGTH,
        PASSWORD_CONTENT,
        PASSWORD_MISMATCH,
        PASSWORD_INCORRECT,
        CHANGE_FAILED,
        ACCOUNT_NOT_FOUND,
        NETWORK,
        IDLE
    }
    data object Idle : AccountState()

    data class Main(
        val name: String = "",
        val surname: String = "",
        val login: String = "",
        val password: String = "",
        val isLoading: Boolean = false,
    ) : AccountState()

    data class EditName(
        val name: String = "",
        val surname: String = "",
        val login: String = "",
        val password: String = "",
        val isLoading: Boolean = false,
        val nameError: AccountError = AccountError.IDLE,
        val surnameError: AccountError = AccountError.IDLE,
        val loading: Boolean = false
        ) : AccountState()

    data class ChangePassword(
        val oldPassword: String = "",
        val newPassword: String = "",
        val oldPasswordVisibility: Boolean = false,
        val newPasswordVisibility: Boolean = false,
        val newPasswordRepeatVisibility: Boolean = false,
        val name: String = "",
        val surname: String = "",
        val login: String = "",
        val password: String = "",
        val oldPasswordError: AccountError = AccountError.IDLE,
        val newPasswordError: AccountError = AccountError.IDLE,
        val newPasswordRepeatError: AccountError = AccountError.IDLE,
        val isLoading: Boolean = false
    ) : AccountState()

    data object Loading : AccountState()

    data class Error(
        val error: AccountError = AccountError.IDLE,
    ) : AccountState()

    data class DeletionError(
        val login: String,
    ): AccountState()
}

sealed class AccountEvent {
    data object GetUserInfo: AccountEvent()
    data class SaveNameButtonClicked(
        val name: String,
        val surname: String
    ): AccountEvent()
    data class SavePasswordButtonClicked(
        val newPassword: String,
        val oldPassword: String,
        val realPassword: String
    ): AccountEvent()

    data object ShowOldPasswordButtonClicked : AccountEvent()
    data object ShowNewPasswordButtonClicked : AccountEvent()
    data object ShowNewPasswordRepeatButtonClicked : AccountEvent()
    data object EditNameButtonClicked : AccountEvent()
    data object EditPasswordButtonClicked: AccountEvent()
    data class DeleteAccountButtonClicked(val login: String): AccountEvent()
    data object LogoutButtonClicked: AccountEvent()
    data object Clear: AccountEvent()
}

sealed class AccountAction {
    data object NavigateToAuthorization : AccountAction()
}